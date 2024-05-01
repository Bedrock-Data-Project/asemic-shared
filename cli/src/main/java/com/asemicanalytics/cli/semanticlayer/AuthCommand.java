package com.asemicanalytics.cli.semanticlayer;

import picocli.CommandLine;

@CommandLine.Command(name = "auth", mixinStandardHelpOptions = true)
public class AuthCommand implements Runnable {
  @Override
  public void run() {
    System.out.println("Authenticating...");
  }
}
