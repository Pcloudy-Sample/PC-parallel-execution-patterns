package com.yourapp.tests;

import com.yourapp.base.PCloudyBaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;

public class FeatureTests extends PCloudyBaseTest {

  private void runTaggedSmoke(String name) {
    Assert.assertNotNull(getDriver(), "Missing driver for " + name);
    System.out.println("Executing sample smoke flow: " + name + " on " + getDriver().getSessionId());
    openFlightModuleFromHome();
  }

  private void runTaggedRegression(String name) {
    Assert.assertNotNull(getDriver(), "Missing driver for " + name);
    System.out.println("Executing sample regression flow: " + name + " on " + getDriver().getSessionId());
    runSampleFlightSearch();
  }

  @Test(groups = {"feature:auth"})
  public void authLogin() {
    runTaggedSmoke("feature:auth");
  }

  @Test(groups = {"feature:payments"})
  public void paymentsCheckout() {
    runTaggedRegression("feature:payments");
  }

  @Test(groups = {"feature:profile"})
  public void profileUpdate() {
    runTaggedRegression("feature:profile");
  }

  @Test(groups = {"feature:search"})
  public void searchCatalog() {
    runTaggedRegression("feature:search");
  }
}
