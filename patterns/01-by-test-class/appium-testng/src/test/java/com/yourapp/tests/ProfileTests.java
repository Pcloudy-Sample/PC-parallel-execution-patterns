package com.yourapp.tests;

import org.testng.annotations.Test;

public class ProfileTests extends BasePatternTest {

  @Test
  public void profileUpdate() {
    runSampleRegression("ProfileTests.profileUpdate");
  }
}
