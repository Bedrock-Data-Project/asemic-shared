package com.asemicanalytics.sequence.endtoend.querylanguage;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.asemicanalytics.core.DataType;
import com.asemicanalytics.core.TableReference;
import com.asemicanalytics.core.TimeGrains;
import com.asemicanalytics.core.column.Column;
import com.asemicanalytics.core.datasource.UserActionDatasource;
import com.asemicanalytics.sequence.SequenceService;
import com.asemicanalytics.sequence.sequence.Step;
import com.asemicanalytics.sql.sql.builder.tablelike.Table;
import com.asemicanalytics.sql.sql.columnsource.ColumnSource;
import com.asemicanalytics.sql.sql.columnsource.TableColumnSource;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class QueryLanguageTestBase {
  protected final Map<String, ColumnSource> stepColumnSources = Map.of(
      "login", columnSource("login"),
      "battle", columnSource("battle"),
      "transaction", columnSource("transaction")
  );

  private ColumnSource columnSource(String stepName) {
    return new TableColumnSource(new UserActionDatasource(
        stepName, "", Optional.empty(), TableReference.of(stepName),
        new LinkedHashMap<>(Map.of(
            "date_", Column.ofHidden("date_", DataType.DATE),
            "ts", Column.ofHidden("ts", DataType.DATETIME),
            "user_id", Column.ofHidden("user_id", DataType.STRING)
        )),
        Map.of(), TimeGrains.min15,
        "date_", "ts", "user_id"),
        new Table(TableReference.of(stepName)));
  }

  protected void assertSteps(String query, List<Step> expectedSteps) {
    assertEquals(expectedSteps,
        SequenceService.parseSequence(query, stepColumnSources).getSteps());
  }
}
