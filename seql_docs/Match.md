## [.. (back)](index.md)

## Match

### Consequtive non-optional steps

The representative is a pattern like:

```sql
Action1 >> Action2 >> Action3 >> Action4
```
or
```sql
Action1{1} >> Action1 >> Action2 >> Action2
```

Implicitly split the starting sequence with Action1 and then just check the order of events.

```sql
match_pattern AS (
  select 
    *,
    case true_step
      when 1 then action in ('Action1')
      when 2 then action in ('Action2')
      when 3 then action in ('Action3')
      when 4 then action in ('Action4')
      when 5 then action in ('Battle') and repetitions >= 1
    end as is_valid
  from repeated_actions
  where     
    case true_step
      when 1 then repetition = 1 -- Take only First Occurence
      when 2 then repetition = 1 -- Take only First Occurence
      when 3 then repetition = 1 -- Take only First Occurence
      when 4 then repetition = 1 -- Take only First Occurence
      when 5 then repetition = repetitions -- Take only Last Occurence
      else false
    end
)
```

### Conesequtive steps with optional steps

```sql
Action1 >> Action2 >> [Action3] >> Action4
```

SQL
```sql
step1_2 AS (
  select
    *,
    case subsequence
      when 1 then action in ('Action1')
      when 2 then action in ('Action2')
      else null
    end as is_valid,
    case subsequence
      when 1 then 1
      when 2 then 2
      else null
    end as step,
    3 as next_subsequnece,
    1 as next_repetition
  from repeated_actions
),
step3 AS ( -- optional
  select
    * except(is_valid, step, next_subsequnece, next_repetition),
    case 
      when is_valid is not null then is_valid
      when subsequence = next_subsequnece then if(action in ('Action3'), true, null)
      else null
    end as is_valid,
    case 
      when step is not null then step
      when subsequence = next_subsequnece then if(action in ('Action3'), 3, null)
      else null
    end as step,
    if(max(if(subsequence = next_subsequnece, action, null)) over w in ('Action3'), next_subsequnece + 1, next_subsequnece) as next_subsequnece,
    1 as next_repetition
  from step1_2
  window w as (partition by user_id, sequence)
),
step_else as (
  select
    * except(is_valid, step, next_subsequnece, next_repetition),
    case
      when is_valid is not null then is_valid
      when subsequence = next_subsequnece then action in ('Action4')
      else false
    end as is_valid,
    case
      when step is not null then step
      when subsequence = next_subsequnece then 4
      else null
    end as step,
    next_subsequnece + 1 as next_subsequnece,
    1 as next_repetition
  from step3
),
```

### Non-consequtive steps

```sql
Action1 >> Action2 >> * >> Action3 >> Action4
```

SQL
```sql
// find where contition is satisfied
step3_4_prep AS (
  select
    *,
    action = 'Action3' AND
    lead(action) over w = 'Action4'
     as condition_satisfied
  from previos_steps
  window w as (partition by user_id, sequence order by subsequence)
),
// move the pointer to the first valid place
step3_4_prep2 AS (
  select
    * except(next_subsequence, next_repetition),
    min(if(condition_satisfied and subsequence >= next_subsequence, subsequence, null)) as next_subsequence,
    1 as repetition
  from step3_4_prep
  window w as (partition by user_id, sequence order by subsequence)
)
// now mark the steps
```

### Non-consequitive steps with optional steps

```sql
Action1 >> * >> [Action2] >> Action3 >> Action4
```


