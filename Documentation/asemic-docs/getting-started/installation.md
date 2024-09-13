# Installing Asemic

This guide will walk you through the process of installing and setting up Asemic for your organization.

## Prerequisites

Before you begin, ensure you have the following:

- A supported data warehouse (BigQuery, Snowflake, or Redshift)
- Necessary permissions to create service accounts and access your data
- A modern web browser (Chrome, Firefox, Safari, or Edge)

## Step 1: Sign Up for Asemic

1. Visit [https://app.asemic.com](https://app.asemic.com)
2. Click on "Sign Up" and fill in your details
3. Verify your email address

## Step 2: Connect Your Data Warehouse

1. Log in to your Asemic account
2. Navigate to Settings > Data Connections
3. Click "Add New Connection"
4. Select your data warehouse type
5. Follow the prompts to enter your connection details

![Data Connection Screen](../assets/data-connection.png)

## Step 3: Install the Asemic CLI

The Asemic CLI is used for managing your semantic layer. Install it using the appropriate command for your operating system:

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

## Step 4: Configure Your Semantic Layer

1. Generate an API token from the Asemic Settings page
2. Export the token to your environment:
   ```bash
   export ASEMIC_API_TOKEN=<your_token_here>
   ```
3. Create a directory for your semantic layer configuration:
   ```bash
   mkdir asemic-config && cd asemic-config
   ```
4. Run the Asemic CLI to generate your initial configuration:
   ```bash
   asemic-cli init
   ```

## Step 5: Validate and Push Your Configuration

1. Validate your configuration:
   ```bash
   asemic-cli config validate
   ```
2. If validation is successful, push your configuration:
   ```bash
   asemic-cli config push
   ```

![CLI Configuration](../assets/cli-config.png)

## Next Steps

Congratulations! You've successfully installed and set up Asemic. Here are some next steps:

1. [Define your first KPI](../analytics-features/custom-metrics.md)
2. [Create your first dashboard](../visualization/dashboard-creation.md)
3. [Explore funnel analysis](../analytics-features/funnel-analysis.md)

If you encounter any issues during installation, please check our [Troubleshooting Guide](../troubleshooting/common-issues.md) or contact our support team.
