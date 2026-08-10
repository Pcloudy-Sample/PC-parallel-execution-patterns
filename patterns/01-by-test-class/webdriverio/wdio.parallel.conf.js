/**
 * pCloudy Parallel Execution — Pattern 01: By Test Class
 * WebdriverIO Configuration
 *
 * Each entry in `capabilities` maps to a separate pCloudy device slot.
 * Tests are split by spec file (one file = one class = one slot).
 *
 * Usage:
 *   npx wdio run wdio.parallel.conf.js
 *
 * Environment variables required:
 *   PCLOUDY_USERNAME   — your pCloudy account email
 *   PCLOUDY_API_KEY    — API key from pCloudy dashboard
 *   PCLOUDY_APP_URL    — pCloudy Drive URL for your APK
 */

const { config: baseConfig } = require('./wdio.base.conf.js');

const PCLOUDY_HUB = 'https://device.pcloudy.com/appiumcloud/wd/hub';
const USERNAME    = process.env.PCLOUDY_USERNAME;
const API_KEY     = process.env.PCLOUDY_API_KEY;
const APP_URL     = process.env.PCLOUDY_APP_URL;

// ── Device slot definitions ───────────────────────────────────
// Each object = one pCloudy device slot running a specific spec file.
// Match maxInstances to your pCloudy plan's parallel slot count.

const deviceSlots = [
    {
        specs: ['./test/specs/login.spec.js', './test/specs/session.spec.js'],
        device: 'Samsung Galaxy S23',
        platformVersion: '13.0',
    },
    {
        specs: ['./test/specs/checkout.spec.js', './test/specs/payment.spec.js'],
        device: 'Google Pixel 7',
        platformVersion: '13.0',
    },
    {
        specs: ['./test/specs/profile.spec.js', './test/specs/settings.spec.js'],
        device: 'OnePlus 11',
        platformVersion: '13.0',
    },
    {
        specs: ['./test/specs/notifications.spec.js'],
        device: 'Xiaomi 13',
        platformVersion: '13.0',
    },
    {
        specs: ['./test/specs/search.spec.js', './test/specs/filters.spec.js'],
        device: 'Samsung Galaxy A54',
        platformVersion: '13.0',
    },
];

// ── Build capabilities array from slot definitions ────────────
const capabilities = deviceSlots.map(slot => ({
    // WebdriverIO will only run the listed specs for this capability
    specs: slot.specs,

    // pCloudy capabilities
    'appium:deviceName':      slot.device,
    'appium:platformVersion': slot.platformVersion,
    'platformName':           'Android',
    'appium:automationName':  'UiAutomator2',
    'appium:app':             APP_URL,

    // pCloudy authentication
    'pCloudy_Username': USERNAME,
    'pCloudy_ApiKey':   API_KEY,

    // Options
    'pCloudy_EnableVideoRecording':    true,
    'pCloudy_EnablePerformanceData':   true,
    'appium:newCommandTimeout':        120,
}));

exports.config = {
    ...baseConfig,

    runner: 'local',
    hostname: 'device.pcloudy.com',
    port: 443,
    path: '/appiumcloud/wd/hub',
    protocol: 'https',

    // One WebdriverIO worker per device slot
    maxInstances: deviceSlots.length,

    capabilities,

    framework: 'mocha',
    reporters: [
        'spec',
        ['junit', {
            outputDir: './results/junit',
            outputFileFormat: opts =>
                `results-${opts.capabilities['appium:deviceName']
                    .replace(/\s+/g, '-')}.xml`,
        }],
    ],

    mochaOpts: {
        timeout: 120000,
    },

    beforeSession(config, capabilities) {
        console.log(`Starting session on: ${capabilities['appium:deviceName']}`);
    },

    afterSession() {
        console.log('Session ended — device released by pCloudy automatically');
    },
};
