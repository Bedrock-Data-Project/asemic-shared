# Cohort Analysis in Asemic

Cohort analysis is a powerful tool for understanding how user behavior evolves over time. Asemic's cohort analysis feature allows you to track metrics for groups of users who share a common characteristic or experience.

## Understanding Cohorts

A cohort is a group of users who share a common characteristic, typically the date they first used your product. Cohort analysis helps you answer questions like:

- How does user retention change over time?
- Do users who join during certain periods perform better than others?
- How does user behavior change as they become more experienced with your product?

## Creating a Cohort Analysis

Every metric created can be used to track both daily changes and for cohort analysis. Simply pick a metric with the `cohort` tag and it will be displayed as a cohort chart.

This enables analysis of many other metrics past the standard Retention and LTV / CLV metrics. For instance, you might be tracking time spent in the app, but that's typically very stable metric. Do the cohort analysis and you might see that your users spend a lot of time on the day on registration, then it sharply drops and subsequently climbs slowly until it reaches 70% if the day 0 value. 

Track a point in journey accross time - how does Retention d7 look like from day to day?

[insert cohort analysis image here]

## Cohort Metrics

Asemic allows you to analyze various metrics across cohorts:

1. **Retention**: Track how many users from each cohort remain active over time.
2. **Revenue**: Analyze how revenue per user changes as cohorts mature.
3. **Engagement**: Measure how user engagement (e.g., sessions per user) evolves.
4. **Custom Metrics**: Apply any custom KPI you've defined to cohort analysis.

## Visualizing Cohort Data

Asemic provides several ways to visualize cohort data:

1. **Cohort Matrix**: A heatmap showing metric values for each cohort over time.
2. **Retention Curve**: A line chart showing how retention changes over time for different cohorts.
3. **Cumulative Metric Chart**: A chart showing how a metric (e.g., lifetime value) accumulates over time for each cohort.

![Cohort Analysis](../assets/cohort-chart.png)

## Advanced Cohort Features

### Custom Cohort Definitions

While date-based cohorts are common, Asemic allows you to define cohorts based on any user property or event:

```yaml
cohort_definition:
  property: first_purchase_amount
  ranges:
    - {min: 0, max: 50, label: "Low Value"}
    - {min: 50, max: 200, label: "Medium Value"}
    - {min: 200, label: "High Value"}
```

### Relative Time Analysis

Analyze cohorts based on relative time (e.g., days since first use) rather than calendar dates:

```yaml
cohort_analysis:
  metric: sessions_per_user
  cohort_by: registration_date
  x_axis: days_since_registration
  max_periods: 90
```

### Cohort Comparison

Compare the performance of different cohorts side-by-side:

```yaml
cohort_comparison:
  base_cohort: 
    start: 2023-01-01
    end: 2023-01-31
  compare_cohort:
    start: 2023-06-01
    end: 2023-06-30
  metric: retention
  periods: 12
```

## Best Practices for Cohort Analysis

1. **Choose Meaningful Cohort Definitions**: While registration date is common, consider cohorts based on first purchase, feature adoption, or other significant events.

2. **Look for Patterns**: Pay attention to trends across cohorts. Are newer cohorts performing better or worse than older ones?

3. **Consider Seasonality**: Be aware of seasonal effects when comparing cohorts from different time periods.

4. **Combine with Segmentation**: Use segmentation in conjunction with cohort analysis to uncover insights about specific user groups.

5. **Focus on Actionable Metrics**: Choose metrics for your cohort analysis that can drive decision-making and product improvements.

6. **Account for Cohort Maturity**: Remember that newer cohorts have had less time to mature, which can affect cumulative metrics like lifetime value.

7. **Use Relative and Absolute Time**: Analyze cohorts both in terms of calendar dates and relative time since cohort formation.

## Practical Examples

### Analyzing Feature Adoption Impact

```yaml
cohort_analysis:
  metric: retention
  cohort_by: feature_x_first_use_date
  compare_to: 
    cohort: users_who_never_used_feature_x
  periods: 12
```

This analysis helps you understand if users who adopt a specific feature have better retention than those who don't.

### Investigating Pricing Change Effects

```yaml
cohort_comparison:
  base_cohort: 
    start: 2023-01-01  # Before pricing change
    end: 2023-03-31
  compare_cohort:
    start: 2023-04-01  # After pricing change
    end: 2023-06-30
  metric: ltv
  periods: 24
```

This comparison can help you understand the impact of a pricing change on user lifetime value.

## Conclusion

Cohort analysis is a powerful tool for understanding user behavior over time and measuring the long-term impact of product changes. By leveraging Asemic's flexible cohort analysis features and following these best practices, you can gain deep insights into user engagement, retention, and value.

For more advanced topics, check out our guides on [Funnel Analysis](funnel-analysis.md) and [Custom Metrics](custom-metrics.md).
