# Pattern 02: By Feature Module

Tag your tests by feature and route each feature to a dedicated pCloudy device slot. Failures in one feature domain never block execution of another.

## When to use this pattern

- Your team is organised by product feature (auth, payments, profile, notifications)
- You want failure isolation — a broken login test should not cancel the checkout run
- You use TestNG groups or pytest markers to categorise tests already

## How it works

```
Tagged test suite (120 tests across 4 features)
         │
         ├── Slot 1 → @feature:auth         (30 tests) → Samsung manufacturer bucket
         ├── Slot 2 → @feature:payments     (35 tests) → Google manufacturer bucket
         ├── Slot 3 → @feature:profile      (25 tests) → OnePlus manufacturer bucket
         └── Slot 4 → @feature:search       (30 tests) → Xiaomi manufacturer bucket

Each slot reports independently — auth failures visible before payment tests finish
Execution ratio impact: HIGH — all slots active, clear domain ownership
```

## Tradeoffs

| Advantage | Disadvantage |
|---|---|
| Feature team owns their slot's results | Uneven feature sizes = uneven execution time |
| Flaky features don't block healthy ones | Requires consistent tagging discipline |
| Easy to add new features as new slots | Cross-feature tests need their own slot |

## Tagging convention

Use a consistent prefix across all tests. These patterns use:

- TestNG: `@Test(groups = {"feature:auth"})`
- Playwright: `test.describe('@feature:auth', () => { ... })`
- pytest: `@pytest.mark.feature_auth`

---

## Implementation

- [Appium + TestNG (Java)](appium-testng/)
- [Playwright (Node.js)](playwright/)
