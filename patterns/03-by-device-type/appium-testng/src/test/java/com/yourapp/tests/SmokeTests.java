package com.yourapp.tests;

import com.yourapp.base.PCloudyBaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;

public class SmokeTests extends PCloudyBaseTest {

  @Test
  public void launchSmoke() {
    Assert.assertNotNull(getDriver(), "Driver should exist in smoke test");
    System.out.println("Smoke session: " + getDriver().getSessionId());
    openFlightModuleFromHome();
  }
}
