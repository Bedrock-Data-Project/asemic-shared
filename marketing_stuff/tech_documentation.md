## Authorization Process

To authorize your account with Asemic, log in to the Asemic platform, navigate to the **Settings** page, and click *
*Authorize**. _(Note: This feature is not yet implemented and marked as TODO)_.

## Supported Databases

### BigQuery

To connect Asemic to your BigQuery database, you need to create a service account with the following roles:

- **BigQuery Data Viewer**
- **BigQuery Job User**
- **BigQuery Data Editor** (for the dedicated dataset where the data model will be created)
  For detailed instructions on creating a service account, please refer
  to [Google's support documentation](https://support.google.com/a/answer/7378726?hl=en).

### Snowflake

_(TODO: Add instructions for Snowflake integration)_

## Creating a Semantic Layer

### Installation of Asemic CLI

The semantic layer is managed using the `asemic-cli` command-line application. Follow the instructions below to install
`asemic-cli`:

#### Ubuntu

`curl -L -o asemic-cli https://github.com/Bedrock-Data-Project/asemic-cli/releases/latest/download/asemic-cli-ubuntu`

#### macOS ARM

`curl -L -o asemic-cli https://github.com/Bedrock-Data-Project/asemic-cli/releases/latest/download/asemic-cli-macos-arm`

#### macOS x64

`curl -L -o asemic-cli https://github.com/Bedrock-Data-Project/asemic-cli/releases/latest/download/asemic-cli-macos-x64`

#### Post-installation

`chmod +x asemic-cli sudo mv asemic-cli /usr/local/bin`
_Note: Windows is not directly supported, but the Ubuntu binary can be used with WSL (Windows Subsystem for Linux)._

### ### Generating the Semantic Layer

The Asemic semantic layer is user-centric, defined by a set of **properties** (attributes describing the user) and *
*KPIs** (aggregations of user properties). Before generating the semantic layer, Asemic makes the following assumptions:

1. Data is tracked in one or more event tables, where each row represents an event performed by a user at a specific
   time.
2. A "first appearance" event table exists, where each user has one row with the timestamp of their first interaction in
   the system.
3. An activity table exists, where each user has a row for every date they were active (interacting with the system in a
   meaningful way, such as logging in).
   Given these assumptions, Asemic can generate dozens of industry-standard KPIs as a starting point.

#### API Token Requirement

Before running `asemic-cli`, you must obtain a valid API token:

1. Go to the **Asemic Settings** page.
2. Click **Generate Token**.
3. Export the token to your environment: `export ASEMIC_API_TOKEN=<insert_token_here>`

#### Generating the User Entity Model

1. **Create a Directory**: Name the directory after your API ID (retrievable from the Asemic Settings page under the
   Team tab, API ID column).
2. **Run the Asemic CLI**: Use the `asemic-cli user-entity-model activity-action` command to start a wizard that will
   map the activity. The process will resemble the following example:

```
asemic-cli user-entity-model activity-action
Enter full table name: gendemo.ua_login
Getting table schema...
Columns:
    user_id [integer]
    time [datetime]
    session_id [string]
    platform [string]
    application_version [integer]
    build_version [string]
    country [string]
    language [string]
    manufacturer [string]
    model [string]
    os_version [string]
    memory_size [string]
    screen_width [integer]
    screen_height [integer]
    screen_dpi [integer]
    device_language [integer]
    date [date]
Enter action name [Leave empty for ua_login]: login

Action datasources need a date column.
Ideally, this should be a partition column for performance reasons.
Enter date column name [Leave empty for date]: 

A timestamp column is needed that represents the exact time action happened
Enter timestamp column name [Leave empty for time]: 

Enter the name of the column that represent the id of the user that performed the action
Enter user id column name [Leave empty for user_id]: 
Adding last_login_property tag to platform. It means entity property will be generated from it.
Adding last_login_property tag to application_version. It means entity property will be generated from it.
Adding last_login_property tag to build_version. It means entity property will be generated from it.
Adding last_login_property tag to country. It means entity property will be generated from it.
Adding last_login_property tag to os_version. It means entity property will be generated from it.
Datasource saved to bedrock-demo/userentity/actions/login.yml
```

- Do the same for first appearance:

```
asemic-cli user-entity-model first-appearance-action
Enter full table name: gendemo.ua_registration
Getting table schema...
Columns:
    user_id [integer]
    time [datetime]
    session_id [string]
    engagement [number]
    payment_propensity [number]
    skill [number]
    platform [string]
    application_version [integer]
    build_version [string]
    country [string]
    language [string]
    manufacturer [string]
    model [string]
    os_version [string]
    memory_size [string]
    screen_width [integer]
    screen_height [integer]
    screen_dpi [integer]
    device_language [integer]
    date [date]
Enter action name [Leave empty for ua_registration]: registration

Action datasources need a date column.
Ideally, this should be a partition column for performance reasons.
Enter date column name [Leave empty for date]: 

A timestamp column is needed that represents the exact time action happened
Enter timestamp column name [Leave empty for time]: 

Enter the name of the column that represent the id of the user that performed the action
Enter user id column name [Leave empty for user_id]: 
Adding first_appearance_property tag to platform. It means entity property will be generated from it.
Adding first_appearance_property tag to application_version. It means entity property will be generated from it.
Adding first_appearance_property tag to build_version. It means entity property will be generated from it.
Adding first_appearance_property tag to country. It means entity property will be generated from it.
Adding first_appearance_property tag to os_version. It means entity property will be generated from it.
Datasource saved to /Users/ikorhner/projects/bedrock-demo/userentity/actions/registration.yml
```

4. **Generate Custom Actions**: Use `asemic-cli user-entity-model action` to generate any custom actions.
5. **Handle Payment Transaction Data**: If you have an action that contains payment transaction data, use
   `asemic-cli user-entity-model payment-transaction-action`.
6. **Generate the Entity Model**: Run `asemic-cli user-entity-model entity` to generate an initial set of properties and
   KPIs.

#### Directory Structure

Once generated, the structure will contain three subfolders:

- **actions**: Actions are Asemic's way of describing user events. It can be any table with rows identified by user ID,
  timestamp, and optional attributes.
- **properties**: Think of properties as columns that you can filter or group by.
- **kpis**: KPIs are metrics that can be plotted on charts.

### Properties

Properties are a powerful mechanism to define complex columns easily. There are several types of properties, each
serving a specific purpose:

#### First Appearance Properties

First appearance properties are columns copied from the first appearance action, which typically represents the user's
first interaction in the system.

```
first_appearance_country:  
  data_type: STRING  
  can_filter: true  
  can_group_by: true  
  first_appearance_property: {source_column: country}
```

Specification:

- source_column: name of the column from first_appearance action

#### Action properties

Action properties are defined as an aggregation over an action table.
Examples:

```
# Users will have value of 1 if active on a date, otherwise 0
active_on_day:  
  data_type: INTEGER  
  can_filter: false  
  can_group_by: false  
  action_property: {source_action: activity, select: 1, aggregate_function: none, default_value: 0}

# sum of revenue field in payment_transaction_action
revenue_on_day:  
  data_type: NUMBER  
  can_filter: true  
  can_group_by: false  
  action_property: {source_action: payment_transaction, select: '{revenue}', aggregate_function: sum, default_value: 0}

# max level played on a given day
max_level_played:  
  data_type: INTEGER  
  can_filter: true  
  can_group_by: true  
  action_property: { source_action: level_played, select: "{level_played.level}", aggregate_function: max, default_value: ~ }
```

Specification:

- source action - name of the action to aggregate
- select - an sql expression of the action table. Columns should be enclosed in curly brackets without specified the
  table (i.e. `{column_name}`)
- where - optional filter expression (i.e. `{column_name} > 3`)
- aggregate_function
    - sum - sum of select expression
    - avg - avg of select expression
    - first_value - select expression sorted by timestamp, first value of the day
    - last_value - select expression sorted by timestamp, last value of the day
    - none - aggregation already contained in select expression
- default_value - Property value if user had no action row on a given day

#### Sliding window properties

Those properties represent a computation of another column over a sliding window.
Examples:

```
# Number of days user was active in previous 7 days
days_active_last_7_days:  
  data_type: INTEGER  
  can_filter: true  
  can_group_by: true  
  sliding_window_property: {source_property: active_on_day, window_function: sum, relative_days_from: -6, relative_days_to: 0}
```

Specification:
source_property: name of property for sliding window calculation
window_function

- sum - sum of all values in the window
- avg - avg of all values in the window
- min - min value in the window
- max - max value in the window
  relative_days_from: relative offset related to current day, start of sliding window
  relative_days_to: relative offset related to current day, end of sliding window

#### Lifetime properties

Those properties apply a function that takes current day and previous day value and produces current day value.
Examples:

```
# Last non null value of a column at a given day
last_login_country:  
  data_type: STRING  
  can_filter: true  
  can_group_by: true  
  lifetime_property:  
    source_action_property: {source_action: activity, select: '{country}', aggregate_function: last_value}  
    merge_function: last_value

# Lifetime revenue
revenue_lifetime:  
  data_type: NUMBER  
  can_filter: true  
  can_group_by: false  
  lifetime_property: {source_property: revenue_on_day, merge_function: sum}

# max value of another property
payers_lifetime:  
  data_type: INTEGER  
  can_filter: true  
  can_group_by: true  
  lifetime_property: {source_property: payers_on_day, merge_function: max}
```

Specification:
source_property: name of property
merge_function

- sum - sum of both values
- min - min of both values
- max - max of both values
- first_value - same as coalesce(previous, current)
- last_value - same as coalesce(current, previous)

#### Computed properties

Those properties are defined as a formula of other properties.

```
cohort_day:  
  data_type: INTEGER  
  can_filter: true  
  can_group_by: true  
  computed_property: {select: 'DATE_DIFF({date}, {first_appearance_date}, DAY)'}

payment_segment:  
  data_type: STRING  
  can_filter: true  
  can_group_by: true  
  computed_property:  
    select: '{revenue_lifetime}'  
    value_mappings:  
    - range: {to: 0}  
      new_value: Non Payer  
    - range: {from: 0, to: 20}  
      new_value: Minnow  
    - range: {from: 20, to: 100}  
      new_value: Dolphin  
    - range: {from: 100}  
      new_value: Whale
```

Specification:

- select: sql expression of other properties. Properties should be enclosed in curly brackets.
- value_mappings: Optional, map range of values to a string.

### Kpis

Kpis are aggregations of properties that can get plotted over time.
Examples:

```
# number of daily active users
dau:  
  label: DAU  
  select: SUM({property.active_on_day})  
  x_axis:  
    date: {total_function: avg}  
    cohort_day: {}

retention:  
  select: SAFE_DIVIDE({kpi.dau} * 100, SUM({property.cohort_size}))  
  unit: {symbol: '%', is_prefix: false}  
  x_axis:  
    cohort_day: {}

# generated retention_d1, retention_d2, etc
retention_d{}:  
  select: SAFE_DIVIDE({kpi.dau} * 100, SUM({property.cohort_size}))  
  where: '{property.cohort_day} = {}'  
  unit: {symbol: '%', is_prefix: false}  
  x_axis:  
    date: {}  
  template: cohort_day

# reference other kpis
arpdau:  
  label: ARPDAU  
  select: SAFE_DIVIDE({kpi.revenue}, {kpi.dau})  
  unit: {symbol: $, is_prefix: true}  
  x_axis:  
    date: {}  
    cohort_day: {}
```

Specification:

- label - Name used in UI
- select - select sql expression. Properties should be aggregated (i.e. SUM({property.revenue})), while kpis are already
  aggregated {kpi.revenue}
- where: sql filter based on properties (i.e. {property.revenue} > 0)
- x_axis - date or cohort_day are allowed. Each axis can have a total_function defined (sum or avg, which represents the
  function when aggregating values over x axis). For example, number of daily active users should not be aggregated over
  time as it would produce meaningless results.
    - date - kpi with time on x axis
    - cohort_day - kpi with cohort_day (age of user in days on a given day) on x axis, useful for cohort charts

### Submitting semantic layer

After generating the semantic layer, it needs to be submitted to asemic. Before submitting, it is recommend to run
`asemic-cli config validate` to validate the configuration. asemic-cli will dry run several queries to test your
properties and kpis.
After validate is succesful, config should be submitted by using `asemic-cli config push`

#### Source control integrations

Dealing with semantic layer in this way is cumbersome, and the recommended approach is to have the semantic layer
managed on source control. If using github, github actions can be leveraged by validating pull requests, pushing merges
to main branch, and triggering backfills. For an example on how to setup this
see https://github.com/Bedrock-Data-Project/bedrock-demo

### Backfilling the semantic layer data model

As a final step, semantic layer needs to be backfilled. This can be done either by using asemic-cli (
`asemic-cli user-entity-model backfill --date-from='2024-08-23' --date-to='2024-08-25'`) or, if using github, runnning a
backfill workflow (see https://github.com/Bedrock-Data-Project/bedrock-demo for an example)
Asemic materializes your physical data model for performance reasons and is expected to be integrated with your etl
process to backfill the data as it becomes available.

### Semantic layer data model

Entity data model consists of several tables (totals, active, daily, cohort) and asemic needs it backfilled it order to
query the data. Asemic query engine is smart enough to pick the most efficient table for a given query, reducing the
costs and increasing performance of complex queries substantially.
Once the data is backfilled, new properties that are added after the backfill can be calculated at query time even if
not backfilled (does not apply to lifetime properties, as it would be too expensive). This is very useful for testing
new properties before committing to a backfill. It is recommended to backfill the data eventually however, as it speeds
up queries and makes them less expensive.
The data model is also easy to use by an analyst and can save lots of time by reducing the need to write complex joins.
Tables:

- totals - each day has all users, with first appearance and lifetime properties.
- active - each day has users that were active in last 90 days with all types of materialized properties
- daily - each day has users that were active on that day with all types of materialized properties
- cohort - each day has users that are certain days old with all types of materialized properties. useful for fast
  cohort analysis.
