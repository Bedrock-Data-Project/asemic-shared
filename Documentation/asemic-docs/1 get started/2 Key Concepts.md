# Asemic Building Blocks

## Semantic Layer

The Semantic Layer (SL) is the core of Asemic's analytics engine. It provides a flexible, powerful way to model your data and define complex metrics and KPIs. The Semantic Layer consists of three distinct layers, each building upon the previous to create a comprehensive model of user behavior.

1. __Layer 1: User Actions__: Contains definitions of User Events and other User Actions.
2. __Layer 2: User Properties__: Contains formulas for calculating __properties__ from User Actions. This is used for building state model of User Entity that enables following a user through time and simplifies calculating metrics and working with metrics. Properties are aggregated actions on User, Day level.
3. __Layer 3: Metric Layer__: This is what normally semantic layers offer, a layer with strict definitions of key metrics consistent accross the organization. Metrics are expressed as functions of properties and other metrics. Metrics are properties aggregated on a daily level across users.

## Data Model

Semantic Model provides clear and consistent definitions of key metrics and entities.

Asemic creates physical data model, aka tables in a data warehouse, based on the logical model defined in Semantic Layer, not the other way round.

Logical model describes each user state on (the end of) each day since it appeared. Physical table that would follow that model would quickly become difficult to work with, so Asemic imploys some tricks and optimizations, using several much smaller tables instead without practical loss in capabilities. These tables are automatically calculated from SL which guarantees consistency between tables and between physical model and what Asemic expects.

This approach has several advantages:

- Data Model provides backbone for working with User Events and User Actions that enables some things that are othervise out of reach for on-the-fly computation on events.
- Data Model also tremendously speeds up calculation of metrics that are well established. This provides smooth experience for reporting needs.
- As data model is calculated from User Actions, Asemic can use definitions from SL to calculate fields ad-hoc, without the need for materializing them first. This makes data model always in sync with SL with no extra effort.
- It makes conversations easier as each concept has well defined place. 

## Asemic CLI

The Asemic CLI is used for managing your Semantic Layer and maintaining physical data model.

Current list of operations are:

```bash
asemic-cli init

asemic-cli config validate # validates your setup without changing production

asemic-cli config push     # once validation passes, push your configuration

```
