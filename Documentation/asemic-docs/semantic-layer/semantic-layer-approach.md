# Asemic's Semantic Layer Approach

Asemic's Semantic Layer represents a paradigm shift in how product analytics data is modeled and queried. This guide explains our unique approach and how it differs from traditional methods.

## Traditional Approaches to Data Modeling

Traditionally, product analytics tools have used one of two main approaches:

1. **Event-Only Models**: These models store each user action as a separate event. While flexible, they become increasingly difficult to query efficiently as data volume grows.

2. **Star Schema Models**: These use predefined concepts and dimensions, which can be efficient for certain types of queries but lack flexibility when new business questions arise.

## Asemic's Hybrid Approach

Asemic uses a hybrid approach that combines the best aspects of event-based and dimensional modeling. Our User-Action Entity Model focuses on the user as the central entity, providing both flexibility and performance.

![Semantic Layer Overview](../assets/EntityModel.png)

### Key Components

1. **User Actions Layer**: Defines available User Actions, which can be events or event-like tables.
2. **User Properties Layer**: Aggregates actions on a per-user, per-day basis.
3. **KPI Layer**: Constructs KPIs with awareness of different dimensions and aggregation methods.

## Advantages of Asemic's Approach

1. **Flexibility**: Easily adapt to new business questions without restructuring your entire data model.
2. **Performance**: Pre-aggregated data and intelligent query optimization enable fast query times, even on large datasets.
3. **Intuitive Modeling**: The user-centric model aligns closely with how businesses think about their data.
4. **Scalability**: Efficiently handle growing data volumes without sacrificing query performance.

## How It Works

1. **Data Ingestion**: Raw event data is ingested into your data warehouse.
2. **Semantic Layer Generation**: Asemic automatically generates and maintains your data model based on the definitions in the User Properties Layer and semantic tags in the User Actions Layer.
3. **Dynamic Querying**: Our proprietary Query Engine dynamically selects the optimal tables and computation methods based on your query.

## Logical vs Physical Modeling

One key innovation in Asemic's approach is the separation of logical and physical modeling:

- **Logical Model**: You define your metrics, KPIs, and relationships in business terms, without worrying about the underlying data structure.
- **Physical Model**: Asemic's intelligent engine automatically optimizes and structures the physical data model based on your logical definitions.

This approach allows you to focus on defining your analytics in business terms, while Asemic handles the complexities of efficient data storage and retrieval.

## Example: Modeling User Retention

In a traditional event-based model, calculating retention might involve complex joins and window functions across a large events table. With Asemic's approach:

1. Define a "daily active user" property in the User Properties Layer.
2. Create a "retention" KPI in the KPI Layer, referencing the "daily active user" property.
3. Asemic automatically handles the aggregation and storage of this data efficiently.

When you query retention, Asemic's Query Engine selects the most efficient data source and computation method, which might be different for different time ranges or granularities.

## Conclusion

Asemic's Semantic Layer approach represents a significant advancement in product analytics data modeling. By combining the flexibility of event-based models with the performance of pre-aggregated data, and adding a layer of business-logic-first modeling, Asemic provides a powerful, scalable, and intuitive platform for product analytics.

This approach allows you to focus on asking and answering important business questions, rather than worrying about the intricacies of data modeling and query optimization.

For more details on how to leverage this powerful system, check out our guides on [Defining User Properties](user-properties-layer.md) and [Creating KPIs](kpi-layer.md).
