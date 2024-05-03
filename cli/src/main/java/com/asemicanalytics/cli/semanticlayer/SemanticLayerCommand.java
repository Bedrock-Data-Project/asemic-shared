package com.asemicanalytics.cli.semanticlayer;

import picocli.CommandLine;

@CommandLine.Command(name = "semantic-layer", mixinStandardHelpOptions = true, subcommands = {
    AuthCommand.class,
    ValidateCommand.class,
    PushCommand.class,
    PullCommand.class
})
public class SemanticLayerCommand {
}
