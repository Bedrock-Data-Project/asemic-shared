package com.asemicanalytics.cli.semanticlayer;

import picocli.CommandLine;

@CommandLine.Command(name = "semantic-layer", mixinStandardHelpOptions = true, subcommands = {
    AuthCommand.class,
    ValidateCommand.class
})
public class SemanticLayerCommand {
}
