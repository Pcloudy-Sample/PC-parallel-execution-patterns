package com.yourapp.tests;

import com.yourapp.base.PCloudyBaseTest;
import org.testng.Assert;

public abstract class BasePatternTest extends PCloudyBaseTest {

  protected void verifySession(String testName) {
    Assert.assertNotNull(getDriver(), "Driver was not created for test: " + testName);
    System.out.println("Running " + testName + " on session: " + getDriver().getSessionId());
  }

  protected void runSampleSmoke(String testName) {
    verifySession(testName);
    openFlightModuleFromHome();
  }

  protected void runSampleRegression(String testName) {
    verifySession(testName);
    runSampleFlightSearch();
  }
}
