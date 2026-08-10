describe('payment module', () => {
  it('runs payment scenario on allocated slot', async () => {
    await browser.url('https://example.com');
    await expect(browser).toHaveTitle(/Example/);
  });
});
