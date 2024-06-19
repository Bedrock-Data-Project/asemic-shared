## [.. (back)](index.md)

## Working with variables

Once steps are properly tagged, you can make a mapping to access variables in a Python-like fashion.

Let's say we are looking for a pattern:

```sql
match Login >> Search >> Purchase
```

Each step can happen multiple times. Let's say each step has an accessible variable, namely: p_login, p_search and
p_purchase. These are some of the transformations:

```python
Step1[1]
```

Keeps the variable only for the Step1, it's value will be NULL for other steps.

```sql
select *,
       max(if(step = 1 and repetition = 1, p_login, null)) over (partition by user_id, sequence_N, step)
from tagged_steps
```

Or, expose the variable for the whole sequence:

```sql
select *,
       max(if(step = 1 and repetition = 1, p_login, null)) over (partition by user_id, sequence_N)
from tagged_steps
```

```julia
Step1[end] or Step1[-1]
```

SQL snippet:

```sql
select *,
       max(if(step = 1 and repetition = repetitions, p_login, null)) over (partition by user_id, sequence_N)
from tagged_steps
```

If index is out of bounds, the last value will be taken. So, it's for anything except `Step[1]` and `Step[end]`

```sql
select *,
       max(if(step = 2 and repetition = least(2, repetitions), p_login, null)) over (partition by user_id, sequence_N)
from tagged_steps
```

Following variables should be always available

```sql
Step
.
name
// event / derivate name
Step.pick            // which consequitive event will represent the step;
// 1 by default
Step.repetition      // order in the group
Step.repetitions     // total in the group
Step.gap             // time between previous and the current event

Sequence // Access current sequence (at the level of the last split)
```

The following sintax is in order:

```sql
// exposing particular parameter
match Login >> Search >> Purchase
set Step1.p_login = Step1[
end
].p_login
```

```sql
// expressing gap in this way, although 
match Login >> Search >> Purchase
set Step1.gap = null
set Step2.gap = Step2[1].time - Step1[
end
].time
set Step3.gap = Step3[1].time - Step2[
end
].time
```

```sql
// using it in filters
match Login >> Search >> Purchase
filter Step2.session_id = Step1.session_id
filter Step3.session_id = Step1.session_id
```

which defaults to

```sql
// using it in filters
match Login >> Search >> Purchase
filter Step2[1].session_id = Step1[1].session_id
filter Step3[1].session_id = Step1[1].session_id
```

