# Pcloudy Parallel Execution Patterns

> **Four production-tested patterns for distributing test suites across Pcloudy's real device cloud — covering Appium, Selenium, WebdriverIO, and Playwright.**

<p align="center">
  <a href="https://www.pcloudy.com"><img alt="Pcloudy" src="https://img.shields.io/badge/Made%20by-Pcloudy-1a73e8"></a>
  <img alt="Patterns" src="https://img.shields.io/badge/patterns-4-blue">
  <img alt="Frameworks" src="https://img.shields.io/badge/frameworks-Appium%20%7C%20Selenium%20%7C%20WebdriverIO%20%7C%20Playwright-teal">
  <img alt="License" src="https://img.shields.io/badge/license-MIT-green">
</p>

---

## The problem this repo solves

AI test generation tools create tests faster than most pipelines can execute them. A team using QPilot or any AI generation tool can produce 50 test cases in 3 minutes. Whether those 50 tests execute before the release window closes is a **parallel execution infrastructure question**, not a generation question.

The execution ratio — tests executed ÷ tests scheduled × 100 — is the metric that exposes this gap. Teams below 70% are generating faster than they execute. The fix is not fewer tests. It is smarter distribution across parallel device slots.

This repo provides four patterns for doing exactly that.

---

## Pattern index

| # | Pattern | Best for | Execution ratio impact |
|---|---------|----------|----------------------|
| [01](patterns/01-by-test-class/) | **By Test Class / Suite** | Teams organised by test module or feature | High — predictable slot usage |
| [02](patterns/02-by-feature-module/) | **By Feature Module** | Feature-team structures, tagged test suites | High — isolates failures by domain |
| [03](patterns/03-by-device-type/) | **By Device Type** | Coverage-optimised testing (manufacturer buckets + browser tiers) | Medium — same tests, different matrix |
| [04](patterns/04-round-robin/) | **Round-Robin Distribution** | Maximum device utilisation, uneven test sizes | Highest — minimises idle slot time |

---

## Quick decision guide

```
Do you want maximum device coverage (same tests, many device types)?
  → Pattern 03: By Device Type

Do you want maximum speed (fewest minutes from commit to results)?
  → Pattern 04: Round-Robin

Are your tests organised by feature or module already?
  → Pattern 02: By Feature Module

Are you starting out and want the simplest setup?
  → Pattern 01: By Test Class
```

Full decision guide: [docs/choosing-a-pattern.md](docs/choosing-a-pattern.md)

---

## Framework support

| Pattern | Appium + TestNG | WebdriverIO | Playwright | Selenium |
|---------|:-:|:-:|:-:|:-:|
| 01 By Test Class | ✅ | ✅ | | |
| 02 By Feature Module | ✅ | | ✅ | |
| 03 By Device Type | ✅ | | | ✅ |
| 04 Round-Robin | ✅ | ✅ | ✅ | ✅ |

---

## Prerequisites

All patterns connect to Pcloudy's device cloud. You need:

| Variable | Where to find it |
|---|---|
| `PCLOUDY_USERNAME` | Your Pcloudy account email |
| `PCLOUDY_API_KEY` | Pcloudy dashboard → Settings → API Key |

Store both as secrets in your CI/CD platform. Never hardcode in test files.

---

## Repository structure

```
parallel-execution-patterns/
├── README.md
├── LICENSE
├── CONTRIBUTING.md
├── docs/
│   └── choosing-a-pattern.md      ← full decision guide
├── patterns/
│   ├── 01-by-test-class/
│   │   ├── README.md
│   │   ├── appium-testng/         ← Java + TestNG
│   │   └── webdriverio/           ← Node.js
│   ├── 02-by-feature-module/
│   │   ├── README.md
│   │   ├── appium-testng/
│   │   └── playwright/
│   ├── 03-by-device-type/
│   │   ├── README.md
│   │   ├── appium-testng/
│   │   └── selenium/
│   └── 04-round-robin/
│       ├── README.md
│       ├── appium-testng/
│       ├── webdriverio/
│       ├── playwright/
│       └── selenium/
└── tools/
    ├── execution-ratio-calculator.py   ← calculates ratio from JUnit XML
    ├── device-allocator.py             ← recommends device split for your suite
    └── slot-utilisation-report.py      ← shows idle vs active time per slot
```

---

## The execution ratio metric

Every pattern in this repo is designed around one metric:

```
Execution ratio = tests executed / tests scheduled × 100

Target: > 90%
Warning: 70–90% — pipeline bottleneck, add parallel slots
Critical: < 70% — generating faster than executing, structural issue
```

The `tools/` folder includes a calculator that reads JUnit XML output and computes your execution ratio automatically. Run it after any test suite to baseline where you are before changing patterns.

---

## Regulated enterprise note

All patterns work with Pcloudy's **Lab-in-a-Box** on-premise deployment. Replace `baseUrl` defaults with your internal endpoint:

- Appium patterns: `https://<your-host>/appiumcloud/wd/hub`
- Selenium patterns: `https://<your-host>/seleniumcloud/wd/hub`

PCI-DSS, HIPAA, RBI, and SOC 2 compliant configurations are supported. See each pattern's README for on-premise configuration notes.

---

## Tools

Three utility scripts in `tools/` that work independently of any pattern:

**`execution-ratio-calculator.py`** — reads JUnit XML reports and outputs your execution ratio. Run after every suite as a baseline.

**`device-allocator.py`** — given your test count and Pcloudy slot count, recommends which pattern to use and how to split the suite.

**`slot-utilisation-report.py`** — parses Pcloudy execution logs to show how much time each device slot was active vs idle. Idle time above 20% usually means a different pattern would perform better.

---

## Resources

- [Pcloudy documentation](https://www.pcloudy.com/docs/)
- [Pcloudy API reference](https://www.pcloudy.com/docs/api-reference)
---

## License

MIT — see [LICENSE](LICENSE).

---

*Built by [Pcloudy](https://www.pcloudy.com) — quality infrastructure for enterprise mobile testing.*
