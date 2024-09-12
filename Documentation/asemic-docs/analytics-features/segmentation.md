# Segmentation in Asemic

Segmentation is a powerful feature in Asemic that allows you to divide your user base into meaningful groups for more targeted analysis. This guide will walk you through creating and using segments effectively.

## Types of Segmentation

Asemic supports several types of segmentation:

1. **Property-based Segmentation**: Divide users based on their properties (e.g., country, device type).
2. **Behavioral Segmentation**: Group users based on their actions (e.g., users who made a purchase in the last 30 days).
3. **Cohort Segmentation**: Segment users based on when they first performed a specific action.
4. **Funnel-based Segmentation**: Group users based on their progress through a defined funnel.
5. **Time-travel Segmentation**: Segment users based on their past or future state and behavior.

## Creating a Basic Segment

> TODO: Ovo mora sa skrinshot-ovima
To create a basic segment:

1. In the Asemic UI, go to Segments > New Segment
2. Give your segment a name
3. Choose the type of segmentation
4. Define your segment criteria
5. Save your segment

Example: Users from the United States
```yaml
segment:
  name: US Users
  type: property
  criteria:
    property: country
    operator: equals
    value: "United States"
```

## Advanced Segmentation Techniques

### Combining Multiple Criteria

You can create more complex segments by combining multiple criteria:

```yaml
segment:
  name: High-Value Mobile Users
  type: compound
  operator: AND
  criteria:
    - type: property
      property: platform
      operator: equals
      value: "mobile"
    - type: property
      property: lifetime_value
      operator: greater_than
      value: 100
```

### Behavioral Segmentation

Create segments based on user actions:

```yaml
segment:
  name: Recent Purchasers
  type: behavioral
  criteria:
    event: purchase
    timeframe:
      type: rolling
      value: 30
      unit: days
```

### Funnel-based Segmentation

Segment users based on their progress through a funnel:

```yaml
segment:
  name: Abandoned Cart Users
  type: funnel
  steps:
    - event: view_product
    - event: add_to_cart
    - event: initiate_checkout
      completed: false
  timeframe:
    type: rolling
    value: 7
    unit: days
```

## Using Segments in Analysis

Once created, segments can be used throughout Asemic:

- Apply segments to any KPI or chart for filtered analysis
- Compare multiple segments side-by-side in dashboards
- Use segments as steps in funnel analysis
- Apply segments in cohort analysis for more granular insights

## Best Practices

1. **Use Clear Naming Conventions**: Choose descriptive names that clearly indicate the segment's criteria.
2. **Start Broad, Then Refine**: Begin with broader segments and refine them as you gain insights.
3. **Limit Segment Complexity**: While Asemic supports complex segments, overly complex segments can be difficult to interpret and may impact query performance.

## Advanced Features

### Dynamic Segments

Asemic supports dynamic segments that update automatically based on user behavior:

```yaml
segment:
  name: Top 10% Users by Revenue
  type: dynamic
  criteria:
    property: revenue
    percentile: 90
```

### Time-travel Segmentation
