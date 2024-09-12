# Prerequisites for Data Integration with Asemic

Before you begin integrating your data with Asemic, there are several prerequisites you need to meet. This guide will walk you through the necessary steps and requirements.

## Data Requirements

1. **SQL Data Warehouse**: Your data must be stored in a SQL-compatible data warehouse.

2. **User Events**: You should have tables containing user events, either stored in separate tables or in one consolidated table.

3. **First Appearance Event**: One of your event tables should have one row per user's first appearance in the system.

4. **Activity Tracking**: You need a table or event that encodes information about user activity.

## Supported Data Warehouses
> TODO: @korhner Šta se možemo da stavimo na ovu listu, a da se ne upucamo u nogu?

Asemic currently supports the following data warehouses:

- BigQuery
- Snowflake
- (Additional warehouses may be supported; check the latest documentation)

## Access and Permissions

Ensure you have the necessary permissions to:

1. Create service accounts or database users
2. Grant read access to the relevant tables
3. Create new tables or views (for Asemic's data model)

### BigQuery Specific Requirements

If you're using BigQuery, you'll need to create a service account with the following roles:

- BigQuery Data Viewer
- BigQuery Job User
- BigQuery Data Editor (for the dedicated dataset where Asemic's data model will be created)

## Data Structure

While Asemic is flexible in terms of data structure, having a well-organized event schema will make the integration process smoother. Consider the following:

1. **Consistent User Identifier**: Ensure you have a consistent user ID across all relevant tables.
2. **Timestamp Fields**: Each event should have an associated timestamp.
3. **Event Type Identification**: There should be a clear way to distinguish between different types of events.
4. **Event structure**: Events are stored either each in a separate table, or all events in one big table with clear event type identification

## Asemic CLI Installation

You'll need to install the Asemic CLI tool, which is used for managing the semantic layer. Installation instructions:

### Ubuntu
```bash
curl -L -o asemic-cli https://github.com/Bedrock-Data-Project/asemic-cli/releases/latest/download/asemic-cli-ubuntu
chmod +x asemic-cli
sudo mv asemic-cli /usr/local/bin
```

### macOS ARM
```bash
curl -L -o asemic-cli https://github.com/Bedrock-Data-Project/asemic-cli/releases/latest/download/asemic-cli-macos-arm
chmod +x asemic-cli
sudo mv asemic-cli /usr/local/bin
```

### macOS x64
```bash
curl -L -o asemic-cli https://github.com/Bedrock-Data-Project/asemic-cli/releases/latest/download/asemic-cli-macos-x64
chmod +x asemic-cli
sudo mv asemic-cli /usr/local/bin
```

## API Token

You'll need to obtain an API token from Asemic:

1. Go to the Asemic Settings page
2. Click "Generate Token"
3. Export the token to your environment:
   ```bash
   export ASEMIC_API_TOKEN=<your_token_here>
   ```

## Network Access

Ensure that your data warehouse can be accessed from Asemic's IP addresses. You may need to whitelist these IPs in your firewall or network settings.

## Data Volume Considerations

While Asemic is designed to handle large volumes of data, be aware of any data limits or costs associated with your data warehouse. Consider starting with a subset of data for initial testing and configuration.

## Time Zone Configuration

Ensure that your event timestamps are consistently recorded in a known time zone. Asemic can work with various time zones, but consistency is key for accurate analysis.

## Next Steps

Once you've met these prerequisites, you're ready to move on to [Connecting Your Data Sources](connecting-data-sources.md) and [Data Modeling in Asemic](data-modeling.md).

If you encounter any issues while preparing for data integration, please consult our [Troubleshooting Guide](../troubleshooting/common-issues.md) or contact Asemic support.
