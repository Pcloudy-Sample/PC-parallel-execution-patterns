package com.yourapp.tests;

import org.testng.annotations.Test;

public class NotificationTests extends BasePatternTest {

  @Test
  public void notificationOpen() {
    runSampleSmoke("NotificationTests.notificationOpen");
  }
}
