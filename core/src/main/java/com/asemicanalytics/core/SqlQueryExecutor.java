package com.asemicanalytics.core;


import com.asemicanalytics.core.column.Column;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface SqlQueryExecutor {
  Dialect getDialect();

  CompletableFuture<SqlResult> submit(String sql, List<DataType> dataTypes, boolean dryRun);

  void executeDdl(String sql);

  CompletableFuture<List<Column>> submitGetColumns(TableReference table);

  CompletableFuture<DatetimeInterval> submitTableFreshness(TableReference table, String id);
}
