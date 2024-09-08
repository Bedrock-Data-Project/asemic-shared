# API Integration with Asemic

Asemic provides a robust API that allows you to integrate our analytics capabilities into your own applications or workflows. This guide will help you get started with the Asemic API.

## Authentication

All API requests require authentication using an API key. To obtain an API key:

1. Log in to your Asemic account
2. Navigate to Settings > API Keys
3. Click "Generate New API Key"

Include your API key in the `Authorization` header of all requests:

```
Authorization: Bearer YOUR_API_KEY
```

## Base URL

All API requests should be made to:

```
https://api.asemic.com/v1/
```

## Available Endpoints

### Querying Data

- `POST /query`: Execute a query and retrieve results

Example request:
```json
{
  "kpi": "dau",
  "start_date": "2023-01-01",
  "end_date": "2023-01-31",
  "group_by": ["country"]
}
```

### Managing Dashboards

- `GET /dashboards`: List all dashboards
- `POST /dashboards`: Create a new dashboard
- `GET /dashboards/{id}`: Retrieve a specific dashboard
- `PUT /dashboards/{id}`: Update a dashboard
- `DELETE /dashboards/{id}`: Delete a dashboard

### Funnel Analysis

- `POST /funnels`: Create and analyze a funnel

Example request:
```json
{
  "steps": [
    {"event": "sign_up"},
    {"event": "complete_profile"},
    {"event": "first_purchase"}
  ],
  "time_horizon": 7,
  "start_date": "2023-01-01",
  "end_date": "2023-01-31"
}
```

## Rate Limiting

API requests are subject to rate limiting. The current limits are:

- 100 requests per minute
- 5000 requests per day

If you exceed these limits, you'll receive a 429 Too Many Requests response.

## Webhooks

Asemic can send webhook notifications for various events. To set up a webhook:

1. Go to Settings > Webhooks
2. Click "Add Webhook"
3. Enter the URL where you want to receive webhook events
4. Select the events you're interested in

## Best Practices

1. Use appropriate error handling in your integrations to gracefully handle API errors or rate limiting.
2. Cache API responses where appropriate to reduce the number of API calls.
3. Use the `fields` parameter to limit the data returned by API calls to only what you need.

## SDKs

We offer SDKs for popular programming languages to make integration easier:

- [Python SDK](https://github.com/asemic/asemic-python)
- [JavaScript SDK](https://github.com/asemic/asemic-js)
- [Ruby SDK](https://github.com/asemic/asemic-ruby)

## Example: Automating Daily Reports

Here's a Python script that uses the Asemic API to generate and email a daily report:

```python
import requests
import smtplib
from email.mime.text import MIMEText

API_KEY = 'your_api_key_here'
BASE_URL = 'https://api.asemic.com/v1/'

def get_daily_metrics():
    headers = {'Authorization': f'Bearer {API_KEY}'}
    data = {
        'kpi': ['dau', 'revenue'],
        'start_date': 'yesterday',
        'end_date': 'yesterday'
    }
    response = requests.post(f'{BASE_URL}query', headers=headers, json=data)
    return response.json()

def send_email(metrics):
    msg = MIMEText(f"DAU: {metrics['dau']}\nRevenue: ${metrics['revenue']}")
    msg['Subject'] = "Daily Metrics Report"
    msg['From'] = "reports@yourcompany.com"
    msg['To'] = "team@yourcompany.com"

    s = smtplib.SMTP('localhost')
    s.send_message(msg)
    s.quit()

if __name__ == '__main__':
    metrics = get_daily_metrics()
    send_email(metrics)
```

For more detailed information and full API documentation, visit our [API Reference](https://docs.asemic.com/api).
