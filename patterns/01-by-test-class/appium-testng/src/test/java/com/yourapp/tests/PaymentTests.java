package com.yourapp.tests;

import org.testng.annotations.Test;

public class PaymentTests extends BasePatternTest {

  @Test
  public void paymentWithCard() {
    runSampleRegression("PaymentTests.paymentWithCard");
  }
}
