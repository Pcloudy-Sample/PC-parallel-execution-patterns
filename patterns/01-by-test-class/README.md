# Pattern 01: By Test Class

Split your test classes (or TestNG suites / pytest modules) across pCloudy device slots so each slot runs a distinct subset of tests simultaneously.

## When to use this pattern

- Your tests are already organised into logical classes or modules
- You want the simplest parallel setup with predictable slot usage
- You need isolated failure reporting per module (a flaky login class doesn't block the checkout run)

## How it works

```
Test suite (100 tests across 5 classes)
         │
         ├── Slot 1 → LoginTests.class        (20 tests)
         ├── Slot 2 → CheckoutTests.class     (20 tests)
         ├── Slot 3 → ProfileTests.class      (20 tests)
         ├── Slot 4 → NotificationTests.class (20 tests)
         └── Slot 5 → SearchTests.class       (20 tests)

Total wall-clock time ≈ time for the slowest single class
Execution ratio impact: HIGH — all slots active throughout
```

## Tradeoffs

| Advantage | Disadvantage |
|---|---|
| Simple to configure | Uneven class sizes = uneven slot utilisation |
| Failures isolated by module | Slowest class determines total run time |
| Easy to add a new class = add a slot | Requires re-balancing if class sizes drift |

## Execution ratio tip

If one class is 3× larger than the others, split it into sub-suites and distribute those too. The `tools/device-allocator.py` script in the repo root will flag imbalanced splits automatically.

---

## Implementation

- [Appium + TestNG (Java)](appium-testng/)
- [WebdriverIO (Node.js)](webdriverio/)
