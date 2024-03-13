package com.asemicanalytics.sequence.sequence;

import com.asemicanalytics.core.Column;
import com.asemicanalytics.core.TableReference;
import java.util.List;

public record StepTable(
    String name, TableReference tableReference, List<Column> columns,
    String userIdColumn, String dateColumn, String timestampColumn) {
}
