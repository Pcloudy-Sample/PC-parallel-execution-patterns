# Choosing a Parallel Execution Pattern

## The single question that determines the right pattern

**Are you optimising for speed or coverage?**

- Speed → how fast can all tests finish? → Pattern 04 Round-Robin
- Coverage → how many device configurations are tested? → Pattern 03 By Device Type

If neither is the primary constraint, the organisational structure of your test suite determines the pattern.

---

## Decision flowchart

```
Start here
     │
     ▼
Are your tests independent (no shared state)?
     │
     ├─ NO  → Fix test independence first, then return here
     │
     └─ YES ▼
            │
     Do you have 5+ parallel slots?
            │
            ├─ YES → Do you need device coverage (flagship/mid/budget)?
            │              │
            │              ├─ YES → Pattern 03: By Device Type
            │              │
            │              └─ NO  → Pattern 04: Round-Robin
            │                       (highest utilisation, fastest result)
            │
            └─ NO (1–4 slots) ──────────────────────────────────────────┐
                                                                        │
                   Are tests already tagged by feature?                 │
                         │                                              │
                         ├─ YES → Pattern 02: By Feature Module         │
                         │                                              │
                         └─ NO  → Pattern 01: By Test Class ◄──────────┘
```

---

## Execution ratio benchmarks by pattern

Based on internal pCloudy deployments across enterprise QA teams.

| Pattern | Typical execution ratio | Wall-clock vs sequential |
|---|---|---|
| 01 By Test Class | 75–85% | 4–5× faster |
| 02 By Feature Module | 80–88% | 4–5× faster |
| 03 By Device Type | 95–100% | Same speed, N× more coverage |
| 04 Round-Robin | 90–98% | 5–6× faster |

Pattern 01 and 02 show lower ratios because class/feature size imbalance leaves some slots idle. Round-robin eliminates this.

---

## Common migration paths

**From sequential to parallel (first time)**
Start with Pattern 01 (By Test Class). It requires the least configuration change and gives immediate wins. Once you understand your slot utilisation, migrate to Pattern 04.

**From Pattern 01 to Pattern 04**
The migration requires verifying test independence. Run your suite twice sequentially in different orders — if results differ, you have shared state. Fix the shared state, then switch.

**Adding device coverage without losing speed**
Run Pattern 04 (Round-Robin) for your regression suite AND Pattern 03 (By Device Type) for your smoke suite. Two separate pipelines, both on pCloudy — smoke covers device diversity, regression covers speed.

---

## Signs you're in the wrong pattern

| Symptom | Likely cause | Fix |
|---|---|---|
| Execution ratio below 70% | One slot doing all the work | Switch to round-robin |
| Tests failing on some devices but not others | Device-specific bugs not caught | Add Pattern 03 coverage |
| Pipeline taking 3× longer than expected | Slot imbalance from by-class split | Run `tools/device-allocator.py` |
| Different results on repeated runs | Shared state between tests | Fix test independence before parallelising |

---

## Calculating how many slots you need

Use the device allocator tool:

```bash
python tools/device-allocator.py --tests 200 --slots 5 --avg-test-time 45
```

As a rule of thumb: for a 90%+ execution ratio in under 30 minutes, you need:

```
slots = ceil(test_count × avg_test_time_sec / 1800)
```

Example: 200 tests × 45s average = 9,000s total. Divided by 1800 (30 min) = 5 slots.
