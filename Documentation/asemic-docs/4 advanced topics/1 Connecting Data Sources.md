# Connecting Data Sources to Asemic

> TODO: @korhner

This guide will walk you through the process of connecting your data sources to Asemic. We'll cover the steps for the main supported data warehouses.

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


## Troubleshooting Common Issues

- **Connection Timeout**: Ensure that Asemic's IP addresses are whitelisted in your firewall settings
- **Permission Denied**: Double-check that the provided credentials have all necessary permissions
- **Table Not Found**: Verify that the specified tables exist and are accessible to the Asemic user
- **Data Type Mismatch**: Ensure that the data types in your warehouse match what Asemic expects (e.g., timestamps, integers)

If you encounter persistent issues, consult our [Troubleshooting Guide](../troubleshooting/common-issues.md) or contact Asemic support.

## Next Steps

Once your data sources are connected and validated, you're ready to start [Data Modeling in Asemic](./2%20Data%20Modeling.md). This is where you'll define your user actions, properties, and KPIs to create a powerful analytics framework.
