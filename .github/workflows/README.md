# GitHub Workflow Usage

This folder contains CI workflows for parallel test execution on pCloudy.

## Workflow in use

- [run-parallel-tests.yml](run-parallel-tests.yml)

It runs Pattern 04 round-robin tests in parallel and publishes an execution-ratio report.

## What this workflow does

1. Builds a shard matrix from the requested slot count.
2. Authenticates with pCloudy.
3. Uploads your APK once to pCloudy Drive.
4. Runs Playwright tests in parallel shards.
5. Aggregates shard XML results.
6. Calculates execution ratio with [tools/execution-ratio-calculator.py](../../tools/execution-ratio-calculator.py).
7. Publishes a summary and artifacts in GitHub Actions.

## Triggers

- Push: main, develop
- Pull Request: main
- Manual run: workflow_dispatch

## Required secrets

Set these in GitHub repository settings:

- PCLOUDY_USERNAME
- PCLOUDY_API_KEY

Path: Settings > Secrets and variables > Actions > Secrets.

## Optional repository variable

- PCLOUDY_APP_PATH

Path to APK in the repository.
Default fallback is app/build/app-release.apk.

Path: Settings > Secrets and variables > Actions > Variables.

## Manual run inputs

When running manually from the Actions tab:

- cloud_host
  - Example: device.pcloudy.com
- total_slots
  - Number of parallel shards/slots
  - Example: 5
- app_path
  - APK path in repo (overrides PCLOUDY_APP_PATH)
  - Example: app/build/app-release.apk

## How to run manually

1. Open Actions tab in GitHub.
2. Select pCloudy Parallel Execution.
3. Click Run workflow.
4. Provide optional inputs:
   - cloud_host: device.pcloudy.com
   - total_slots: 5
   - app_path: app/build/app-release.apk
5. Start the run.

## Artifacts and outputs

Per-shard artifacts:

- results-shard-<n>
  - JUnit XML and Playwright report from shard n

Final report artifact:

- execution-ratio-report
  - ratio.json

GitHub Summary includes:

- Pattern name
- Parallel slot count
- Execution ratio
- Target threshold (>90%)

## Local structure expectation

Workflow assumes Playwright project exists at:

- [patterns/04-round-robin/playwright](../../patterns/04-round-robin/playwright)

And that tests write JUnit XML files that can be aggregated.

## Troubleshooting

- App upload fails with "App file not found"
  - Check app_path input or PCLOUDY_APP_PATH variable.
  - Verify APK exists in the repo path at run time.
- Auth token fetch fails
  - Verify PCLOUDY_USERNAME and PCLOUDY_API_KEY are valid.
  - Verify cloud_host value.
- Ratio shows N/A
  - Check if JUnit XML files were produced by shard jobs.
  - Confirm results were uploaded and downloaded correctly.

## Notes

- Current workflow executes round-robin Playwright path.
- total_slots is dynamic and drives shard matrix generation.
