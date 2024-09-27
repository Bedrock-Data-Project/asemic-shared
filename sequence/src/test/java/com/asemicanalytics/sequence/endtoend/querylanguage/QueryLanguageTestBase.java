package com.asemicanalytics.sequence.endtoend.querylanguage;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.asemicanalytics.core.DataType;
import com.asemicanalytics.core.TableReference;
import com.asemicanalytics.core.column.Column;
import com.asemicanalytics.core.column.Columns;
import com.asemicanalytics.core.logicaltable.action.EventLogicalTable;
import com.asemicanalytics.sequence.SequenceService;
import com.asemicanalytics.sequence.sequence.Step;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class QueryLanguageTestBase {
  protected final Map<String, EventLogicalTable> stepLogicalTables = Map.of(
      "login", ActionLogicalTable("login"),
      "battle", ActionLogicalTable("battle"),
      "transaction", ActionLogicalTable("transaction")
  );

  private EventLogicalTable ActionLogicalTable(String stepName) {
    return new EventLogicalTable(
        stepName, "", Optional.empty(), TableReference.of(stepName),
        new Columns<>(new LinkedHashMap<>(Map.of(
            "date_",
            Column.ofHidden("date_", DataType.DATE).withTag(EventLogicalTable.DATE_COLUMN_TAG),
            "ts", Column.ofHidden("ts", DataType.DATETIME)
                .withTag(EventLogicalTable.TIMESTAMP_COLUMN_TAG),
            "user_id", Column.ofHidden("user_id", DataType.STRING)
                .withTag(EventLogicalTable.ENTITY_ID_COLUMN_TAG)
        ))),
        Map.of(), Optional.empty(), Set.of());
  }

  protected void assertSteps(String query, List<Step> expectedSteps) {
    assertEquals(expectedSteps,
        SequenceService.parseSequence(query, stepLogicalTables).getSteps());
  }
}
