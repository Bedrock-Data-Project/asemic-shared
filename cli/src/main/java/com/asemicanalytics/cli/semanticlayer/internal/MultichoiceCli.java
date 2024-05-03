package com.asemicanalytics.cli.semanticlayer.internal;

import java.util.List;

public class MultichoiceCli {
  private final String header;
  private final List<String> choices;

  public MultichoiceCli(String header, List<String> choices) {
    this.header = header;
    this.choices = choices;
  }

  public int choose() {
    System.out.println(header);
    for (int i = 0; i < choices.size(); i++) {
      System.out.printf(" [%d] %s%n", i + 1, choices.get(i));
    }
    // TODO validation and retry
    System.out.print("Enter a numeric choice: ");
    return Integer.parseInt(System.console().readLine()) - 1;
  }
}
