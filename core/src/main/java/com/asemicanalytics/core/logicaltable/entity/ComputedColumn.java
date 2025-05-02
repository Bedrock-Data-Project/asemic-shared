package com.asemicanalytics.core.logicaltable.entity;

import com.asemicanalytics.core.PlaceholderKeysExtractor;
import com.asemicanalytics.core.column.Column;
import com.asemicanalytics.core.logicaltable.event.EventLogicalTable;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class ComputedColumn extends EntityProperty {
  private final String formula;
  private final Set<String> formulaKeys;
  private final List<ValueMapping> valueMappingList;

  public record ValueMapping(
      Optional<String> from,
      Optional<String> to,
      String newValue) {
  }

  public ComputedColumn(Column column, String formula, List<ValueMapping> valueMappingList) {
    super(column);
    this.formula = formula;
    this.formulaKeys = PlaceholderKeysExtractor.extractKeys(formula);
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

  @Override
  public Set<String> referencedProperties() {
    return formulaKeys;
  }

  @Override
  public Map<EventLogicalTable, Set<String>> referencedEventParameters() {
    return Map.of();
  }
}
