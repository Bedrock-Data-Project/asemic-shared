## [.. (back)](index.md)

## Match

### Consequtive non-optional steps

The representative is a pattern like:

```sql
match Action1 >> Action2 >> Action3 >> Action4
```
or
```sql
match Action1{1} >> Action1 >> Action2 >> Action2
```

Just check the order of events.

```sql
step1_2_3_4 AS (
  select
    *,
    case subsequence
      when 1 then action in ('Action1')
      when 2 then action in ('Action2')
      when 3 then action in ('Action3')
      when 4 then action in ('Action4')
      else null
    end as is_valid,
    case subsequence
      when 1 then 1
      when 2 then 2
      when 3 then 3
      when 4 then 4
      else null
    end as step,
    3 as next_subsequnece,
    1 as next_repetition
  from repeated_actions
),
```

### Conesequtive steps with optional steps

```sql
match Action1 >> Action2 >> [Action3] >> Action4
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
    1 as next_repetition
  from step3_4_prep
  window w as (partition by user_id, sequence order by subsequence)
)
// now mark the steps using Consequtive non-optional steps technique
// or Conesequtive steps with optional steps technique
```

### Non-consequitive steps with optional steps

```sql
match Action1 >> * >> [Action2] >> Action3 >> * >> Action4
```
TODO: needs to be more precise
Transform this into following steps:
1. match Action1
2. `split by` [] >> Action3 so each split goes until the end of original sequence
3. `match` the remaining part of the pattern ([Action2] >> Action3 >> * >> Action4). If needed, split again like this
4. `merge` for every `split by`

### Leading optional steps
```sql
match ? >> ? >> Action1 >> Action2
```

```
split by ? >> ? >> Action1
match ? >> ? >> Action1 >> Action2
```

```
match [Action1] >> [Action2] >> Action3 >> Action4
```

```
split by [Action1] >> [Action2] >> Action3

split by ? >> ? >> Action3
match [Action1] >> [Action2] >> Action3 >> Action4
```


```sql
match * >> Action1 >> Action2
```


------------
```sql
match [Action1] << [Action2] << Action3 >> Action4
```

```sql
match * >> Action1 >> Action2 // PREFIX >> STEP1 >> STEP2

```

```sql
match *((Action1) | (Action1 >> Action2) | ()) >> Action3 >> Action4 // PREFIX >> Step1 >> Step2 ; but PREFIX can be only one of the specified options


match * >> Action3 >> Action4  // PREFIX >> Step1 >> Step2


match * >> Action3 >> Action4
filter Action1 >> end or Action1 >> Action2 >> end or PREFIX.length = 0
SEQ
```

L >> B >> A >> C

_L_ABC
 1 234
_L_324
_LB_AC
 12 