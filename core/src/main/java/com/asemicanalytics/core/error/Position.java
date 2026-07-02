package com.asemicanalytics.core.error;

/** A 1-based source position (line and column) within a DSL string. */
public record Position(int line, int column) {
}
