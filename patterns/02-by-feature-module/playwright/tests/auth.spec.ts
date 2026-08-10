import { test, expect } from '@playwright/test';

test('@feature:auth login screen loads', async ({ page }) => {
  await page.goto('https://example.com');
  await expect(page).toHaveTitle(/Example/);
});
