import { test, expect } from '@playwright/test';

test('@feature:search search entry smoke', async ({ page }) => {
  await page.goto('https://example.com');
  await expect(page.locator('a')).toHaveAttribute('href', /iana/);
});
