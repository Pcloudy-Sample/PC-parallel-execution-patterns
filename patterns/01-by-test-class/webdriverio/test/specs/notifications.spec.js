describe('notifications module', () => {
  it('runs notifications scenario on allocated slot', async () => {
    await browser.url('https://example.com');
    await expect(browser).toHaveTitle(/Example/);
  });
});
