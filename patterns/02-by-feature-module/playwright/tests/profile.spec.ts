import { test, expect } from '@playwright/test';

test('@feature:profile profile panel smoke', async ({ page }) => {
  await page.goto('https://example.com');
  await expect(page.locator('p')).toContainText('illustrative examples');
});
