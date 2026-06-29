# Custom UID Generation Logic

The uid generator converts a number into a UID using this 62-character set:

```text
0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ
```

The mapping is as follows

| Bit position | Character |
| ---: | --- |
| 0 | `0` |
| 1 | `1` |
| 61 | `Z` |

## Generating the UID

1. Read the number in binary form.
2. Check each bit of the number from position 0 through position 61.
3. When a bit is `1`, add the character mapped to that bit position to our uid string.

Examples:

| Number | Binary | Set bit positions | Generated UID |
| ---: | ---: | --- | --- |
| 1 | `001` | 0 | `0` |
| 2 | `010` | 1 | `1` |
| 3 | `011` | 0 and 1 | `01` |
| 4 | `100` | 2 | `2` |
| 5 | `101` | 0 and 2 | `02` |
| 6 | `110` | 1 and 2 | `12` |
| 7 | `111` | 0, 1, and 2 | `012` |


Only bit positions 0 through 61 are used, so the largest supported number is:

```text
2⁶² - 1 = 4,611,686,018,427,387,903
```

These are a lot of pastes, but in case we manage to fill all spots in that case a
slug can be introduced ( this is not yet implemented check TODO.md for completion status)


