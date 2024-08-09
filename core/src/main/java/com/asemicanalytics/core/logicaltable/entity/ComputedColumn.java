package com.asemicanalytics.core.logicaltable.entity;

import com.asemicanalytics.core.column.Column;
import java.util.List;
import java.util.Optional;

public class ComputedColumn extends EntityProperty {
  private final String formula;
  private final List<ValueMapping> valueMappingList;

  public record ValueMapping(
      Optional<String> from,
      Optional<String> to,
      String newValue) {
  }

  public ComputedColumn(Column column, String formula, List<ValueMapping> valueMappingList) {
    super(column);
    this.formula = formula;
    this.valueMappingList = valueMappingList;
  }

  public ComputedColumn(Column column, String formula) {
    this(column, formula, List.of());
  }

  public String getFormula() {
    return formula;
  }

  public List<ValueMapping> getValueMappings() {
    return valueMappingList;
  }

  @Override
  public EntityPropertyType getType() {
    return EntityPropertyType.COMPUTED;
  }
}
