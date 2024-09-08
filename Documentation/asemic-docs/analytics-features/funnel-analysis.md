# Funnel Analysis in Asemic

Funnel analysis is a powerful tool for understanding user journeys and identifying bottlenecks in your product or process. Asemic's funnel analysis feature goes beyond simple linear funnels, allowing you to define and analyze complex sequences of user actions.

## Creating a Basic Funnel

To create a basic funnel in Asemic:

1. Navigate to the Funnel Analysis section in the Asemic UI.
2. Click "New Funnel"
3. Define your funnel steps:
   - Click "Add Step" for each step in your funnel
   - Select the event or action for each step
   - (Optional) Add conditions to each step
4. Set your funnel parameters:
   - Time horizon
   - Start and end dates
   - (Optional) User segments

Here's an example of a basic funnel definition:

```python
Funnel(
    time_horizon=1440,  # 24 hours
    steps=[
        Step(Registration),
        Step(ProductView),
        Step(AddToCart),
        Step(Purchase)
    ]
)
```

## Advanced Funnel Features

Asemic's funnel analysis tool offers several advanced features to handle complex user journeys:

### Optional Steps

You can mark steps as optional, meaning they don't need to occur for the funnel to be completed, but they'll be included in the analysis if they do occur:

```python
Step(ViewTutorial, optional=True)
```

### Parallel Steps

Define multiple possible actions for a single step:

```python
Step([
    RewardClaimed(card="AwesomeFighter"),
    RewardClaimed(card="AwesomeRanger"),
    RewardClaimed(card="AwesomeMage")
])
```

### Negative Conditions

Specify actions that should not occur within a sequence:

```python
Step(NOT([
    Step(CurrencyChange, conditions={"currency": "premium"}),
    Step(Battle, conditions={"outcome": "won"})
]))
```

### Repeating Steps

Define steps that should repeat a certain number of times:

```python
Step(Level, conditions={"level": 13, "outcome": "lost"}, repeat={"min": 10})
```

### Time Windows

Specify time constraints between steps:

```python
Step(Purchase, time_window={"min": 5, "max": 60, "unit": "minutes"})
```

## Analyzing Funnel Results

Asemic provides various ways to analyze and visualize funnel results:

1. **Conversion Rates**: See the percentage of users who move from one step to the next.
2. **Drop-off Points**: Identify where users are most likely to exit the funnel.
3. **Time Between Steps**: Analyze how long users take between each step.
4. **Segmentation**: Compare funnel performance across different user segments.
5. **Cohort Analysis**: See how funnel performance changes for different user cohorts.

![Funnel Analysis](../assets/funnel-analysis.png)

## Best Practices for Funnel Analysis

1. **Start Simple**: Begin with a basic funnel and add complexity as needed.
2. **Use Clear Step Names**: Choose descriptive names for your funnel steps to make analysis easier.
3. **Consider Time Horizons**: Set appropriate time horizons based on your typical user journey.
4. **Segment Your Analysis**: Use segmentation to uncover insights about different user groups.
5. **Iterate and Refine**: Regularly review and update your funnels based on new insights and changing business needs.
6. **Combine with Other Analyses**: Use funnel analysis in conjunction with cohort analysis and retention metrics for a fuller picture.

## Advanced Use Cases

### Multi-Path Funnels

Analyze complex user journeys with multiple possible paths:

```python
Funnel(
    time_horizon=2880,  # 48 hours
    steps=[
        Step(Registration),
        Step([ProductView, CategoryBrowse]),
        Step(AddToCart),
        Step([ImmediatePurchase, AbandonCart]),
        Step(ReturnToCart, optional=True),
        Step(Purchase)
    ]
)
```

### Funnel with Negative Conditions

Identify users who completed a purchase without using a discount:

```python
Funnel(
    time_horizon=1440,  # 24 hours
    steps=[
        Step(ProductView),
        Step(AddToCart),
        Step(NOT(ApplyDiscount)),
        Step(Purchase)
    ]
)
```

## Conclusion

Asemic's funnel analysis tool provides powerful capabilities for understanding and optimizing your user journeys. By leveraging its advanced features and following best practices, you can gain deep insights into user behavior and identify opportunities for improvement in your product or process.

For more advanced topics, check out our guides on [Cohort Analysis](cohort-analysis.md) and [Custom Metrics](custom-metrics.md).
