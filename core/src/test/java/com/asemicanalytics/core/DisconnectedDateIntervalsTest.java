package com.asemicanalytics.core;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.junit.jupiter.api.Test;

class DisconnectedDateIntervalsTest {
  @Test
  void givenAListOfPoints_shouldCreateDisconnectedDateIntervals() {
    DisconnectedDateIntervals disconnectedDateIntervals = DisconnectedDateIntervals.ofDates(
        new TreeSet<>(Set.of(
            LocalDate.of(2021, 1, 1),
            LocalDate.of(2021, 1, 2),
            LocalDate.of(2021, 1, 3),
            LocalDate.of(2021, 1, 6),
            LocalDate.of(2021, 1, 7),
            LocalDate.of(2021, 1, 14),
            LocalDate.of(2021, 1, 18))));

    assertEquals(List.of(
        new DateInterval(LocalDate.of(2021, 1, 1), LocalDate.of(2021, 1, 3)),
        new DateInterval(LocalDate.of(2021, 1, 6), LocalDate.of(2021, 1, 7)),
        new DateInterval(LocalDate.of(2021, 1, 14), LocalDate.of(2021, 1, 14)),
        new DateInterval(LocalDate.of(2021, 1, 18), LocalDate.of(2021, 1, 18))
    ), disconnectedDateIntervals.intervals());
  }

  @Test
  void givenAListOfPoints_shouldReturnEmptyList_whenNoIntersection() {
    DisconnectedDateIntervals disconnectedDateIntervals = DisconnectedDateIntervals.ofDates(
        new TreeSet<>(Set.of(
            LocalDate.of(2021, 1, 1),
            LocalDate.of(2021, 1, 2),
            LocalDate.of(2021, 1, 3),
            LocalDate.of(2021, 1, 6),
            LocalDate.of(2021, 1, 7),
            LocalDate.of(2021, 1, 14),
            LocalDate.of(2021, 1, 18))));

    assertEquals(List.of(), disconnectedDateIntervals.intersection(new DateInterval(
            LocalDate.of(2021, 1, 4),
            LocalDate.of(2021, 1, 5)))
        .intervals());
  }

  @Test
  void givenAListOfPoints_shouldReturnInterval_whenIntersectionInInterval() {
    DisconnectedDateIntervals disconnectedDateIntervals = DisconnectedDateIntervals.ofDates(
        new TreeSet<>(Set.of(
            LocalDate.of(2021, 1, 1),
            LocalDate.of(2021, 1, 2),
            LocalDate.of(2021, 1, 3),
            LocalDate.of(2021, 1, 6),
            LocalDate.of(2021, 1, 7),
            LocalDate.of(2021, 1, 14),
            LocalDate.of(2021, 1, 18))));

    assertEquals(List.of(
        new DateInterval(LocalDate.of(2021, 1, 2),
            LocalDate.of(2021, 1, 3))
    ), disconnectedDateIntervals.intersection(new DateInterval(
            LocalDate.of(2021, 1, 2),
            LocalDate.of(2021, 1, 3)))
        .intervals());
  }

  @Test
  void givenAListOfPoints_shouldReturnInterval_whenIntersectionInManyIntervals() {
    DisconnectedDateIntervals disconnectedDateIntervals = DisconnectedDateIntervals.ofDates(
        new TreeSet<>(Set.of(
            LocalDate.of(2021, 1, 1),
            LocalDate.of(2021, 1, 2),
            LocalDate.of(2021, 1, 3),
            LocalDate.of(2021, 1, 6),
            LocalDate.of(2021, 1, 7),
            LocalDate.of(2021, 1, 14),
            LocalDate.of(2021, 1, 18))));

    assertEquals(List.of(
        new DateInterval(LocalDate.of(2021, 1, 2),
            LocalDate.of(2021, 1, 3)),
        new DateInterval(LocalDate.of(2021, 1, 6),
            LocalDate.of(2021, 1, 7)),
        new DateInterval(LocalDate.of(2021, 1, 14),
            LocalDate.of(2021, 1, 14))
    ), disconnectedDateIntervals.intersection(new DateInterval(
            LocalDate.of(2021, 1, 2),
            LocalDate.of(2021, 1, 14)))
        .intervals());
  }

