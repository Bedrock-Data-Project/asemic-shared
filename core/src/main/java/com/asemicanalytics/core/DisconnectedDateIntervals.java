package com.asemicanalytics.core;

import com.google.common.collect.BoundType;
import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class DisconnectedDateIntervals {
  private final RangeSet<LocalDate> intervals;

  private DisconnectedDateIntervals(RangeSet<LocalDate> intervals) {
    this.intervals = intervals;
  }

  public DisconnectedDateIntervals() {
    this.intervals = TreeRangeSet.create();
  }

  public static DisconnectedDateIntervals ofDates(TreeSet<LocalDate> dates) {
    var disconnectedDateIntervals = new DisconnectedDateIntervals();
    for (var date : dates) {
      if (!disconnectedDateIntervals.intervals.isEmpty()
          && disconnectedDateIntervals.intervals.span().upperEndpoint().equals(date.minusDays(1))) {
        disconnectedDateIntervals.intervals.add(Range.closed(date.minusDays(1), date));
      } else {
        disconnectedDateIntervals.intervals.add(Range.closed(date, date));
      }
    }
    return disconnectedDateIntervals;
  }

  public static DisconnectedDateIntervals ofIntervals(List<DateInterval> intervals) {
    var disconnectedDateIntervals = new DisconnectedDateIntervals();
    for (var interval : intervals) {
      disconnectedDateIntervals.intervals.add(Range.closed(interval.from(), interval.to()));
    }
    return disconnectedDateIntervals;
  }

  public DisconnectedDateIntervals intersection(DateInterval interval) {
    var intersection = intervals.subRangeSet(Range.closed(interval.from(), interval.to()));
    return new DisconnectedDateIntervals(intersection);
  }

  public DisconnectedDateIntervals intersection(DisconnectedDateIntervals intervals) {
    var intersection = this;
    for (var range : intervals.intervals.asRanges()) {
      intersection =
          intersection.intersection(new DateInterval(range.lowerEndpoint(), range.upperEndpoint()));
    }
    return intersection;
  }

  public void remove(DateInterval interval) {
    intervals.remove(Range.closed(interval.from(), interval.to()));
    normalize();
  }

  public void remove(DisconnectedDateIntervals intervals) {
    this.intervals.removeAll(intervals.intervals);
    normalize();
  }

  private void normalize() {
    DisconnectedDateIntervals normalizedIntervals = new DisconnectedDateIntervals();
    for (var range : intervals.asRanges()) {
      var from = range.lowerEndpoint();
      var to = range.upperEndpoint();

      if (range.lowerBoundType() == BoundType.OPEN) {
        from = from.plusDays(1);
      }
      if (range.upperBoundType() == BoundType.OPEN) {
        to = to.minusDays(1);
      }

      normalizedIntervals.intervals.add(Range.closed(from, to));
    }
    intervals.clear();
    intervals.addAll(normalizedIntervals.intervals);
  }

  public List<DateInterval> intervals() {
    var result = new ArrayList<DateInterval>();
    for (var range : intervals.asRanges()) {
      var lower = range.lowerEndpoint();
      var upper = range.upperEndpoint();

      result.add(new DateInterval(lower, upper));
    }
    return result;
  }

  public DisconnectedDateIntervals plusDays(long days) {
    var newIntervals = new DisconnectedDateIntervals();
    for (var range : intervals.asRanges()) {
      newIntervals.intervals.add(Range.closed(
          range.lowerEndpoint().plusDays(days),
          range.upperEndpoint().plusDays(days)));
    }
    return newIntervals;
  }

  public DisconnectedDateIntervals shiftDays(long fromDays, long toDays) {
    if (intervals.asRanges().size() != 1) {
      throw new IllegalArgumentException("Can only shift one interval");
    }
    var newIntervals = new DisconnectedDateIntervals();
    newIntervals.intervals.add(Range.closed(
        intervals.span().lowerEndpoint().plusDays(fromDays),
        intervals.span().upperEndpoint().plusDays(toDays)));
    return newIntervals;
  }

  public DisconnectedDateIntervals clone() {
    return new DisconnectedDateIntervals(TreeRangeSet.create(intervals));
  }

  public boolean contains(LocalDate date) {
    return intervals.contains(date);
  }

  public void add(LocalDate date) {
    if (contains(date.minusDays(1))) {
      intervals.add(Range.closed(date.minusDays(1), date));
    } else {
      intervals.add(Range.closed(date, date));
    }
  }

  public DateInterval span() {
    return new DateInterval(intervals.span().lowerEndpoint(), intervals.span().upperEndpoint());
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    DisconnectedDateIntervals that = (DisconnectedDateIntervals) obj;
    return intervals.equals(that.intervals);
  }
}
