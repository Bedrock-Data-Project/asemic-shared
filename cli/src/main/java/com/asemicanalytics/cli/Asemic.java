package com.asemicanalytics.cli;

import com.asemicanalytics.cli.semanticlayer.SemanticLayerCommand;
import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(name = "asemic", mixinStandardHelpOptions = true, subcommands = {
    SemanticLayerCommand.class})
public class Asemic {
  public static void main(String[] args) {
    int rc = new CommandLine(new Asemic()).execute(args);
    System.exit(rc);
  }
}
