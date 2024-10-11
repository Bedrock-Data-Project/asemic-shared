package com.asemicanalytics.sql.sql.executor;

import static com.asemicanalytics.sql.sql.builder.tokens.QueryFactory.identifier;
import static com.asemicanalytics.sql.sql.builder.tokens.QueryFactory.select;

import brave.Span;
import brave.Tracer;
import brave.Tracing;
import com.asemicanalytics.core.DataType;
import com.asemicanalytics.core.Dialect;
import com.asemicanalytics.core.SqlQueryExecutor;
import com.asemicanalytics.core.SqlResult;
import com.asemicanalytics.core.TableReference;
import com.asemicanalytics.core.column.Column;
import com.asemicanalytics.sql.sql.builder.tokens.QueryBuilder;
import com.asemicanalytics.sql.sql.builder.tokens.QueryFactory;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public abstract class ThreadPoolSqlQueryExecutor implements SqlQueryExecutor {

  protected final Executor executor;
  private final Dialect dialect;

  protected ThreadPoolSqlQueryExecutor(int maxWorkers, Dialect dialect) {
    executor = new ThreadPoolExecutor(
        maxWorkers,
        maxWorkers,
        0,
        TimeUnit.MILLISECONDS,
        new SynchronousQueue<>(),
        Executors.defaultThreadFactory(),
        new ThreadPoolExecutor.AbortPolicy()
    );
    this.dialect = dialect;
  }

  public Dialect getDialect() {
    return dialect;
  }

  protected <T> CompletableFuture<T> submit(Callable<T> callable, String operationName) {
    var tracer = Tracing.currentTracer();
    var parentSpan = tracer.currentSpan();

    return CompletableFuture.supplyAsync(() -> {
      Span newSpan;
      if (parentSpan == null) {
        newSpan = tracer.nextSpan().name(operationName);
      } else {
        newSpan = tracer.newChild(parentSpan.context()).name(operationName);
      }

      try (Tracer.SpanInScope ws = tracer.withSpanInScope(newSpan.start())) {
        newSpan.tag("executorType", getClass().getSimpleName());
        return callable.call();
      } catch (Exception e) {
        throw new RuntimeException(e);
      } finally {
        newSpan.finish();
      }
    }, executor);
  }

  public CompletableFuture<SqlResult> submit(String sql, List<DataType> dataTypes, boolean dryRun) {
    return submit(() -> executeQuery(sql, dataTypes, dryRun), "SqlQueryExecutor.submit");
  }

  public CompletableFuture<List<Column>> submitGetColumns(TableReference table) {
    return submit(() -> getColumns(table), "SqlQueryExecutor.submitGetColumns");
  }

  public CompletableFuture<List<String>> submitGetTables(String schema) {
    return submit(() -> getTables(schema), "SqlQueryExecutor.submitGetTables");
  }

  public CompletableFuture<SqlResult> submitExecuteDdl(String sql) {
    return submit(() -> executeDdl(sql), "SqlQueryExecutor.submitExecuteDdl");
  }

  protected abstract SqlResult executeQuery(String sql, List<DataType> dataTypes, boolean dryRun)
      throws InterruptedException;

  protected abstract List<Column> getColumns(TableReference table) throws InterruptedException;

  protected abstract List<String> getTables(String schema) throws InterruptedException;

  protected abstract SqlResult executeDdl(String sql) throws InterruptedException;

  @Override
  public CompletableFuture<SqlResult> submitGetCountByPartition(TableReference table,
                                                                Column partitionColumn,
                                                                String partitionFrom,
                                                                String partitionTo) {
    var tbl = QueryFactory.table(table);
    var selectStatement =
        select()
            .select(
                tbl.column(partitionColumn.getId()).withAlias("partition_value"),
                identifier("COUNT(*)").withAlias("count")
            )
            .from(tbl)
            .and(tbl.column(partitionColumn.getId()).condition(
                "between", List.of(partitionFrom, partitionTo), partitionColumn.getDataType()
            ))
            .groupBy(
                tbl.column(partitionColumn.getId())
            );
    QueryBuilder query = new QueryBuilder();
    query.select(selectStatement);
    return submit(() -> executeQuery(query.render(dialect),
            List.of(DataType.STRING, DataType.INTEGER), false),
        "SqlQueryExecutor.submitGetCountByPartition");
  }
}
