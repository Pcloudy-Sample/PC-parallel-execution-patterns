package com.yourapp.tests;

import com.yourapp.base.PCloudyBaseTest;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class RoundRobinTests extends PCloudyBaseTest {

  private static final String[] TEST_KEYS = {
    "auth-login", "auth-session", "checkout-cart", "checkout-payment",
    "profile-edit", "profile-avatar", "search-keyword", "search-filter",
    "notifications-open", "notifications-clear"
  };

  @DataProvider(name = "workItems")
  public Object[][] workItems() {
    Object[][] data = new Object[TEST_KEYS.length][2];
    for (int i = 0; i < TEST_KEYS.length; i++) {
      data[i][0] = i;
      data[i][1] = TEST_KEYS[i];
    }
    return data;
  }

  @Test(dataProvider = "workItems")
  public void runAssignedCase(int index, String testKey) {
    int shardIndex = Integer.parseInt(System.getProperty("SHARD_INDEX", "1"));
    int totalShards = Integer.parseInt(System.getProperty("TOTAL_SHARDS", "5"));

    if ((index % totalShards) != (shardIndex - 1)) {
      throw new SkipException("Case " + testKey + " belongs to different shard");
    }

    Assert.assertNotNull(getDriver(), "Driver should exist for shard execution");
    System.out.println("Executing " + testKey + " on shard " + shardIndex + "/" + totalShards);

    if (testKey.contains("checkout") || testKey.contains("payment") || testKey.contains("search")) {
      runSampleFlightSearch();
      return;
    }

    openFlightModuleFromHome();
  }
}
