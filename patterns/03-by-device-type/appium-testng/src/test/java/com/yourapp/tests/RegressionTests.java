package com.yourapp.tests;

import com.yourapp.base.PCloudyBaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;

public class RegressionTests extends PCloudyBaseTest {

  @Test
  public void checkoutRegression() {
    Assert.assertNotNull(getDriver(), "Driver should exist in regression test");
    System.out.println("Regression session: " + getDriver().getSessionId());
    runSampleFlightSearch();
  }
}
