package com.asemicanalytics.cli.semanticlayer.internal;

import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipUtils {
  public static Path zipDirectory(Path sourceDir) throws IOException {
    var zipFile = Files.createTempFile(null, ".zip");
    try (ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFile.toFile()))) {
      Files.walkFileTree(sourceDir, new SimpleFileVisitor<>() {
        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
          Path targetFile = sourceDir.relativize(file);
          zipOut.putNextEntry(new ZipEntry(targetFile.toString()));
          Files.copy(file, zipOut);
          zipOut.closeEntry();
          return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
            throws IOException {
          Path targetDir = sourceDir.relativize(dir);
          if (!targetDir.toString().isEmpty()) {
            zipOut.putNextEntry(new ZipEntry(targetDir + "/"));
            zipOut.closeEntry();
          }
          return FileVisitResult.CONTINUE;
        }
      });
    }
    return zipFile;
  }

  public static void unzipToDirectory(Path zipPath, Path unzipDestination) throws IOException {
    try (ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipPath.toFile()))) {
      byte[] buffer = new byte[1024];
      ZipEntry zipEntry = zipIn.getNextEntry();
      while (zipEntry != null) {
        Path filePath = unzipDestination.resolve(zipEntry.getName());
        if (zipEntry.isDirectory()) {
          Files.createDirectories(filePath);
        } else {
          Files.createDirectories(filePath.getParent());
          try (BufferedOutputStream bos = new BufferedOutputStream(
              new FileOutputStream(filePath.toFile()))) {
            int len;
            while ((len = zipIn.read(buffer)) > 0) {
              bos.write(buffer, 0, len);
            }
          }
        }
        zipEntry = zipIn.getNextEntry();
      }
    }
  }
}
