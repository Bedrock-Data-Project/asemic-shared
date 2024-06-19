## [.. (back)](index.md)

## Split By

### Interface

Currently implemented only split by 1 action.
`split by Action`
It can be extended, though, easily...
`split by (Action1 | Action2 | ...)`

Takes whatever sequence is passed and adds `sequence` tag. Whatever is before the first split Action is tagged
with `sequence = 0`. First proper sequence has value `sequence = 1`

On repeated use, adds new tags `sequence_1`, `sequence_2`, `sequence_N`...

```
split by Action1
split by Action2
```

First split will add tag `sequence`. The second split will break each separate stream, break it by Action2 and tag it
with `sequence_2`.

##### Split By One Single Occurence

```sql
split by Login{1}
```

Example, split on every Login.
`LBKNLBKNLLBKLB`

```
s BKNLBKNLBKNLLBKLB
0 BKN
1    LBKN
2        LBKN
3            L
4             LBK
5                LB
```

SQL:

```sql
sequences AS (
  select
    *,
    sum(if(action = 'Login', 1, 0)) over w as sequence
  from domain
  window w as (partition by user_id order by ts, case action when 'Login' then 1 else 2 end)
),
```

Comment
`case action when 'Login' then 1 else 2 end` forces the action to be first in row in case of multiple actions having the
same timestamp.

##### Split By N Single Occurence

```sql
split by [2]Login{1}
or
split by Login{1}=2
or
...
```

Similar to previous, but we want every sequence to have events from two sessions.

```
s BKNLBKNLBKNLLBKLB
0 BKN
1    LBKNLBKN
2        LBKNL
3            LLBK
4             LBKLB
5                LB
```

SQL

```sql
sequences_prep AS (
  select
    *,
    generate_array(greatest(1, sum(if(action = 'Login', 1, 0)) over w - 2), sum(if(action = 'Login', 1, 0)) over w) as sequences
                                                                    -- where 2 is number of repetitions of entry event
  from domain
  window w as (partition by user_id order by ts, case action when 'Login' then 1 else 2 end)
),
sequences AS (
  select
    sequences_prep.* except(sequences), array_length(sequences) as N, sequence
  from sequences_prep
    cross join unnest(sequences) as sequence
),
```

##### Split By One Repeated Event

```sql
split by Login
```

Let's treat consequtive repeated events as a singular entity.

```
s BKNLBKNLBKNLLBKLB
0 BKN
1    LBKN
2        LBKN
3            LLBK   <- one sequence, compare to the first option
4                LB
```

SQL

```sql
sequences_prep AS (
  select  
    *,
    if(action = 'Login', 1, 0) * if(lag(action) over w = 'Login', 0, 1) as sequence_start -- ovde NULL zeza ako je Login prvi event
  from domain
  window w as (partition by user_id order by ts, case action when 'Login' then 1 else 2 end)
),
sequences AS (
  select
    sequences_prep.* except(sequence_start),
    sum(sequence_start) over w as sequence
  from sequences_prep
  window w as (partition by user_id order by ts, case action when 'Login' then 1 else 2 end)
),
```

##### Split By N Repeated Event

```sql
split by [2]Login
or
split by Login=2
or
...
```

Let's treat consequtive repeated events as a singular entity, but allow them to appear multiple times in a sequence.

```
s BKNLBKNLBKNLLBKLB
0 BKN
1    LBKNLBKN
2        LBKNLLBK
3            LLBKLB
4                LB
```

SQL

```sql
sequences_prep AS (
  select  
    *,
    if(action = 'Login', 1, 0) * if(lag(action) over w = 'Login', 0, 1) as sequence_start -- ovde NULL zeza ako je Login prvi event
  from domain
  window w as (partition by user_id order by ts, case action when 'Login' then 1 else 2 end)
),
sequences_prep2 AS (
  select
    * except(sequence_start),
    generate_array(greatest(1, sum(sequence_start) over w - 2), sum(sequence_start) over w) as sequences
                                                                    -- where 2 is number of repetitions of entry event
  from sequences_prep
  window w as (partition by user_id order by ts, case action when 'Login' then 1 else 2 end)
),
sequences AS (
  select
    sequences_prep2.* except(sequences), array_length(sequences) as N, sequence
  from sequences_prep2
    cross join unnest(sequences) as sequence
),
```

##### Split By pattern

It's simple to extend this to examples like:

```sql
split by Login >> AnotherAction // splits on Login followed by AnotherAction

split by [] >> [] >> Login  // splits not on Login, but two "places" before

...

```
