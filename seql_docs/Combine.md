## [.. (back)](index.md)

### Combine

Combine implements second part of what is basically map-reduce pattern.

##### Interface

Removes one deepest `sequence` tag, keeps tags in the sequence with the most tags, resets all other tags and destroys duplicate sequences created by previous split by. Also adds count of colapsed sequences.

Tag the sequence with the most valid steps:
```sql
sequence_level_calc AS (
select
  *,
  count(first_occurence) over (partition by user_id, sequence_2) as valid_steps
from tagged_steps
),
all_sequences AS (
select
  *,
  max(valid_count) over (partition by user_id, sequence_1) as max_valid_steps
from tagged_steps
)
```

Default `combine` should depend on the type of `split by`.

1. `split by Login{1}`
Keep all sequences, remove tags from all but one ("leftmost" one with the most matched steps).

2. `split by Login`
Keep only a sequence with the most matched steps


For example:

- Combine can count complete pattern and add that to the global parameter of parent sequence
- Combine can clear all tags except one that satisfies some sequence-level condition
- etc
