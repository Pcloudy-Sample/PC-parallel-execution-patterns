package com.yourapp.tests;

import com.yourapp.base.PCloudySeleniumBase;
import org.testng.Assert;
import org.testng.annotations.Test;

public class SmokeTests extends PCloudySeleniumBase {

  @Test
  public void smokeHomePage() {
    String message = runSampleWebForm("Smoke test from pCloudy pattern 03");
    Assert.assertTrue(message.toLowerCase().contains("received"), "Expected confirmation message");
  }
}
