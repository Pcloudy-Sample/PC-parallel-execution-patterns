package com.yourapp.tests;

import com.yourapp.base.PCloudySeleniumBase;
import org.testng.Assert;
import org.testng.annotations.Test;

public class RegressionTests extends PCloudySeleniumBase {

  @Test
  public void regressionContentCheck() {
    String message = runSampleWebForm("Regression test from pCloudy pattern 03");
    Assert.assertTrue(message.toLowerCase().contains("received"), "Expected confirmation message");
  }
}
