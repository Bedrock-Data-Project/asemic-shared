# Semantic Layer Overview

The Semantic Layer is the core of Asemic's analytics engine. It provides a flexible, powerful way to model your data and define complex metrics and KPIs. The Semantic Layer consists of three distinct layers, each building upon the previous to create a comprehensive model of user behavior.

## Layer 0: Action Definition

The Action Definition layer is where you define the basic building blocks of user behavior in your product. These are typically events or actions that users can take.

Key features:
- Define available User Actions
- Map raw event data to meaningful actions
- Apply semantic tags to provide context

Example:
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

[Add an image here showing the Action Definition layer]

## Layer 1: User Properties

The User Properties layer aggregates actions on a per-user, per-day basis. These properties serve as the building blocks for defining KPIs.

Key features:
- Define properties computed for each user
- Support for action-based, lifetime, and sliding window properties
- Complex calculations like sliding windows and computed properties

Example:
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
```

[Add an image here illustrating the User Properties layer]

## Layer 2: KPI Definition

The KPI layer is where you define your key performance indicators based on the properties defined in Layer 1.

Key features:
- Define complex KPIs based on user properties
- Support for different aggregation methods along various dimensions
- Ability to create templated KPIs for related metrics

Example:
```yaml
kpis:
  dau:
    label: DAU
    select: SUM({property.dau})
    x_axis:
      date: { total_function: avg }
      cohort_day: { }
```

[Add an image here showing the KPI Definition layer]

## How the Semantic Layer Works

1. **Data Ingestion**: Raw event data is ingested into your data warehouse.
2. **Action Mapping**: The Action Definition layer maps this raw data to meaningful user actions.
3. **Property Calculation**: The User Properties layer aggregates these actions into properties on a per-user, per-day basis.
4. **KPI Computation**: The KPI layer uses these properties to compute complex metrics and KPIs.
5. **Query Optimization**: Asemic's Query Engine dynamically selects the optimal tables and computation methods based on your query.

This layered approach allows for tremendous flexibility in defining and analyzing user behavior, while also providing optimizations for query performance.

In the following sections, we'll dive deeper into each layer and show you how to define actions, properties, and KPIs in Asemic.
