package com.yourapp.tests;

import org.testng.annotations.Test;

public class SessionTests extends BasePatternTest {

  @Test
  public void sessionLifecycle() {
    runSampleSmoke("SessionTests.sessionLifecycle");
  }
}
