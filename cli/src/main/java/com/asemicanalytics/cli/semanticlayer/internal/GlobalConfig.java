package com.asemicanalytics.cli.semanticlayer.internal;

import java.io.File;
import java.nio.file.Path;
import java.security.ProtectionDomain;

public class GlobalConfig {
  public static String getApiUri() {
    var apiUri = System.getenv("ASEMIC_API_URL");
    if (apiUri != null) {
      return apiUri;
    }
    return "http://34.36.33.251";
  }

  public static String getApiToken() {
    var apiToken = System.getenv("ASEMIC_API_TOKEN");
    if (apiToken != null) {
      return apiToken;
    }
    throw new IllegalStateException(
        "API token not found. Define ASEMIC_API_TOKEN environment variable");
  }

  public static Path getAppIdDir() {
    var appIdDir = System.getenv("ASEMIC_APP_ID_DIR");
    if (appIdDir != null) {
      return Path.of(appIdDir);
    }

    ProtectionDomain protectionDomain = GlobalConfig.class.getProtectionDomain();
    File jarFile = new File(protectionDomain.getCodeSource().getLocation().getPath());
    String jarFilePath = jarFile.getAbsolutePath();
    return Path.of(jarFilePath).getParent();
  }

  public static String getAppId() {
    return getAppIdDir().getFileName().toString();
  }
}
