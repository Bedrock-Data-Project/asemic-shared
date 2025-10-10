## [.. (back)](index.md)

## Domain

Noted by `domain` keyword. Optional.

__Default__ value is whatever is mentioned in `matched` section.
It is a collection of `Actions` that will be used to construct sequences.

### Interface

> __Example 1__ implicit domain


`match Login >> Search >> Purchase`

Domain is consisted of three Events, namely: Login, Search and Purchase. It is equivalent to writing:

```sql
domain Login, Search, Purchase
match Login >> Search >> Purchase
```

> __Example 2__ impact of the domain

Example sequence, let's denote Login, Search and Purchase with letters L, S and P:
`LSPSP`
Matched part of the above sequence:

`[LSP]SP`

Changing the domain will affect the match.

```sql
domain Login, Search, Purchase, Tutorial
match Login >> Search >> Purchase
```

Example sequence:
`LTSPTSP`

Matched part of the sequence:
`[L]TSPTSP`

> Note! If you remove Tutorial events (T) from the sequence, you will get the previous example sequence

#### Options:

##### Event derivatives

> __Example 3__ event derivative

```sql
domain Login, Search where location = 'main screen' as SearchMain, Purchase
match Login >> SearchMain >> Purchase
```

> __Example 4__ using event derivative implicitly

It could be implicitly defined as:

`match Login >> Search where location = 'main screen' as SearchMain >> Purchase`

#### TODO and Questions:

- Should it be `domain add` keyword? Meaning is perhaps more straightforward.
