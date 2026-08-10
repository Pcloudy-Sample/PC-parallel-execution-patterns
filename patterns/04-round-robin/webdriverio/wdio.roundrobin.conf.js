/**
 * pCloudy Parallel Execution — Pattern 04: Round-Robin
 * WebdriverIO Spec Sharding Configuration
 *
 * WebdriverIO's spec sharding divides the full spec list into N equal buckets.
 * Each bucket runs on a separate pCloudy device slot.
 *
 * Usage (run each command in a separate CI job / matrix):
 *   SHARD_INDEX=1 TOTAL_SHARDS=5 npx wdio run wdio.roundrobin.conf.js
 *   SHARD_INDEX=2 TOTAL_SHARDS=5 npx wdio run wdio.roundrobin.conf.js
 *   ...
 *
 * Environment variables:
 *   PCLOUDY_USERNAME    — your pCloudy account email
 *   PCLOUDY_API_KEY     — API key from pCloudy dashboard
 *   PCLOUDY_APP_URL     — pCloudy Drive URL for your APK
 *   SHARD_INDEX         — current shard number (1-based)
 *   TOTAL_SHARDS        — total number of parallel slots
 */

const glob   = require('glob');
const path   = require('path');

const PCLOUDY_HUB  = 'https://device.pcloudy.com/appiumcloud/wd/hub';
const USERNAME     = process.env.PCLOUDY_USERNAME;
const API_KEY      = process.env.PCLOUDY_API_KEY;
const APP_URL      = process.env.PCLOUDY_APP_URL;
const SHARD_INDEX  = parseInt(process.env.SHARD_INDEX  || '1',  10);
const TOTAL_SHARDS = parseInt(process.env.TOTAL_SHARDS || '5', 10);

// ── Round-robin spec allocation ───────────────────────────────
// Get ALL spec files, then take every Nth one starting at shard offset.
// This distributes evenly regardless of how many specs exist.
function getShardedSpecs(shardIndex, totalShards) {
  const allSpecs = glob.sync('./test/specs/**/*.spec.js').sort();

  // Round-robin: shard 1 gets indices 0, 5, 10... | shard 2 gets 1, 6, 11...
  return allSpecs.filter((_, i) => i % totalShards === (shardIndex - 1));
}

const mySpecs = getShardedSpecs(SHARD_INDEX, TOTAL_SHARDS);
console.log(`Shard ${SHARD_INDEX}/${TOTAL_SHARDS}: running ${mySpecs.length} specs`);

// ── Device assignment per shard ───────────────────────────────
const DEVICES = [
  'Samsung Galaxy S23',
  'Google Pixel 7',
  'OnePlus 11',
  'Xiaomi 13',
  'Samsung Galaxy A54',
];
const assignedDevice = DEVICES[(SHARD_INDEX - 1) % DEVICES.length];

// ── WebdriverIO config ────────────────────────────────────────
exports.config = {
  runner: 'local',
  hostname: 'device.pcloudy.com',
  port: 443,
  path: '/appiumcloud/wd/hub',
  protocol: 'https',

  specs: mySpecs,    // only this shard's allocated specs
  maxInstances: 1,   // one session per shard job

  capabilities: [{
    platformName:             'Android',
    'appium:deviceName':      assignedDevice,
    'appium:platformVersion': '13.0',
    'appium:automationName':  'UiAutomator2',
    'appium:app':             APP_URL,
    'appium:newCommandTimeout': 120,

    // pCloudy authentication
    pCloudy_Username:               USERNAME,
    pCloudy_ApiKey:                 API_KEY,
    pCloudy_EnableVideoRecording:   true,
    pCloudy_EnablePerformanceData:  true,
  }],

  framework: 'mocha',

  reporters: [
    'spec',
    ['junit', {
      outputDir: './results',
      outputFileFormat: () => `shard-${SHARD_INDEX}-of-${TOTAL_SHARDS}.xml`,
    }],
  ],

  mochaOpts: { timeout: 90000 },

  onPrepare() {
    console.log(`\nRound-Robin Shard ${SHARD_INDEX}/${TOTAL_SHARDS}`);
    console.log(`Device: ${assignedDevice}`);
    console.log(`Specs:  ${mySpecs.length} files\n`);
  },
};
