import { test, expect } from '@playwright/test';

test('search round robin smoke', async ({ page }) => {
  await page.goto('https://example.com');
  await expect(page).toHaveTitle(/Example/);
});
