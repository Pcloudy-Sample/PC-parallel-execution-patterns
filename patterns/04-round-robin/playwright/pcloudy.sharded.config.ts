/**
 * pCloudy Parallel Execution — Pattern 04: Round-Robin
 * Playwright Sharded Configuration
 *
 * Playwright's built-in --shard flag distributes tests round-robin
 * across N shards. Each shard runs on a separate pCloudy device slot.
 *
 * Usage (5 slots — run each command in a separate CI job):
 *   npx playwright test --shard=1/5 --config=pcloudy.sharded.config.ts
 *   npx playwright test --shard=2/5 --config=pcloudy.sharded.config.ts
 *   npx playwright test --shard=3/5 --config=pcloudy.sharded.config.ts
 *   npx playwright test --shard=4/5 --config=pcloudy.sharded.config.ts
 *   npx playwright test --shard=5/5 --config=pcloudy.sharded.config.ts
 *
 * In CI/CD, pass SHARD_INDEX and TOTAL_SHARDS from the matrix:
 *   npx playwright test --shard=${SHARD_INDEX}/${TOTAL_SHARDS}
 *
 * Environment variables:
 *   PCLOUDY_USERNAME    — your pCloudy account email
 *   PCLOUDY_API_KEY     — API key from pCloudy dashboard
 *   TOTAL_SHARDS        — total number of parallel slots (default: 5)
 *   SHARD_DEVICE_MAP    — JSON mapping shard index to device name (optional)
 */

import { defineConfig } from '@playwright/test';

const TOTAL_SHARDS = parseInt(process.env.TOTAL_SHARDS || '5', 10);

// Map each shard index to a specific pCloudy device
// If not set, pCloudy auto-assigns an available device
const SHARD_DEVICES: Record<number, string> = process.env.SHARD_DEVICE_MAP
  ? JSON.parse(process.env.SHARD_DEVICE_MAP)
  : {
      1: 'Samsung Galaxy S23',
      2: 'Google Pixel 7',
      3: 'OnePlus 11',
      4: 'Xiaomi 13',
      5: 'Samsung Galaxy A54',
    };

const shardIndex = parseInt(process.env.SHARD_INDEX || '1', 10);
const assignedDevice = SHARD_DEVICES[shardIndex] || 'auto';

export default defineConfig({
  testDir: './tests',

  // Playwright distributes tests round-robin across shards automatically
  fullyParallel: false,   // false within a shard — tests run sequentially per slot
  workers: 1,             // one worker per shard = one pCloudy device per job

  timeout: 90_000,
  retries: 1,

  reporter: [
    ['html',  { outputFolder: `playwright-report/shard-${shardIndex}` }],
    ['junit', { outputFile: `results/shard-${shardIndex}.xml` }],
  ],

  use: {
    // Connect to pCloudy browser / device cloud
    connectOptions: {
      wsEndpoint: 'https://device.pcloudy.com/appiumcloud/wd/hub',
    },
    launchOptions: {
      args: [
        `--pcloudy-username=${process.env.PCLOUDY_USERNAME}`,
        `--pcloudy-api-key=${process.env.PCLOUDY_API_KEY}`,
        `--pcloudy-device=${assignedDevice}`,
      ],
    },

    // Capture evidence on failure
    screenshot: 'only-on-failure',
    video:      'retain-on-failure',
    trace:      'on-first-retry',

    // Navigation timeout
    navigationTimeout: 30_000,
    actionTimeout:     15_000,
  },

  projects: [
    {
      name: `shard-${shardIndex}-of-${TOTAL_SHARDS}`,
      use: {},
    },
  ],
});
