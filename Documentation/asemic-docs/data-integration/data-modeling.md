# Data Modeling in Asemic

After connecting your data sources, the next step is to model your data in Asemic. This process involves defining user actions, properties, and KPIs that will form the foundation of your analytics.

## Understanding Asemic's Data Model

Asemic uses a three-layer approach to data modeling:

1. **User Actions Layer**: Defines the basic events or actions users can take
2. **User Properties Layer**: Aggregates actions into meaningful properties
3. **KPI Layer**: Defines high-level metrics based on user properties

## Defining User Actions

User actions are the foundational events in your data. To define a user action:

1. In your config directory, create a new YAML file for each action (e.g., `login_action.yml`)
2. Define the action using the following structure:

```yaml
table_name: gendemo_ua_login
tags: [ activity_action ]
columns:
  date: { data_type: DATE, tags: [ date_column ] }
  user_id: { data_type: INTEGER, tags: [ entity_id_column ] }
  time: { data_type: DATETIME, tags: [ action_timestamp_column ] }
  platform: { data_type: STRING, tags: [ last_login_property ] }
  country: { data_type: STRING, tags: [ last_login_property ] }
```

3. Repeat this process for all relevant user actions in your data

## Defining User Properties

User properties aggregate actions into meaningful metrics. To define a user property:

1. Create a `properties.yml` file in your config directory
2. Define properties using the following structure:

```yaml
properties:
  dau:
    label: DAU
    data_type: INTEGER
    can_filter: true
    can_group_by: true
    action_property:
      source_action: activity
      select: 1
      aggregate_function: none
      default_value: 0

  lifetime_revenue:
    label: Lifetime Revenue
    data_type: FLOAT
    can_filter: true
    can_group_by: true
    lifetime_property:
      source_property: daily_revenue
      merge_function: sum
```

## Defining KPIs

KPIs are high-level metrics based on user properties. To define a KPI:

1. Create a `kpis.yml` file in your config directory
2. Define KPIs using the following structure:

```yaml
kpis:
  arpu:
    label: "Average Revenue Per User"
    select: SAFE_DIVIDE({kpi.total_revenue}, {kpi.dau})
    unit: { symbol: "$", is_prefix: true }
    x_axis:
      date: { total_function: avg }
      cohort_day: { }

  retention:
    label: "Retention"
    select: SAFE_DIVIDE({kpi.dau} * 100, SUM({property.cohort_size}))
    unit: { symbol: "%", is_prefix: false }
    x_axis:
      cohort_day: { }
```

## Validating Your Data Model

After defining your actions, properties, and KPIs:

1. Run the Asemic CLI validation:
   ```bash
   asemic-cli config validate
   ```
2. Address any issues raised by the validation process
3. Once validation passes, push your configuration:
   ```bash
   asemic-cli config push
   ```

## Best Practices for Data Modeling

1. **Start Simple**: Begin with basic actions, properties, and KPIs. You can always add complexity later.
2. **Use Descriptive Names**: Choose clear, meaningful names for your actions, properties, and KPIs.
3. **Document Your Model**: Add comments to your YAML files explaining the purpose of each element.
4. **Consider Performance**: Be mindful of the computational cost of your properties and KPIs, especially for large data sets.
5. **Reuse Components**: Build complex metrics from simpler, reusable components.
6. **Align with Business Objectives**: Ensure your KPIs directly relate to your key business questions.

## Advanced Modeling Techniques

### Funnel Definition

You can define complex funnels using Asemic's sequence matching capabilities:

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

### Cohort Analysis

Define cohorts based on user properties or actions:

```yaml
cohort_definition:
  property: first_purchase_amount
  ranges:
    - {min: 0, max: 50, label: "Low Value"}
    - {min: 50, max: 200, label: "Medium Value"}
    - {min: 200, label: "High Value"}
```

## Iterating on Your Data Model

As your business evolves, you'll likely need to update your data model. To do this:

1. Make changes to your YAML configuration files
2. Run `asemic-cli config validate` to check for any issues
3. If validation passes, run `asemic-cli config push` to update your model
4. Asemic will automatically reprocess your data based on the new model

Remember, Asemic's flexible approach allows you to evolve your data model without needing to restructure your underlying data warehouse.

## Next Steps

With your data model defined, you're ready to start analyzing your data. Check out our guides on [Creating Dashboards](../visualization/dashboard-creation.md) and [Performing Analyses](../analytics-features/custom-metrics.md) to start gaining insights from your data.
