package com.asemicanalytics.cli.semanticlayer.internal;

import com.asemicanalytics.cli.invoker.ApiClient;
import java.util.Map;

public class ApiClientFactory {
  public static ApiClient create(Map<String, String> headers) {
    var apiClient = new ApiClient();
    apiClient.setBasePath(GlobalConfig.getApiUri());
    apiClient.setBearerToken(GlobalConfig.getApiToken());
    headers.forEach(apiClient::addDefaultHeader);
    return apiClient;
  }

  public static ApiClient create() {
    return create(Map.of());
  }
}
