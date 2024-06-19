package com.asemicanalytics.config.configparser.yaml;

public interface YamlFileLoader {
  <T> T load(String content, Class<T> valueType);
}
