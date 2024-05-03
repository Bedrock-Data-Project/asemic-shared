package com.asemicanalytics.cli.semanticlayer;

import com.asemicanalytics.cli.api.DatasourcesControllerApi;
import com.asemicanalytics.cli.invoker.ApiException;
import com.asemicanalytics.cli.semanticlayer.internal.ApiClientFactory;
import com.asemicanalytics.cli.semanticlayer.internal.GlobalConfig;
import com.asemicanalytics.cli.semanticlayer.internal.ZipUtils;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import picocli.CommandLine;

@CommandLine.Command(name = "pull", mixinStandardHelpOptions = true)
public class PullCommand implements Runnable {

  @Override
  public void run() {
    var api = new DatasourcesControllerApi(ApiClientFactory.create());
    try {
      File configZipped = api.getConfig(GlobalConfig.getAppId());
      deleteDirContents(GlobalConfig.getAppIdDir());
      ZipUtils.unzipToDirectory(configZipped.toPath(), GlobalConfig.getAppIdDir());
      System.out.println("@|fg(green) OK|@");
    } catch (ApiException | IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static void deleteDirContents(Path dir) throws IOException {
    Files.walkFileTree(dir, new SimpleFileVisitor<>() {
      @Override
      public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        Files.delete(file);
        return FileVisitResult.CONTINUE;
      }

      @Override
      public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        Files.delete(dir);
        return FileVisitResult.CONTINUE;
      }
    });
  }
}
