package com.asemicanalytics.core.logicaltable;

import com.asemicanalytics.core.TableReference;

public record MaterializedIndexTable(
    TableReference table,
    String filterExpression,
    int cost
) {
}
