package com.asemicanalytics.config.parser.yaml;

public interface YamlFileLoader {
  <T> T load(String content, Class<T> valueType);
}
