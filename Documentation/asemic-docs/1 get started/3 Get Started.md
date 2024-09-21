# Developer Quickstart

The Asemic comes with CLI app for setting up and maintaining your project. It is Git-friendly, low-code solution.

## Connecting to Your Data

This guide will walk you through the process of connecting your data sources to Asemic. We'll cover the steps for the main supported data warehouses.

### Setting Up Workspace

Once you have been registered with Asemic, you will need to setup a workspace.

Workspace can contain multiple projects, and each project is an indepented entity, containing datasets of a single product. (At the moment, workspace and project will be set for you during onboarding)

You can access projects list by going to `Settings -> Projects` in Asemic UI. We need to setup several things here. First, setup connection to your warehouse by clicking create connection.

### Setting Up Connection

#### Big Query

To connect Asemic to your BigQuery database, you need to create a service account with the following roles:

- **BigQuery Data Viewer**
- **BigQuery Job User**
- **BigQuery Data Editor** (for the dedicated dataset where the data model will be created)

> For detailed instructions on creating a service account, please refer to [Google's support documentation](https://support.google.com/a/answer/7378726).

Once you have your service account key, you need to base64 encode it (you can use an online utility like https://www.base64encode.org/ for that, or doing it in terminal). Then will gcp project id and base64 encoded service account key in create connection popup. Before submitting, make sure test connection button says it can connect succesfully.

After connecting to database, take note of API ID found in projects list. This will be your identifier when working with asemic-cli , the tool for managing your asemic semantic layer.

Finally, go to `setting/profile` and regenerate API token and store it somewhere safe. This will be your secret key asemic-cli authorization.

> Note: it is recommended to store your semantic layer config on version control, to facilitate collaboration. If using github, you can check the asemic demo example: https://github.com/Bedrock-Data-Project/bedrock-demo
This repo uses github actions for automatic validation of pull requests, automatic push on merge to main branch and workflow for backfilling the entity model.

#### Not Using Big Query?

Don't worry, we support almost all data warehouses with standard SQL interface. Check [Connecting Data Sources](../4%20advanced%20topics/1%20Connecting%20Data%20Sources.md) for more examples.

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

## Setting Up the Asemic CLI

After connecting your data source, you'll need to set up the Asemic CLI:

1. Generate an API token from the Asemic Settings page
2. Export the token to your environment:
   ```bash
   export ASEMIC_API_TOKEN=<your_token_here>
   ```
3. Create a directory for your semantic layer configuration:
   ```bash
   mkdir asemic-config && cd asemic-config
   ```
4. Initialize your configuration:
   ```bash
   asemic-cli init
   ```
## Setting up Semantic Layer
