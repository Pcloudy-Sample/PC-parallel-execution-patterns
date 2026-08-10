# Pattern 03: By Device Type

Run the same test suite across multiple coverage buckets simultaneously. Designed for teams optimising for **coverage** rather than speed.

- Appium implementation uses manufacturer buckets (Samsung, Google, OnePlus, Xiaomi) with tier tags.
- Selenium implementation uses browser tiers (Chrome, Firefox, Edge, Safari).

## When to use this pattern

- Your app needs to perform consistently across different hardware tiers
- You have compliance or market coverage requirements (e.g. must support devices down to Android 10)
- You want to catch device-specific rendering bugs, memory issues, or performance regressions

## How it works

```
Full test suite (100 tests)
         │
         ├── Appium Slot 1 → Samsung manufacturer bucket (tier: flagship)
         ├── Appium Slot 2 → Google manufacturer bucket  (tier: mid-range)
         ├── Appium Slot 3 → Samsung manufacturer bucket (tier: budget)
         ├── Appium Slot 4 → Samsung manufacturer bucket (tier: legacy)
         │
         └── Selenium Slot set → browser tiers (chrome / firefox / edge / safari)

All slots run identical tests for their matrix target.
Execution ratio impact: MEDIUM — same tests across multiple coverage dimensions.
```

## Tradeoffs

| Advantage | Disadvantage |
|---|---|
| Catches hardware-specific failures | Longer total pipeline (same tests × N devices) |
| Clear coverage matrix for stakeholders | Flaky tests appear on all devices |
| Required for regulated markets (RBI, HIPAA device diversity) | Higher pCloudy slot consumption |

## Current matrix in this repo

| Layer | Buckets used |
|---|---|
| Appium + TestNG | Manufacturer buckets: Samsung, Google, OnePlus, Xiaomi |
| Selenium | Browser tiers: chrome, firefox, edge, safari |

---

## Implementation

- [Appium + TestNG (Java)](appium-testng/)
- [Selenium (Java — web/browser)](selenium/)
