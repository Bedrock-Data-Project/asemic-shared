package com.asemicanalytics.cli.semanticlayer;

import com.asemicanalytics.cli.api.ConfigureDatasourcesControllerApi;
import com.asemicanalytics.cli.invoker.ApiException;
import com.asemicanalytics.cli.model.DatabaseDto;
import com.asemicanalytics.cli.semanticlayer.internal.ApiClientFactory;
import com.asemicanalytics.cli.semanticlayer.internal.GlobalConfig;
import com.asemicanalytics.cli.semanticlayer.internal.MultichoiceCli;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import picocli.CommandLine;

@CommandLine.Command(name = "auth", mixinStandardHelpOptions = true)
public class AuthCommand implements Runnable {

  private DatabaseDto fromBigQuery() {
    String projectId = System.console().readLine("Enter your google billing project ID: ");
    String serviceAccountPath = System.console().readLine(
        "Enter path to your service account key "
            + "(Should be generated on google cloud console from a service account): ");
    try {
      String serviceAccount = Files.readString(Path.of(serviceAccountPath));
      String encodedServiceAccount = Base64.getEncoder()
          .encodeToString(serviceAccount.getBytes(StandardCharsets.UTF_8));

      return new DatabaseDto()
          .databaseType("bigquery")
          .databaseConfig(Map.of(
              "gcp_project_id", projectId,
              "service_account_key", encodedServiceAccount));
    } catch (IOException e) {
      throw new IllegalArgumentException(e);
    }
  }

  private DatabaseDto fromSnowflake() {
    String user = System.console().readLine("Enter username: ");
    String password = new String(System.console().readPassword("Enter password: "));
    String jdbcUrl = System.console().readLine("Enter JDBC URL: ");

    return new DatabaseDto()
        .databaseType("snowflake")
        .databaseConfig(Map.of(
            "user", user,
            "password", password,
            "jdbc_url", jdbcUrl));

  }

  @Override
  public void run() {
    var datasourcesApi = new ConfigureDatasourcesControllerApi(ApiClientFactory.create());

    int choice = new MultichoiceCli("Choose a database type",
        List.of("BigQuery", "Snowflake"))
        .choose();
    var databaseDto = switch (choice) {
      case 0 -> fromBigQuery();
      case 1 -> fromSnowflake();
      default -> throw new IllegalArgumentException("Invalid choice");
    };

    try {
      datasourcesApi.submitAppDbAuth(GlobalConfig.getAppId(), databaseDto);
      System.out.println("@|fg(green) OK|@");
    } catch (ApiException e) {
      throw new RuntimeException(e);
    }

  }
}
