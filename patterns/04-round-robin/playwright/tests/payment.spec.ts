import { test, expect } from '@playwright/test';

test('payment round robin smoke', async ({ page }) => {
  await page.goto('https://example.com');
  await expect(page).toHaveTitle(/Example/);
});
