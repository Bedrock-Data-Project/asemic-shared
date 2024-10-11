package com.asemicanalytics.core;


import com.asemicanalytics.core.column.Column;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface SqlQueryExecutor {
  Dialect getDialect();

  CompletableFuture<SqlResult> submit(String sql, List<DataType> dataTypes, boolean dryRun);

  CompletableFuture<SqlResult> submitExecuteDdl(String sql);

  CompletableFuture<List<Column>> submitGetColumns(TableReference table);

  CompletableFuture<List<String>> submitGetTables(String schema);

  CompletableFuture<DatetimeInterval> submitTableFreshness(TableReference table, String id);

  CompletableFuture<SqlResult> submitGetCountByPartition(TableReference table,
                                                         Column partitionColumn,
                                                         String partitionFrom, String partitionTo)
      throws InterruptedException;
}
