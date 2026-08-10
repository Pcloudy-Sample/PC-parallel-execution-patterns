package com.yourapp.tests;

import org.testng.annotations.Test;

public class CheckoutTests extends BasePatternTest {

  @Test
  public void checkoutFlow() {
    runSampleRegression("CheckoutTests.checkoutFlow");
  }
}
