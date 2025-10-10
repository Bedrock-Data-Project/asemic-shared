package com.asemicanalytics.core;

import com.asemicanalytics.core.dataframe.Dataframe;
import java.time.Duration;

public record SqlResult(
    Dataframe dataframe,
    String sql,
    Duration duration,
    Boolean cached,
    long bytesProcessed) {
}
