## [.. (back)](index.md)

## Combine

Combine implements second part of what is basically map-reduce pattern.

### Interface

Removes one deepest `sequence` tag, keeps tags in the sequence with the most tags, resets all other tags and destroys duplicate sequences created by previous split by. Also adds count of colapsed sequences.

Tag the sequence with the most valid steps:
```sql
combine_aux1 AS (
select
  *,
  sum(if(is_valid, first_occurence, 0)) over (partition by user_id, sequence_2) as valid_steps
from tagged_steps
),
combine_aux2 AS (
select
  *,
  max(valid_steps) over (partition by user_id, sequence_1) = valid_steps as valid_sequence
from tagged_steps
)
```
Take the first (leftmost) valid sequence, if it exists.

```sql
select
  * except(steps),
  if(valid_sequence, steps, null) as steps
from combine_aux2
qualify sequence_2 - if(valid_sequence, -1000, 0) = min(sequence_2 - if(valid_sequence, -1000, 0)) over (partition by user_id, row_id)
```

The above algorithm can be simplified if we know the split can not create duplicates. TODO for performance pass.


#### TODO and questions

- Maybe this can be extended (in the future) to make true map-reduce framework.