package com.asemicanalytics.sequence.sequence;

import java.util.List;

public interface Step {
  List<String> getStepNames();

  int getIndex();
}
