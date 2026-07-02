package com.asemicanalytics.sequence.endtoend;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.asemicanalytics.core.DataType;
import com.asemicanalytics.core.error.WarehouseException;
import com.asemicanalytics.sequence.endtoend.utils.DatabaseHelper;
import com.asemicanalytics.sql.sql.h2.H2QueryExecutor;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.Test;

public class WarehouseErrorTest {

  @Test
  void failedQuerySurfacesAsWarehouseException() {
    var executor = new H2QueryExecutor(
        DatabaseHelper.USER, DatabaseHelper.PASSWORD, DatabaseHelper.JDBC_URL, 4);
    var future = executor.submit(
        "SELECT * FROM table_that_does_not_exist", List.of(DataType.INTEGER), false);

    ExecutionException thrown = assertThrows(ExecutionException.class, future::get);
    assertTrue(thrown.getCause() instanceof WarehouseException,
        "expected WarehouseException but got " + thrown.getCause());
  }
}
