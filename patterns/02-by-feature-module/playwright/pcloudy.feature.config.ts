/**
 * pCloudy Parallel Execution — Pattern 02: By Feature Module
 * Playwright Configuration (TypeScript)
 *
 * Uses Playwright's project system to route feature-tagged tests
 * to separate pCloudy device sessions running in parallel.
 *
 * Tagging convention:
 *   test.describe('@feature:auth', () => { ... })
 *   Or use test.describe with grep filtering per project.
 *
 * Usage:
 *   npx playwright test --config=pcloudy.feature.config.ts
 *
 * Environment variables:
 *   PCLOUDY_USERNAME   — your pCloudy account email
 *   PCLOUDY_API_KEY    — API key from pCloudy dashboard
 */

import { defineConfig, devices } from '@playwright/test';

const PCLOUDY_HUB = 'https://device.pcloudy.com/appiumcloud/wd/hub';

// Shared pCloudy connection options
const pCloudyBase = {
  connectOptions: {
    wsEndpoint: PCLOUDY_HUB,
  },
  use: {
    // pCloudy authentication passed as browser launch args
    launchOptions: {
      args: [
        `--pcloudy-username=${process.env.PCLOUDY_USERNAME}`,
        `--pcloudy-api-key=${process.env.PCLOUDY_API_KEY}`,
      ],
    },
    // Capture on failure
    screenshot: 'only-on-failure',
    video: 'retain-on-failure',
    trace: 'on-first-retry',
  },
};

export default defineConfig({
  // Run each project (feature × device) in parallel
  fullyParallel: true,
  workers: 4,   // match your pCloudy plan's parallel slot count

  // Global timeout per test
  timeout: 90_000,
  retries: 1,

  reporter: [
    ['html', { outputFolder: 'playwright-report' }],
    ['junit', { outputFile: 'results/junit-results.xml' }],
  ],

  projects: [
    // ── Slot 1: Authentication feature ───────────────────────
    {
      name: 'auth-samsung-s23',
      grep: /@feature:auth/,       // only run tests tagged @feature:auth
      use: {
        ...pCloudyBase.use,
        // pCloudy device capabilities for mobile web / hybrid
        browserName: 'chromium',
        channel: 'chrome',
        // For native mobile use Appium + WebDriver instead
        viewport: { width: 390, height: 844 },  // iPhone 14 equivalent
        userAgent: 'Mozilla/5.0 (Linux; Android 13; SM-S918B)',
        // Device-specific metadata passed to pCloudy
        extraHTTPHeaders: {
          'X-PCloud-Device': 'Samsung Galaxy S23',
          'X-PCloud-Platform': 'Android',
          'X-PCloud-Version': '13.0',
        },
      },
      testDir: './tests',
    },

    // ── Slot 2: Payments feature ──────────────────────────────
    {
      name: 'payments-pixel-7',
      grep: /@feature:payments/,
      use: {
        ...pCloudyBase.use,
        browserName: 'chromium',
        viewport: { width: 412, height: 915 },
        extraHTTPHeaders: {
          'X-PCloud-Device': 'Google Pixel 7',
          'X-PCloud-Platform': 'Android',
          'X-PCloud-Version': '13.0',
        },
      },
      testDir: './tests',
    },

    // ── Slot 3: Profile feature ───────────────────────────────
    {
      name: 'profile-oneplus-11',
      grep: /@feature:profile/,
      use: {
        ...pCloudyBase.use,
        browserName: 'chromium',
        viewport: { width: 412, height: 919 },
        extraHTTPHeaders: {
          'X-PCloud-Device': 'OnePlus 11',
          'X-PCloud-Platform': 'Android',
          'X-PCloud-Version': '13.0',
        },
      },
      testDir: './tests',
    },

    // ── Slot 4: Search feature ────────────────────────────────
    {
      name: 'search-xiaomi-13',
      grep: /@feature:search/,
      use: {
        ...pCloudyBase.use,
        browserName: 'chromium',
        viewport: { width: 393, height: 873 },
        extraHTTPHeaders: {
          'X-PCloud-Device': 'Xiaomi 13',
          'X-PCloud-Platform': 'Android',
          'X-PCloud-Version': '13.0',
        },
      },
      testDir: './tests',
    },
  ],
});
