describe('search module', () => {
  it('runs search scenario on allocated slot', async () => {
    await browser.url('https://example.com');
    await expect(browser).toHaveTitle(/Example/);
  });
});