  @Test
  void givenAListOfPoints_shouldReturnInterval_whenRemoveADate() {
    DisconnectedDateIntervals disconnectedDateIntervals = DisconnectedDateIntervals.ofDates(
        new TreeSet<>(Set.of(
            LocalDate.of(2021, 1, 1),
            LocalDate.of(2021, 1, 2),
            LocalDate.of(2021, 1, 3),
            LocalDate.of(2021, 1, 6),
            LocalDate.of(2021, 1, 7),
            LocalDate.of(2021, 1, 14),
            LocalDate.of(2021, 1, 18))));
    disconnectedDateIntervals.remove(new DateInterval(
        LocalDate.of(2021, 1, 2),
        LocalDate.of(2021, 1, 2)
    ));

    assertEquals(List.of(
        new DateInterval(LocalDate.of(2021, 1, 1), LocalDate.of(2021, 1, 1)),
        new DateInterval(LocalDate.of(2021, 1, 3), LocalDate.of(2021, 1, 3)),
        new DateInterval(LocalDate.of(2021, 1, 6), LocalDate.of(2021, 1, 7)),
        new DateInterval(LocalDate.of(2021, 1, 14), LocalDate.of(2021, 1, 14)),
        new DateInterval(LocalDate.of(2021, 1, 18), LocalDate.of(2021, 1, 18))
    ), disconnectedDateIntervals.intervals());
  }

  @Test
  void givenAListOfPoints_shouldReturnInterval_whenRemoveARange() {
    DisconnectedDateIntervals disconnectedDateIntervals = DisconnectedDateIntervals.ofDates(
        new TreeSet<>(Set.of(
            LocalDate.of(2021, 1, 1),
            LocalDate.of(2021, 1, 2),
            LocalDate.of(2021, 1, 3),
            LocalDate.of(2021, 1, 6),
            LocalDate.of(2021, 1, 7),
            LocalDate.of(2021, 1, 14),
            LocalDate.of(2021, 1, 18))));
    disconnectedDateIntervals.remove(new DateInterval(
        LocalDate.of(2021, 1, 2),
        LocalDate.of(2021, 1, 7)
    ));

    assertEquals(List.of(
        new DateInterval(LocalDate.of(2021, 1, 1), LocalDate.of(2021, 1, 1)),
        new DateInterval(LocalDate.of(2021, 1, 14), LocalDate.of(2021, 1, 14)),
        new DateInterval(LocalDate.of(2021, 1, 18), LocalDate.of(2021, 1, 18))
    ), disconnectedDateIntervals.intervals());
  }

  @Test
  void givenAListOfPoints_shouldReturnInterval_whenRemoveARangeContainingAll() {
    DisconnectedDateIntervals disconnectedDateIntervals = DisconnectedDateIntervals.ofDates(
        new TreeSet<>(Set.of(
            LocalDate.of(2021, 1, 1),
            LocalDate.of(2021, 1, 2),
            LocalDate.of(2021, 1, 3),
            LocalDate.of(2021, 1, 6),
            LocalDate.of(2021, 1, 7),
            LocalDate.of(2021, 1, 14),
            LocalDate.of(2021, 1, 18))));
    disconnectedDateIntervals.remove(new DateInterval(
        LocalDate.of(2021, 1, 1),
        LocalDate.of(2021, 1, 18)
    ));

    assertEquals(List.of(), disconnectedDateIntervals.intervals());
  }

