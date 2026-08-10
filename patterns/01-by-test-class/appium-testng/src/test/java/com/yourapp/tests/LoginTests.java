package com.yourapp.tests;

import org.testng.annotations.Test;

public class LoginTests extends BasePatternTest {

  @Test
  public void loginHappyPath() {
    runSampleSmoke("LoginTests.loginHappyPath");
  }
}
