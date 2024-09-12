# Introduction to Asemic

Asemic is a next-generation product analytics platform designed to give you deeper insights into user behavior. By combining a powerful Semantic Layer with advanced funnel analysis capabilities, Asemic allows you to ask complex questions about your data and get answers quickly.

## Core Concepts

### Data Integration

Asemic is designed to work with your existing data warehouse, minimizing the need for complex ETL processes. Our system can automatically generate and maintain your data model based on the definitions in the Semantic Layer.

### Semantic Layer

At the heart of Asemic is our innovative three-layer Semantic Layer:

1. **User Action Definition Layer**: Defines available User Actions, providing context to raw event data.
2. **User Properties Layer**: Aggregates actions on a per-user, per-day basis, serving as building blocks for KPIs. Tracks state of the user through time, allowing easy time-travel and simplifies advanced analysis.
3. **KPI Layer**: Constructs complex KPIs with awareness of different dimensions and aggregation methods.

This approach allows for unprecedented flexibility in defining and analyzing user behavior.

### Auto-maintanence of the Physical Model

Semantic Layer is not an afterthought, it is the core concept that preceeds physical data model. Asemic uses logical structure in the Semantic Layer to generate data model from the events and then automatically builds and maintains this structure. 


### Advanced Funnel Analysis

Asemic's funnel analysis goes beyond simple linear funnels:

- Support for optional steps
- Parallel paths
- Negative conditions (NOT steps)
- Flexible time horizons and delays between steps

This allows you to model complex user journeys and gain insights into nuanced behavior patterns.

### Long-term User Journey

Funnels can provide insights in the early days of User Journey and model short-term decision making processes later on. 
Long-term User Journey is best modeled with series of cohort metrics that track various states over many days. Asemic provides simple interface for defining cohort metrics and has specialized UI for working with it.

## What Sets Asemic Apart

- **Flexibility**: Define metrics and KPIs using a business-logic first approach, without being constrained by your physical data model.
- **Power**: Perform complex analyses that would be difficult or impossible with traditional tools.
- **Ease of Use**: Our intuitive interface and smart Layout Engine make it easy to create insightful visualizations.
- **Scalability**: Asemic is built to handle large volumes of data efficiently, with optimizations at both the data model and query level.

In the following sections, we'll dive deeper into each of these concepts and show you how to leverage the full power of Asemic for your product analytics needs.

[Add an image here illustrating the three layers of the Semantic Layer]
