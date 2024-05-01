package com.asemicanalytics.cli.semanticlayer.internal;

import com.asemicanalytics.cli.invoker.ApiClient;
import java.net.URI;
import java.util.Map;

public class ApiClientFactory {
  public static ApiClient create(Map<String, String> headers) {
    URI baseUri = URI.create(GlobalConfig.getApiUri());

    return new ApiClient()
        .setScheme(baseUri.getScheme())
        .setHost(baseUri.getHost())
        .setPort(baseUri.getPort())
        .setRequestInterceptor(request -> {
          request.header("Authorization", "Bearer " + GlobalConfig.getApiToken());
          headers.forEach(request::header);
        });
  }

  public static ApiClient create() {
    return create(Map.of());
  }
}
