# Auto-Generation of Data Model

Asemic's Semantic Layer includes a powerful auto-generation feature that creates and maintains your data model based on your configuration. This page explains how this process works and how you can control it.

## How Auto-Generation Works

1. **Configuration Analysis**: When you push your Semantic Layer configuration, Asemic analyzes your User Actions, User Properties, and KPI definitions.

2. **Schema Generation**: Based on this analysis, Asemic generates a schema for your data model. This includes tables for storing aggregated data, indexes for optimizing queries, and views for simplifying complex calculations.

3. **Data Population**: Asemic then populates this schema with data from your source tables, performing necessary transformations and aggregations.

4. **Incremental Updates**: As new data arrives in your source tables, Asemic automatically updates your data model, ensuring it's always up-to-date.

## Frequency of Updates

- **Initial Generation**: Occurs when you first push your configuration.
- **Incremental Updates**: Run daily.
- **Manual Rewind** This can be triggered to backfil new property, or to correct some issues in data.

## User Controls

> TODO: @korhner Check this

While the auto-generation process is largely automated, you have several controls:

1. **Manual Trigger**: You can manually trigger a full refresh of your data model:
   ```bash
   asemic-cli model refresh
   ```

2. **Update Frequency**: You can adjust the frequency of incremental updates in your configuration:
   ```yaml
   model_settings:
     incremental_update_frequency: 30m  # 30 minutes
   ```

3. **Partial Updates**: You can trigger updates for specific parts of your model:
   ```bash
   asemic-cli model update --kpi daily_revenue
   ```

## Best Practices

1. **Start Small**: Begin with a small subset of your data to ensure your configuration is correct before scaling up.
2. **Monitor Performance**: Keep an eye on the time taken for model generation and updates. If it's taking too long, you may need to optimize your configuration.
3. **Version Control**: Keep your Semantic Layer configuration under version control to easily track changes and roll back if necessary.
4. **Test Before Pushing**: Use the `asemic-cli config validate` command to test your configuration before pushing changes to production.

## Limitations

- The auto-generation process can take significant time for very large datasets or complex configurations.
- There's a limit to the complexity of calculations that can be auto-generated. For extremely complex cases, you may need to use custom SQL.

By leveraging Asemic's auto-generation capabilities, you can maintain a complex, performant data model with minimal manual intervention. This allows you to focus on deriving insights from your data rather than managing infrastructure.

