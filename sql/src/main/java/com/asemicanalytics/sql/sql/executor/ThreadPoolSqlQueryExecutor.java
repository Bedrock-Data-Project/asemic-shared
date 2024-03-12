package com.asemicanalytics.sql.sql.executor;

import com.asemicanalytics.core.Column;
import com.asemicanalytics.core.DataType;
import com.asemicanalytics.core.Dialect;
import com.asemicanalytics.core.SqlQueryExecutor;
import com.asemicanalytics.core.SqlResult;
import com.asemicanalytics.core.TableReference;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ThreadPoolSqlQueryExecutor implements SqlQueryExecutor {

  private static final Tracer tracer = GlobalOpenTelemetry.getTracer("query-engine");
  private final Executor executor;
  private final Dialect dialect;
  Logger logger = LoggerFactory.getLogger(ThreadPoolSqlQueryExecutor.class);


  protected ThreadPoolSqlQueryExecutor(int maxWorkers, Dialect dialect) {
    executor = Context.taskWrapping(new ThreadPoolExecutor(
        maxWorkers,
        maxWorkers,
        0,
        TimeUnit.MILLISECONDS,
        new SynchronousQueue<>(),
        Executors.defaultThreadFactory(),
        new ThreadPoolExecutor.AbortPolicy()
    ));
    this.dialect = dialect;
  }

  public Dialect getDialect() {
    return dialect;
  }

  protected <T> CompletableFuture<T> submit(Callable<T> callable) {
    return CompletableFuture.supplyAsync(() -> {
      Span span = tracer.spanBuilder("SQL query").startSpan();
      try (Scope scope = span.makeCurrent()) {
        return callable.call();
      } catch (Exception e) {
        throw new RuntimeException(e);
      } finally {
        span.end();
      }
    }, executor);
  }

  public CompletableFuture<SqlResult> submit(String sql, List<DataType> dataTypes, boolean dryRun) {
    return submit(() -> executeQuery(sql, dataTypes, dryRun));
  }

  public CompletableFuture<List<Column>> submitGetColumns(TableReference table) {
    return submit(() -> getColumns(table));

  }

  protected abstract SqlResult executeQuery(String sql, List<DataType> dataTypes, boolean dryRun)
      throws InterruptedException;

  protected abstract List<Column> getColumns(TableReference table) throws InterruptedException;
}
