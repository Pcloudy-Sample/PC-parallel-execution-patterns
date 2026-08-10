# Pattern 04: Round-Robin Distribution

Distribute individual test cases evenly across all available pCloudy device slots. The highest execution ratio pattern — no slot sits idle while another finishes early.

## When to use this pattern

- You have a large, heterogeneous test suite (100+ tests of varying duration)
- Maximum speed is the priority — you want the shortest possible wall-clock time
- Your tests are independent (no shared state between test cases)

## How it works

```
200 tests → 5 device slots → 40 tests per slot (balanced)

Without round-robin (by class):
  Slot 1: ████████████████████████ 40 tests, 48 min
  Slot 2: ████████████ 20 tests, 22 min  ← idle for 26 min
  Slot 3: ██████████████████ 30 tests, 35 min ← idle for 13 min
  Slot 4: ████████████████████████████ 50 tests, 58 min
  Slot 5: ████████████████ 30 tests, 35 min
  Total wall-clock: 58 min | Slot utilisation: 64%

With round-robin:
  Slot 1: ████████████████████ 40 tests, ~42 min
  Slot 2: ████████████████████ 40 tests, ~42 min
  Slot 3: ████████████████████ 40 tests, ~42 min
  Slot 4: ████████████████████ 40 tests, ~42 min
  Slot 5: ████████████████████ 40 tests, ~42 min
  Total wall-clock: ~42 min | Slot utilisation: 94%
```

## Execution ratio impact

**Highest of all four patterns.** Round-robin minimises idle slot time and maximises the ratio of tests executed per available execution window. If your execution ratio is below 80%, switching to round-robin from a by-class split typically recovers 15–25 percentage points.

## Tradeoffs

| Advantage | Disadvantage |
|---|---|
| Maximum device utilisation | Tests must be fully independent |
| Shortest total execution time | Harder to attribute failures to a domain |
| Scales linearly — add a slot, reduce time proportionally | Requires a distribution script (included below) |

## Test independence requirement

Round-robin requires that tests share no state. Each test must:
- Create its own test data (not depend on a previous test's output)
- Clean up after itself
- Not assume a specific device or session

If your tests share state, use Pattern 01 (By Test Class) or Pattern 02 (By Feature) first, then migrate to round-robin once tests are independent.

---

## Implementation

- [Appium + TestNG (Java)](appium-testng/) — uses TestNG's `dataprovider` + custom distributor
- [WebdriverIO (Node.js)](webdriverio/) — uses spec sharding
- [Playwright (Node.js)](playwright/) — uses built-in `--shard` flag
- [Selenium (Java)](selenium/) — uses parallel thread pool with queue
