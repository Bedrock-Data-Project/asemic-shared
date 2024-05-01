package com.asemicanalytics.cli.semanticlayer;

import com.asemicanalytics.cli.api.DatasourcesControllerApi;
import com.asemicanalytics.cli.invoker.ApiException;
import com.asemicanalytics.cli.semanticlayer.internal.ApiClientFactory;
import com.asemicanalytics.cli.semanticlayer.internal.GlobalConfig;
import com.asemicanalytics.cli.semanticlayer.internal.ZipUtils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import picocli.CommandLine;

@CommandLine.Command(name = "validate", mixinStandardHelpOptions = true)
public class PushCommand implements Runnable {

  public static void push(String version) throws IOException, ApiException {
    Path zipFilePath = null;
    try {
      zipFilePath = ZipUtils.zipDirectory(GlobalConfig.getAppIdDir());

      Map<String, String> headers = version != null
          ? Map.of("AppConfigVersion", version)
          : Map.of();
      var api = new DatasourcesControllerApi(ApiClientFactory.create(headers));
      api.submitAppConfig(GlobalConfig.getAppId(), zipFilePath.toFile());
    } finally {
      if (zipFilePath != null) {
        Files.delete(zipFilePath);
      }
    }
  }

  public static void push() throws IOException, ApiException {
    push(null);
  }

  @Override
  public void run() {
    try {
      push();
    } catch (IOException | ApiException e) {
      throw new RuntimeException(e);
    }
  }
}
