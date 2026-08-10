package com.yourapp.base;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public class PCloudyBaseTest {

  private static final ThreadLocal<AppiumDriver> DRIVER = new ThreadLocal<>();

  @BeforeMethod(alwaysRun = true)
  public void setUp() throws Exception {
    String userName = read("userName", "PCLOUDY_USERNAME");
    String apiKey = read("accessKey", "PCLOUDY_API_KEY");
    String appName = read("pCloudy_ApplicationName", "PCLOUDY_APP_NAME");
    String manufacturer = read("pCloudy_DeviceManufacturer", "PCLOUDY_DEVICE_MANUFACTURER");
    if (manufacturer.isEmpty()) {
      manufacturer = "Samsung";
    }
    String baseUrl = System.getProperty("baseUrl", "https://device.pcloudy.com/appiumcloud/wd/hub");

    DesiredCapabilities capabilities = new DesiredCapabilities();
    capabilities.setCapability("appium:platformName", "Android");
    capabilities.setCapability("appium:automationName", "UiAutomator2");

    Map<String, Object> pcloudyOptions = new HashMap<String, Object>();
    pcloudyOptions.put("pCloudy_Username", userName);
    pcloudyOptions.put("pCloudy_ApiKey", apiKey);
    if (!manufacturer.isEmpty()) {
      pcloudyOptions.put("pCloudy_DeviceManufacturer", manufacturer);
    }
    pcloudyOptions.put("appiumVersion", "2.0.0");
    if (!appName.isEmpty()) {
      pcloudyOptions.put("pCloudy_ApplicationName", appName);
    }
    capabilities.setCapability("pcloudy:options", pcloudyOptions);

    DRIVER.set(new AndroidDriver(new URL(baseUrl), capabilities));
    DRIVER.get().manage().timeouts().implicitlyWait(Duration.ofSeconds(8));
  }

  @AfterMethod(alwaysRun = true)
  public void tearDown() {
    if (DRIVER.get() != null) {
      DRIVER.get().quit();
      DRIVER.remove();
    }
  }

  protected AppiumDriver getDriver() {
    return DRIVER.get();
  }

    protected WebElement waitVisible(By locator) {
    WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(12));
    return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected void openFlightModuleFromHome() {
    waitVisible(By.xpath("//android.widget.Button[@resource-id='com.pcloudy.appiumdemo:id/accept']"))
      .click();
    waitVisible(By.xpath("//android.widget.Button[@resource-id='com.pcloudy.appiumdemo:id/flightButton']"))
      .click();
    }

    protected void runSampleFlightSearch() {
    openFlightModuleFromHome();

    waitVisible(By.xpath("//android.widget.Spinner[@resource-id='com.pcloudy.appiumdemo:id/spinnerfrom']"))
      .click();
    waitVisible(
        By.xpath(
          "//android.widget.CheckedTextView[@resource-id='android:id/text1' and @text='Bangalore, India (BLR)']"))
      .click();

    waitVisible(By.xpath("//android.widget.Spinner[@resource-id='com.pcloudy.appiumdemo:id/spinnerto']"))
      .click();
    waitVisible(
        By.xpath(
          "//android.widget.CheckedTextView[@resource-id='android:id/text1' and @text='Pune, India (PNQ)']"))
      .click();

    waitVisible(By.xpath("//android.widget.RadioButton[@resource-id='com.pcloudy.appiumdemo:id/singleTrip']"))
      .click();
    waitVisible(By.xpath("//android.widget.TextView[@resource-id='com.pcloudy.appiumdemo:id/txtdepart']"))
      .click();
    waitVisible(By.xpath("//android.widget.Button[@resource-id='android:id/button1' and @text='OK']"))
      .click();
    waitVisible(By.xpath("//android.widget.Button[@resource-id='com.pcloudy.appiumdemo:id/searchFlights']"))
      .click();
    }

  private String read(String property, String env) {
    String value = System.getProperty(property, "");
    if (value.isEmpty()) {
      String envValue = System.getenv(env);
      return envValue == null ? "" : envValue;
    }
    return value;
  }

}
