package com.yourapp.base;

import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.http.ClientConfig;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public class PCloudySeleniumBase {

  private static final ThreadLocal<RemoteWebDriver> DRIVER = new ThreadLocal<>();

  @BeforeMethod(alwaysRun = true)
  public void setUp() throws Exception {
    String userName = read("userName", "PCLOUDY_USERNAME");
    String accessKey = read("accessKey", "PCLOUDY_API_KEY");
    String baseUrl = System.getProperty("baseUrl", "https://browser.device.pcloudy.com");
    String browserName = System.getProperty("browserName", "chrome");
    String os = System.getProperty("os", "Android");
    String local = System.getProperty("local", "false");
    String autoHeal = System.getProperty("autoHeal", "false");
    String queueTimeout = System.getProperty("queueTimeout", "None");
    String idleTimeout = System.getProperty("idleTimeout", "None");
    String project = System.getProperty("project", "parallel-execution-patterns");
    String build = System.getProperty("build", "pattern-04-selenium");
    String name = System.getProperty("name", "round-robin-suite");
    String tag = System.getProperty("tag", "round-robin");

    DesiredCapabilities capabilities = new DesiredCapabilities();
    capabilities.setCapability("browserName", browserName);

    Map<String, Object> pcloudyOptions = new HashMap<String, Object>();
    pcloudyOptions.put("userName", userName);
    pcloudyOptions.put("accessKey", accessKey);
    pcloudyOptions.put("os", os);
    pcloudyOptions.put("local", stringToBoolean(local));
    pcloudyOptions.put("pCloudy_EnableVideo", true);
    pcloudyOptions.put("autoHeal", stringToBoolean(autoHeal));
    if (!"None".equals(queueTimeout)) {
      pcloudyOptions.put("queueTimeout", stringToInteger(queueTimeout));
    }
    if (!"None".equals(idleTimeout)) {
      pcloudyOptions.put("idleTimeout", stringToInteger(idleTimeout));
    }
    if (!"None".equals(project)) {
      pcloudyOptions.put("project", project);
    }
    if (!"None".equals(build)) {
      pcloudyOptions.put("build", build);
    }
    if (!"None".equals(name)) {
      pcloudyOptions.put("name", name);
    }
    if (!"None".equals(tag)) {
      pcloudyOptions.put("tag", tag);
    }
    capabilities.setCapability("pcloudy:options", pcloudyOptions);

    ClientConfig clientConfig =
        ClientConfig.defaultConfig()
            .readTimeout(Duration.ofMinutes(15))
            .connectionTimeout(Duration.ofMinutes(15));

    RemoteWebDriver wd =
        (RemoteWebDriver)
            RemoteWebDriver.builder()
                .address(new URL(baseUrl + "/seleniumcloud/wd/hub"))
                .oneOf(capabilities)
                .config(clientConfig)
                .build();
    wd.manage().timeouts().implicitlyWait(Duration.ofSeconds(8));
    DRIVER.set(wd);
  }

  @AfterMethod(alwaysRun = true)
  public void tearDown(ITestResult result) {
    if (DRIVER.get() != null) {
      String status = result != null && result.isSuccess() ? "pass" : "fail";
      DRIVER.get().executeScript("pCloudy_setResult", status);
      DRIVER.get().quit();
      DRIVER.remove();
    }
  }

  protected RemoteWebDriver getDriver() {
    return DRIVER.get();
  }

  protected String runSampleWebForm(String inputText) {
    RemoteWebDriver driver = getDriver();
    driver.manage().window().maximize();
    driver.get("https://www.selenium.dev/selenium/web/web-form.html");

    WebElement textBox = waitForVisibility(By.name("my-text"), driver);
    textBox.sendKeys(inputText);

    WebElement submitButton = waitForVisibility(By.cssSelector("button"), driver);
    submitButton.click();

    WebElement message = waitForVisibility(By.id("message"), driver);
    return message.getText();
  }

  protected WebElement waitForVisibility(By locator, RemoteWebDriver driver) {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
  }

  private String read(String property, String env) {
    String value = System.getProperty(property, "");
    if (value.isEmpty()) {
      String envValue = System.getenv(env);
      return envValue == null ? "" : envValue;
    }
    return value;
  }

  private boolean stringToBoolean(String value) {
    if (value == null) {
      throw new IllegalArgumentException("Boolean input cannot be null");
    }
    String lower = value.trim().toLowerCase();
    if (!"true".equals(lower) && !"false".equals(lower)) {
      throw new IllegalArgumentException("Boolean value must be true/false, got: " + value);
    }
    return Boolean.parseBoolean(lower);
  }

  private int stringToInteger(String value) {
    if (value == null) {
      throw new IllegalArgumentException("Integer input cannot be null");
    }
    try {
      return Integer.parseInt(value.trim());
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("Invalid integer value: " + value);
    }
  }
}
