# Connecting Data Sources to Asemic

This guide will walk you through the process of connecting your data sources to Asemic. We'll cover the steps for the main supported data warehouses.

## General Process

1. Log in to your Asemic account
2. Navigate to Settings > Data Connections
3. Click "Add New Connection"
4. Select your data warehouse type
5. Follow the prompts to enter your connection details

## Connecting BigQuery

1. In the Asemic UI, select "BigQuery" as your data warehouse type.
2. You'll need to provide the following information:
   - Project ID
   - Dataset ID
   - Service Account JSON key

To set up the necessary permissions:

1. In the Google Cloud Console, create a new service account
2. Grant this service account the following roles:
   - BigQuery Data Viewer
   - BigQuery Job User
   - BigQuery Data Editor (for the Asemic dataset)
3. Create and download a JSON key for this service account
4. Upload this JSON key to Asemic when prompted

## Connecting Snowflake

1. In the Asemic UI, select "Snowflake" as your data warehouse type.
2. You'll need to provide the following information:
   - Account name
   - Warehouse name
   - Database name
   - Schema name
   - Username
   - Password

To set up the necessary permissions:

1. In Snowflake, create a new user for Asemic
2. Grant this user the following privileges:
   - USAGE on the warehouse
   - USAGE on the database
   - USAGE on the schema
   - SELECT on all relevant tables
   - CREATE TABLE in the Asemic schema (for creating the data model)

## Verifying the Connection

After entering your connection details:

1. Click "Test Connection" to verify that Asemic can successfully connect to your data warehouse
2. If the test is successful, click "Save Connection"

## Setting Up the Asemic CLI

After connecting your data source, you'll need to set up the Asemic CLI:

1. Install the Asemic CLI as described in the [Prerequisites](prerequisites.md)
2. Generate an API token from the Asemic Settings page
3. Export the token to your environment:
   ```bash
   export ASEMIC_API_TOKEN=<your_token_here>
   ```
4. Create a directory for your semantic layer configuration:
   ```bash
   mkdir asemic-config && cd asemic-config
   ```
5. Initialize your configuration:
   ```bash
   asemic-cli init
   ```

## Configuring Data Mappings

Once your connection is established, you'll need to map your data to Asemic's expected structure:

1. In the Asemic UI, go to Data Mapping
2. For each required data point (e.g., user ID, timestamp, event type), select the corresponding column from your data
3. If your data is spread across multiple tables, you may need to create views in your data warehouse to consolidate it

## Validating Your Setup

After configuring your data mappings:

1. Run the Asemic CLI validation:
   ```bash
   asemic-cli config validate
   ```
2. Address any issues raised by the validation process
3. Once validation passes, push your configuration:
   ```bash
   asemic-cli config push
   ```

## Troubleshooting Common Issues

- **Connection Timeout**: Ensure that Asemic's IP addresses are whitelisted in your firewall settings
- **Permission Denied**: Double-check that the provided credentials have all necessary permissions
- **Table Not Found**: Verify that the specified tables exist and are accessible to the Asemic user
- **Data Type Mismatch**: Ensure that the data types in your warehouse match what Asemic expects (e.g., timestamps, integers)

If you encounter persistent issues, consult our [Troubleshooting Guide](../troubleshooting/common-issues.md) or contact Asemic support.

## Next Steps

Once your data sources are connected and validated, you're ready to start [Data Modeling in Asemic](data-modeling.md). This is where you'll define your user actions, properties, and KPIs to create a powerful analytics framework.
