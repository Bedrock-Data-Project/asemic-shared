# Query Optimization in Asemic

Asemic is designed to handle large volumes of data efficiently, with optimizations at both the data model and query level. This guide will explain how Asemic optimizes queries and provide tips for writing more efficient queries.

## How Asemic Optimizes Queries

Asemic uses several techniques to optimize query performance:

### 1. Intelligent Data Model

Asemic's Semantic Layer creates an efficient data model that's optimized for common analytics queries. This model includes:

- Pre-aggregated data at different levels of granularity
- Materialized views for frequently used calculations
- Indexes on commonly queried dimensions

### 2. Query Engine

Asemic's proprietary Query Engine (QE) dynamically selects the optimal tables and computation methods based on your query. It considers factors such as:

- The specific KPIs and dimensions in your query
- The date range of your analysis
- The granularity of your data
- The size of your data set

### 3. Caching

Asemic implements a smart caching layer that stores the results of recent queries. This can significantly speed up repeated or similar queries.

## Writing Efficient Queries

While Asemic's optimizations handle much of the performance tuning automatically, there are still ways you can write more efficient queries:

### 1. Use Appropriate Time Ranges

When defining funnels or cohorts, use an appropriate time horizon. For example:

```python
Funnel(
    time_horizon=1440,  # 24 hours
    steps=[
        Step(Registration),
        Step(FirstPurchase)
    ]
)
```

This allows Asemic to limit the amount of data it needs to process.

### 2. Leverage Pre-Calculated Properties

Whenever possible, use pre-calculated User Properties instead of complex on-the-fly calculations. For example, instead of calculating "users who made a purchase in the last 7 days" in your query, define a sliding window property:

```yaml
properties:
  purchased_last_7_days:
    sliding_window_property:
      source_property: daily_purchase
      sliding_window_function: max
      relative_days_from: -6
      relative_days_to: 0
```

### 3. Use Appropriate Data Types

When defining properties, use the most appropriate data type. For example, use INTEGER for whole numbers instead of FLOAT to reduce storage and processing overhead.

### 4. Limit the Use of Complex Computed Properties

While computed properties are powerful, they can be computationally expensive. Use them judiciously and consider pre-computing values where possible.

### 5. Optimize Funnel Definitions

When defining funnels, consider the following:

- Order your steps from most to least frequent events where possible
- Use the `optional` flag for steps that aren't critical to your analysis
- Be mindful of using many `NOT` conditions, as these can be computationally expensive

### 6. Use Segmentation Wisely

While segmentation is powerful, each additional segment multiplies the amount of data that needs to be processed. Use segmentation judiciously and consider using filters instead for less critical breakdowns.

## Monitoring Query Performance

Asemic provides tools to monitor the performance of your queries:

1. **Query Execution Time**: Visible in the UI for each query you run
2. **Query Explain Plans**: Available for advanced users to understand how queries are being executed
3. **Usage Analytics**: Dashboards showing query patterns and performance across your organization

If you notice consistently slow query performance, consider reaching out to Asemic support for assistance in optimizing your data model or queries.

## Conclusion

Asemic's architecture is designed to provide optimal performance for a wide range of analytics queries. By understanding how Asemic optimizes queries and following best practices for query writing, you can ensure that your analyses run efficiently, even as your data volumes grow.

For more advanced topics, check out our guide on [Custom SQL in Asemic](custom-sql.md).
