# Quick Start Guide

> TODO: @korhner
Redefinisati ovo tako da se prikaže najkraći mogući proces koji od 0 vodi do situacije gde može da se radi u BI alatu.

This guide will help you get started with Asemic quickly. We'll cover the basic steps from installation to creating your first analysis.

## 1. Installation

First, install the Asemic CLI:

```bash
# For Ubuntu
curl -L -o asemic-cli https://github.com/Bedrock-Data-Project/asemic-cli/releases/latest/download/asemic-cli-ubuntu
chmod +x asemic-cli
sudo mv asemic-cli /usr/local/bin

# For macOS ARM
curl -L -o asemic-cli https://github.com/Bedrock-Data-Project/asemic-cli/releases/latest/download/asemic-cli-macos-arm
chmod +x asemic-cli
sudo mv asemic-cli /usr/local/bin
```

## 2. Connect Your Data Source

1. Log in to your Asemic account at [https://app.asemic.com](https://app.asemic.com)
2. Navigate to Settings > Data Connections
3. Click "Add New Connection" and follow the prompts to connect your data warehouse

## 3. Configure Your Semantic Layer

1. Generate an API token from the Asemic Settings page
2. Export the token to your environment:
   ```bash
   export ASEMIC_API_TOKEN=<your_token_here>
   ```
3. Initialize your Asemic configuration:
   ```bash
   mkdir asemic-config && cd asemic-config
   asemic-cli init
   ```

## 4. Define User Actions

Create a file `login_action.yml`:

```yaml
table_name: user_logins
tags: [ activity_action ]
columns:
  date: { data_type: DATE, tags: [ date_column ] }
  user_id: { data_type: INTEGER, tags: [ entity_id_column ] }
  time: { data_type: DATETIME, tags: [ action_timestamp_column ] }
  platform: { data_type: STRING, tags: [ last_login_property ] }
```

## 5. Define User Properties

In `properties.yml`:

```yaml
properties:
  dau:
    label: DAU
    data_type: INTEGER
    can_filter: true
    can_group_by: true
    action_property:
      source_action: login
      select: 1
      aggregate_function: none
      default_value: 0
```

## 6. Define KPIs

In `kpis.yml`:

```yaml
kpis:
  dau:
    label: "Daily Active Users"
    select: SUM({property.dau})
    x_axis:
      date: { total_function: avg }
      cohort_day: { }
```

## 7. Validate and Push Your Configuration

```bash
asemic-cli config validate
asemic-cli config push
```

## 8. Create Your First Dashboard

1. In the Asemic UI, go to Dashboards > New Dashboard
2. Click "Add Visualization"
3. Select your "Daily Active Users" KPI
4. Choose a date range and click "Create"

Congratulations! You've set up Asemic and created your first dashboard. Explore the documentation to learn more about Asemic's powerful features.

