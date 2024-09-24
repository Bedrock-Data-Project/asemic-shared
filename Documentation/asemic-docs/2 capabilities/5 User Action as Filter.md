# User Action as Filter

The User Action as Filter feature in Asemic allows you to segment and filter your data based on user actions or events. This powerful tool enables you to create dynamic segments and perform complex analyses based on user behavior.

## Modes of Operation

There are three main modes of operation for the User Action filter:

1. Had Action
2. Had No Action
3. Time Travel

### 1. Had Action

This mode generates a dynamic segment of users who have performed a specific action on a given day.

#### How it works:
- For each day in the selected period, users who have performed the specified action are included in the segment. This creates a dynamic segment where users change from day to day.
- You can further refine the filter by specifying parameters of the event.

#### Example 1:
```
Users who had PromotionOffer
where
    Offer Type = 'Offer Name'
```

This filter selects users who used promotion offer. Promotion offers are often very dynamic and there could be many of them generated over the year, so this is the ideal way to segment those users without weighing down on the data model.

#### Example 2:
```
Users who had Login
where
    Login Platform = 'iPhone'
```

This filter selects users who logged in from iPhone. In multi-screen application a User could be active on iPhone, tablet, home PC etc. This is important when counting active users on each of these, as these numbers are not additive, but we still want to know activity number for each category.

#### Use case:

Segmenting users based on a dynamic state. It's ideal when users could be in many mutually non-exclusive states or if states are dynamically added on daily 

### 2. Had No Action

This mode creates a segment of users who have not performed a specific action within the given time frame.

#### How it works:
- For each day in the selected period, users who have zero occurrences of the specified action are included in the segment.
- You can specify parameters of the event to make the filter more precise.

#### Example:
```
Users who had no PageView
where
    Page = 'Pricing'
```

This filter selects users who haven't viewed the pricing page.

#### Use case:
Identifying users for custom campaigns where we want to expose them to .

### 3. Time Travel

This mode allows for more complex, time-sensitive segmentation of users.

#### How it works:
There are two options for Time Travel:
1. Static time period - noted by `calender period` option
2. Dynamic time period - noted by `rolling window period` option

#### 3.1 Static Time Period

This option is useful for segmenting users based on actions during specific events or promotions.

#### Example:
```
Users who had SpecialOffer
in period of Nov 24, 2023 - Nov 28, 2023
where for each event
    Offer type = 'Black Friday'
and where in period
    Count ≥ 1
```

This filter selects users who participated in the Black Friday special offer.

#### Use case:
Analyzing the effectiveness of a time-limited promotion or event.

#### 3.2 Dynamic Time Period

This option allows you to create segments based on relative time dependencies.

#### Example:
```
Users in Dynamic cohort
who had Purchase in period of last 7 days
where for each event
    Currency = 'Credits'
and Paid amount > 100
```

This filter creates a rolling 7-day window, selecting users who have spent more than 100 credits in the past week.

#### Use case:
Tracking user engagement or spending patterns over time, useful for identifying trends or the effectiveness of ongoing campaigns.

---
The User Action as Filter feature in Asemic provides a powerful way to segment and analyze your user base based on their actions and behaviors. By leveraging the different modes and options available, you can gain deep insights into user engagement, identify opportunities for improvement, and create highly targeted analyses and campaigns.

