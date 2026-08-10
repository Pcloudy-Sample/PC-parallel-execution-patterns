exports.config = {
  logLevel: 'info',
  waitforTimeout: 10000,
  connectionRetryTimeout: 120000,
  connectionRetryCount: 1,
  services: [],
  mochaOpts: {
    ui: 'bdd',
    timeout: 120000
  }
};
