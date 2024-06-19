package com.asemicanalytics.config.configloader.dtomapper;

import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.UserWideColumnsDto;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class UserWideColumnsDtoMapper implements
    Function<List<UserWideColumnsDto>, UserWideColumnsDto> {

  private void merge(UserWideColumnsDto columns, UserWideColumnsDto toMerge) {
    columns.getRegistrationColumns().addAll(toMerge.getRegistrationColumns());
    columns.getUserActionColumns()
        .ifPresent(cols -> cols.addAll(toMerge.getUserActionColumns().orElse(List.of())));
    columns.getTotalColumns()
        .ifPresent(cols -> cols.addAll(toMerge.getTotalColumns().orElse(List.of())));
    columns.getComputedColumns()
        .ifPresent(cols -> cols.addAll(toMerge.getComputedColumns().orElse(List.of())));
  }

  @Override
  public UserWideColumnsDto apply(List<UserWideColumnsDto> columns) {

    UserWideColumnsDto result = new UserWideColumnsDto();
    for (int i = 0; i < columns.size(); i++) {
      merge(result, columns.get(i));
    }

    Set<String> columnIds = new HashSet<>();
    result.getRegistrationColumns().forEach(col -> {
      if (!columnIds.add(col.getColumn().getId())) {
        throw new IllegalArgumentException(
            "Duplicate column id: " + col.getColumn().getId() + " in user wide");
      }
    });
    return result;
  }
}
