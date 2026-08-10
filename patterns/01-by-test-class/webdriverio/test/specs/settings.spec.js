describe('settings module', () => {
  it('runs settings scenario on allocated slot', async () => {
    await browser.url('https://example.com');
    await expect(browser).toHaveTitle(/Example/);
  });
});
