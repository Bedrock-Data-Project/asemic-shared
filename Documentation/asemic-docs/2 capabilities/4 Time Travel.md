# Time Travel

Time Travel is an advanced segmentation feature in Asemic that allows you to easily create complex segments and explore causal relationships of user states accross long periods of time, providing deep insights into user trends and patterns.

## Overview

Time Travel operates in two main modes:

1. Static Cohorts
2. Dynamic Segments

Both modes can be applied to User Actions and User Properties, allowing for highly flexible and precise segmentation.

## Time Travel with User Properties

Time Travel can be applied to User Properties, allowing you to segment users based on how their characteristics or status changed over time.

### Static Time Period

This mode allows you to segment users based on their properties during a specific, fixed time range.

#### Example:

```
Segment users on `calender period` of `Jan 1, 2023 - Jan 31, 2023`
Keep data for days in period where
    `Subscription Tier` = 'Premium'
And Where in Period
    MAX(`Subscription Tier`) = 'Premium'
```
This filter identifies users who had been in the 'Premium' tier for at least one day during the January. 
```
Segment users on `calender period` of `Jan 1, 2023 - Jan 31, 2023`
Keep data for days in period where
    `Subscription Tier` = 'Premium'
And Where in Period
    COUNT(`Subscription Tier`) >= 10
```
If we want to make the condition a bit more restrictive, we can ask for people who had 'Premium' tier for at least 10 days during the January.
```
Segment users on `calender period` of `Jan 1, 2023 - Jan 31, 2023`
Keep data for days in period where
    `Subscription Tier` = 'Premium'
And Where in Period
    COUNT(`Subscription Tier`) = 0
```
Adding this filter to the same segment would, as a whole, identify users who downgraded from a premium subscription.

#### Use case:
Analyzing churn patterns or identifying users for re-engagement campaigns.

### Dynamic Time Period

This mode allows you to create segments based on how user properties have changed over a rolling time window.

#### Example 1:
```
Segment users on `rolling period window` of `previous 30 days`
Keep data for days in period where
    `Revenue` > 0
And Where In Period
    SUM(`Revenue`) >= 100
```

This filter creates a segment of Gold tier customers who have spent more than $100, calculated on daily basis, calculated on a rolling basis.

#### Example 2:
```
Segment users on `rolling period window` of `next 7 days`
Keep data for all days in period
    + Filter
And Where In Period
    SUM(`Revenue`) > 0
```

This segments future spenders. It uses historical data, so this will not be available for the last 6 days (as there's no 7 days of data after it). It is a way 

#### Use case:
Quickly developing dynamic segmentation. Identifying rapidly growing customers or tracking the effectiveness of customer development programs.

---
The Time Travel feature in Asemic provides a powerful tool for creating sophisticated, time-based segments using both User Actions and User Properties. By leveraging this feature, you can gain deep insights into user behavior patterns, track complex customer journeys, and create highly targeted analyses and campaigns. Whether you're analyzing historical trends or tracking real-time changes in user behavior, Time Travel offers the flexibility and precision needed for advanced analytics in dynamic business environments.

