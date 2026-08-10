describe('login sharded flow', () => {
  it('runs in assigned shard', async () => {
    await browser.url('https://example.com');
    await expect(browser).toHaveTitle(/Example/);
  });
});
