# Custom SQL in Asemic

While Asemic's built-in query tools are powerful and flexible, there may be times when you need to write custom SQL to perform complex analyses or access data in specific ways. This guide will show you how to use custom SQL in Asemic effectively.

## When to Use Custom SQL

Consider using custom SQL when:

1. You need to perform complex calculations that can't be expressed using standard Asemic metrics.
2. You want to access raw data tables directly for detailed analysis.
3. You're integrating Asemic with external tools or workflows that require specific data formats.

## Accessing Custom SQL

To use custom SQL in Asemic:

1. Navigate to the Query Editor in the Asemic UI.
2. Select "Custom SQL" as your query type.
3. Write your SQL query in the provided editor.
4. Run the query and view the results.

## SQL Syntax and Supported Functions

Asemic supports standard SQL syntax, including:

- SELECT, FROM, WHERE, GROUP BY, HAVING, ORDER BY clauses
- Subqueries and Common Table Expressions (CTEs)
- Window functions
- Aggregate functions (SUM, COUNT, AVG, etc.)
- Date and time functions
- String manipulation functions

Additionally, Asemic provides several custom functions to work with the Semantic Layer:

- `PROPERTY(property_name)`: Access a user property
- `KPI(kpi_name)`: Access a defined KPI
- `COHORT_DAY()`: Get the number of days since a user joined a cohort
- `SESSION_START()`, `SESSION_END()`: Get the start and end times of a user session

## Example Custom SQL Queries

### Complex Segmentation

```sql
WITH user_segments AS (
  SELECT
    user_id,
    CASE
      WHEN PROPERTY('lifetime_purchases') > 10 AND PROPERTY('average_order_value') > 100 THEN 'High Value'
      WHEN PROPERTY('lifetime_purchases') > 5 OR PROPERTY('average_order_value') > 50 THEN 'Medium Value'
      ELSE 'Low Value'
    END AS segment
  FROM user_entity
)
SELECT
  segment,
  COUNT(DISTINCT user_id) AS user_count,
  AVG(KPI('retention_d7')) AS avg_d7_retention
FROM user_segments
GROUP BY segment
```

### Funnel Analysis with Custom Conditions

```sql
WITH funnel_steps AS (
  SELECT
    user_id,
    MIN(CASE WHEN event_name = 'sign_up' THEN event_time END) AS sign_up_time,
    MIN(CASE WHEN event_name = 'complete_profile' THEN event_time END) AS profile_time,
    MIN(CASE WHEN event_name = 'first_purchase' AND PROPERTY('purchase_amount') > 50 THEN event_time END) AS purchase_time
  FROM events
  WHERE event_time BETWEEN '2023-01-01' AND '2023-12-31'
  GROUP BY user_id
)
SELECT
  COUNT(DISTINCT user_id) AS total_users,
  COUNT(DISTINCT CASE WHEN sign_up_time IS NOT NULL THEN user_id END) AS signed_up,
  COUNT(DISTINCT CASE WHEN profile_time IS NOT NULL THEN user_id END) AS completed_profile,
  COUNT(DISTINCT CASE WHEN purchase_time IS NOT NULL THEN user_id END) AS made_purchase
FROM funnel_steps
```

## Best Practices for Custom SQL

1. **Use Comments**: Add comments to explain complex logic or calculations.

2. **Leverage CTEs**: Use Common Table Expressions to break down complex queries into manageable parts.

3. **Be Mindful of Performance**: Large-scale custom SQL queries can be resource-intensive. Use EXPLAIN PLAN to understand query performance.

4. **Use Semantic Layer Functions**: Whenever possible, use PROPERTY() and KPI() functions to leverage the Semantic Layer's pre-aggregated data.

5. **Validate Results**: Cross-check results from custom SQL queries against standard Asemic reports to ensure accuracy.

6. **Consider Query Reusability**: If you find yourself frequently writing similar custom SQL, consider defining a custom metric or property instead.

7. **Be Cautious with Updates**: Custom SQL in Asemic is generally read-only. Be very careful if you're granted write access, as changes can affect your entire data model.

## Integrating Custom SQL with Asemic Features

Custom SQL can be integrated with other Asemic features:

- **Dashboards**: Results from custom SQL queries can be added to dashboards as custom visualizations.
- **Scheduled Reports**: Set up custom SQL queries to run on a schedule and deliver results via email or to a specified endpoint.
- **API Access**: Custom SQL queries can be executed via Asemic's API for integration with external tools.

## Limitations and Considerations

- Custom SQL has access to raw data, which may include sensitive information. Ensure you have appropriate data access permissions.
- Complex custom SQL queries may have longer execution times compared to standard Asemic queries.
- Custom SQL queries don't automatically benefit from Asemic's query optimization features.

## Conclusion

Custom SQL in Asemic provides a powerful tool for advanced users to perform complex analyses and access data in specific ways. By following best practices and leveraging Asemic's Semantic Layer functions, you can extend the platform's capabilities to meet your unique analytical needs.

For more information on optimizing your queries, check out our guide on [Query Optimization](query-optimization.md).
