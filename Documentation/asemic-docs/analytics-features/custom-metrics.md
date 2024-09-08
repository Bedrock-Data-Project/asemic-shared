# Custom Metrics in Asemic

Custom metrics in Asemic allow you to define and analyze specific measures that are unique to your business. This feature enables you to go beyond pre-defined metrics and create calculations that precisely match your analytical needs.

## Creating Custom Metrics

To create a custom metric in Asemic:

1. Navigate to the Metrics section in the Asemic UI.
2. Click "New Metric"
3. Define your metric using Asemic's metric definition language.
4. (Optional) Add descriptions and tags for easy reference.

Here's an example of a custom metric definition:

```yaml
metrics:
  average_order_value:
    label: "Average Order Value"
    select: SAFE_DIVIDE(SUM({property.order_total}), COUNT(DISTINCT {property.order_id}))
    unit: { symbol: "$", is_prefix: true }
    x_axis:
      date: { total_function: avg }
      cohort_day: { }
```

## Types of Custom Metrics

Asemic supports several types of custom metrics:

1. **Aggregations**: Sum, count, average, etc. of user properties
2. **Ratios**: Divide one metric by another
3. **Filtered Metrics**: Apply specific conditions to your calculations
4. **Cumulative Metrics**: Calculate running totals over time
5. **Complex Calculations**: Use custom SQL for advanced computations

## Examples of Custom Metrics

### Retention Rate

```yaml
metrics:
  d7_retention_rate:
    label: "7-Day Retention Rate"
    select: SAFE_DIVIDE(
      SUM(IF({property.days_since_registration} = 7 AND {property.is_active}, 1, 0)),
      SUM(IF({property.days_since_registration} = 0, 1, 0))
    ) * 100
    unit: { symbol: "%", is_prefix: false }
    x_axis:
      date: { total_function: avg }
```

### Lifetime Value

```yaml
metrics:
  ltv:
    label: "Lifetime Value"
    select: SUM({property.lifetime_revenue}) / COUNT(DISTINCT {property.user_id})
    unit: { symbol: "$", is_prefix: true }
    x_axis:
      date: { total_function: avg }
      cohort_day: { }
```

### Conversion Rate

```yaml
metrics:
  signup_to_purchase_rate:
    label: "Signup to Purchase Conversion Rate"
    select: SAFE_DIVIDE(
      COUNT(DISTINCT IF({property.has_purchased}, {property.user_id}, NULL)),
      COUNT(DISTINCT {property.user_id})
    ) * 100
    unit: { symbol: "%", is_prefix: false }
    x_axis:
      date: { total_function: avg }
```

## Advanced Custom Metric Features

### Metric Templates

Create templates for similar metrics to reduce redundancy:

```yaml
metrics:
  cohort_day: [1, 7, 30, 90]
  retention_d{}:
    label: "Day {} Retention"
    select: SAFE_DIVIDE(
      SUM(IF({property.days_since_registration} = {}, 1, 0)),
      SUM(IF({property.days_since_registration} = 0, 1, 0))
    ) * 100
    unit: { symbol: "%", is_prefix: false }
    x_axis:
      date: { total_function: avg }
    template: cohort_day
```

### Custom SQL Metrics

For complex calculations that can't be expressed in the standard metric syntax:

```yaml
metrics:
  complex_segmentation:
    label: "Complex User Segmentation"
    select: |
      CASE
        WHEN {property.lifetime_purchases} > 10 AND {property.average_order_value} > 100 THEN 'High Value'
        WHEN {property.lifetime_purchases} > 5 OR {property.average_order_value} > 50 THEN 'Medium Value'
        ELSE 'Low Value'
      END
    x_axis:
      date: { }
```

## Best Practices for Custom Metrics

1. **Use Clear Naming Conventions**: Choose descriptive names that clearly indicate what the metric measures.

2. **Document Your Metrics**: Add detailed descriptions to your metric definitions to help other team members understand their purpose and calculation method.

3. **Consider Performance**: Be mindful of the computational cost of your metrics, especially for complex calculations or large data sets.

4. **Validate Your Metrics**: Always cross-check your custom metrics against raw data to ensure accuracy.

5. **Use Consistent Units**: Maintain consistency in units and scales across related metrics to make comparisons easier.

6. **Leverage Existing Properties**: Build your custom metrics on top of well-defined user properties when possible, rather than reimplementing complex logic.

7. **Review and Refine**: Regularly review your custom metrics to ensure they're still relevant and accurately measuring what you intend.

## Using Custom Metrics in Analysis

Once defined, custom metrics can be used throughout Asemic:

- In dashboards and visualizations
- As part of funnel analysis
- In cohort analysis
- For segmentation and filtering

## Conclusion

Custom metrics in Asemic provide the flexibility to measure and analyze the specific aspects of user behavior that matter most to your business. By leveraging this powerful feature and following best practices, you can create a suite of metrics that precisely match your analytical needs and drive informed decision-making.

For more advanced topics, check out our guides on [Query Optimization](../advanced-topics/query-optimization.md) and [Custom SQL](../advanced-topics/custom-sql.md).
