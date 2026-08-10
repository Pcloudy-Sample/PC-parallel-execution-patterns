describe('filters module', () => {
  it('runs filters scenario on allocated slot', async () => {
    await browser.url('https://example.com');
    await expect(browser).toHaveTitle(/Example/);
  });
});