  @Test
  void givenAListOfPoints_shouldReturnInterval_whenRemoveTwoRangesContainingAll() {
    DisconnectedDateIntervals disconnectedDateIntervals = DisconnectedDateIntervals.ofDates(
        new TreeSet<>(Set.of(
            LocalDate.of(2021, 1, 1),
            LocalDate.of(2021, 1, 2),
            LocalDate.of(2021, 1, 3),
            LocalDate.of(2021, 1, 6),
            LocalDate.of(2021, 1, 7),
            LocalDate.of(2021, 1, 14),
            LocalDate.of(2021, 1, 18))));
    disconnectedDateIntervals.remove(DisconnectedDateIntervals.ofIntervals(
        new DateInterval(
            LocalDate.of(2021, 1, 1),
            LocalDate.of(2021, 1, 18)
        ),
        new DateInterval(
            LocalDate.of(2021, 1, 20),
            LocalDate.of(2021, 1, 25)
        )
    ));

    assertEquals(List.of(), disconnectedDateIntervals.intervals());
  }

  @Test
  void givenAListOfPoints_shouldReturnInterval_whenExpandLeftAndShrinkRight() {
    DisconnectedDateIntervals disconnectedDateIntervals = DisconnectedDateIntervals.ofDates(
        new TreeSet<>(Set.of(
            LocalDate.of(2021, 1, 10),
            LocalDate.of(2021, 1, 11),
            LocalDate.of(2021, 1, 12)
        )));
    disconnectedDateIntervals.expandOrShrink(-2, -1);

    assertEquals(List.of(
        new DateInterval(LocalDate.of(2021, 1, 8), LocalDate.of(2021, 1, 11))
    ), disconnectedDateIntervals.intervals());
  }

  @Test
  void givenContinuousIntervals_shouldReturnSingleInterval() {
    DisconnectedDateIntervals disconnectedDateIntervals = DisconnectedDateIntervals.ofIntervals(
        new DateInterval(
            LocalDate.of(2023, 1, 1),
            LocalDate.of(2023, 1, 30)
        ),
        new DateInterval(
            LocalDate.of(2023, 1, 31),
            LocalDate.of(2023, 3, 1)
        ));

    assertEquals(List.of(
        new DateInterval(LocalDate.of(
            2023, 1, 1),
            LocalDate.of(2023, 3, 1))
    ), disconnectedDateIntervals.intervals());

  }

  @Test
  void givenDiscontinuedIntervals_shouldReturnTwoIntervals() {
    DisconnectedDateIntervals disconnectedDateIntervals = DisconnectedDateIntervals.ofIntervals(
        new DateInterval(
            LocalDate.of(2023, 1, 1),
            LocalDate.of(2023, 1, 30)
        ),
        new DateInterval(
            LocalDate.of(2023, 2, 1),
            LocalDate.of(2023, 3, 1)
        ));

    assertEquals(List.of(
        new DateInterval(
            LocalDate.of(2023, 1, 1),
            LocalDate.of(2023, 1, 30)),
        new DateInterval(
            LocalDate.of(2023, 2, 1),
            LocalDate.of(2023, 3, 1))
    ), disconnectedDateIntervals.intervals());

  }

  @Test
  void givenOverlappingIntervals_shouldReturnSingleInterval() {
    DisconnectedDateIntervals disconnectedDateIntervals = DisconnectedDateIntervals.ofIntervals(
        new DateInterval(
            LocalDate.of(2023, 1, 1),
            LocalDate.of(2023, 1, 30)
        ),
        new DateInterval(
            LocalDate.of(2023, 1, 15),
            LocalDate.of(2023, 3, 1)
        ));

    assertEquals(List.of(
        new DateInterval(LocalDate.of(
            2023, 1, 1),
            LocalDate.of(2023, 3, 1))
    ), disconnectedDateIntervals.intervals());

  }

  @Test
  void givenAInterval_shouldReturnSingleInterval_whenAddingIntervalToEnd() {
    DisconnectedDateIntervals disconnectedDateIntervals = DisconnectedDateIntervals.ofIntervals(
        new DateInterval(
            LocalDate.of(2023, 1, 1),
            LocalDate.of(2023, 1, 30)
        ));
    disconnectedDateIntervals.add(new DateInterval(LocalDate.of(
        2023, 1, 31),
        LocalDate.of(2023, 3, 1)));

    assertEquals(List.of(
        new DateInterval(LocalDate.of(
            2023, 1, 1),
            LocalDate.of(2023, 3, 1))
    ), disconnectedDateIntervals.intervals());

  }
}
