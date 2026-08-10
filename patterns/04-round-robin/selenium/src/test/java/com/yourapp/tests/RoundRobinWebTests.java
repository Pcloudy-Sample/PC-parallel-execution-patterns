package com.yourapp.tests;

import com.yourapp.base.PCloudySeleniumBase;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class RoundRobinWebTests extends PCloudySeleniumBase {

  private static final String[] CASES = {
    "home", "signup", "search", "checkout", "payment", "profile", "help", "support"
  };

  @DataProvider(name = "webCases")
  public Object[][] webCases() {
    Object[][] data = new Object[CASES.length][2];
    for (int i = 0; i < CASES.length; i++) {
      data[i][0] = i;
      data[i][1] = CASES[i];
    }
    return data;
  }

  @Test(dataProvider = "webCases")
  public void executeShardCase(int index, String name) {
    int shardIndex = Integer.parseInt(System.getProperty("SHARD_INDEX", "1"));
    int totalShards = Integer.parseInt(System.getProperty("TOTAL_SHARDS", "5"));

    if ((index % totalShards) != (shardIndex - 1)) {
      throw new SkipException("Skipping " + name + " for this shard");
    }

    String message = runSampleWebForm("Round robin case: " + name);
    Assert.assertTrue(message.toLowerCase().contains("received"), "Expected confirmation for " + name);
  }
}
