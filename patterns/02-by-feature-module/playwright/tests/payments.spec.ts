import { test, expect } from '@playwright/test';

test('@feature:payments payment page smoke', async ({ page }) => {
  await page.goto('https://example.com');
  await expect(page.locator('h1')).toContainText('Example Domain');
});
