package com.yourapp.tests;

import org.testng.annotations.Test;

public class SearchTests extends BasePatternTest {

  @Test
  public void keywordSearch() {
    runSampleRegression("SearchTests.keywordSearch");
  }
}
