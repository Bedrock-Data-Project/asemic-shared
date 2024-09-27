package com.asemicanalytics.core.logicaltable.action;

import com.asemicanalytics.core.TableReference;
import com.asemicanalytics.core.column.Column;
import com.asemicanalytics.core.column.Columns;
import com.asemicanalytics.core.kpi.Kpi;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class PaymentTransactionEventLogicalTable extends EventLogicalTable {
  public static final String TAG = "payment_transaction_action";

  public static final String TRANSACTION_AMOUNT_COLUMN_TAG = "transaction_amount_column";

  private final String transactionAmountColumn;

  public PaymentTransactionEventLogicalTable(String id, String label,
                                             Optional<String> description,
                                             TableReference table,
                                             Columns columns,
                                             Map<String, Kpi> kpis, Optional<String> where,
                                             Set<String> tags) {
    super(id, label, description, table, columns, kpis, where, tags);
    this.transactionAmountColumn = columns.getColumnIdByTag(TRANSACTION_AMOUNT_COLUMN_TAG);
  }

  public Column getTransactionAmountColumn() {
    return columns.column(transactionAmountColumn);
  }
}
