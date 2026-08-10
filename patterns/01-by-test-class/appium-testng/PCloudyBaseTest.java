package com.yourapp.base;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.*;

import java.net.URL;
import java.util.concurrent.TimeUnit;

/**
 * pCloudy Base Test — Pattern 01: By Test Class
 *
 * Reads device configuration from TestNG parameters (set per <test> block
 * in testng-parallel.xml). Each parallel <test> block gets its own driver
 * instance — no sharing between slots.
 *
 * Required system properties (set via -D or CI/CD secrets):
 *   pCloudyUsername  — your pCloudy account email
 *   pCloudyApiKey    — API key from pCloudy dashboard
 *   appUrl           — pCloudy Drive URL for your APK/IPA
 */
public class PCloudyBaseTest {

    // ThreadLocal ensures each parallel test gets its own driver
    protected static ThreadLocal<AppiumDriver> driver = new ThreadLocal<>();

    private static final String PCLOUDY_HUB =
        "https://device.pcloudy.com/appiumcloud/wd/hub";

    @Parameters({"deviceManufacturer", "platformName", "platformVersion"})
    @BeforeMethod
    public void setUp(String deviceManufacturer, String platformName, String platformVersion)
            throws Exception {

        String username = System.getProperty("pCloudyUsername",
                          System.getenv("PCLOUDY_USERNAME"));
        String apiKey   = System.getProperty("pCloudyApiKey",
                          System.getenv("PCLOUDY_API_KEY"));
        String appUrl   = System.getProperty("appUrl",
                          System.getenv("PCLOUDY_APP_URL"));
        String manufacturer = (deviceManufacturer != null && !deviceManufacturer.trim().isEmpty())
            ? deviceManufacturer
            : System.getProperty("pCloudy_DeviceManufacturer", System.getenv("PCLOUDY_DEVICE_MANUFACTURER"));

        if (username == null || apiKey == null) {
            throw new IllegalStateException(
                "PCLOUDY_USERNAME and PCLOUDY_API_KEY must be set");
        }

        DesiredCapabilities caps = new DesiredCapabilities();

        // pCloudy authentication capabilities
        caps.setCapability("pCloudy_Username", username);
        caps.setCapability("pCloudy_ApiKey", apiKey);

        // Device configuration — use manufacturer targeting
        caps.setCapability("pCloudy_DeviceManufacturer", manufacturer);
        caps.setCapability("platformName", platformName);
        caps.setCapability("platformVersion", platformVersion);

        // App — uploaded to pCloudy Drive
        caps.setCapability("pCloudy_AppPackage", appUrl);

        // Performance options
        caps.setCapability("newCommandTimeout", 120);
        caps.setCapability("pCloudy_EnablePerformanceData", true);
        caps.setCapability("pCloudy_EnableVideoRecording", true);

        if ("Android".equalsIgnoreCase(platformName)) {
            caps.setCapability("automationName", "UiAutomator2");
            driver.set(new AndroidDriver(new URL(PCLOUDY_HUB), caps));
        } else if ("iOS".equalsIgnoreCase(platformName)) {
            caps.setCapability("automationName", "XCUITest");
            driver.set(new IOSDriver(new URL(PCLOUDY_HUB), caps));
        }

        driver.get().manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        System.out.printf("Driver initialised — Manufacturer: %s | Platform: %s %s%n",
            manufacturer, platformName, platformVersion);
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        if (driver.get() != null) {
            driver.get().quit();
            driver.remove();
        }
    }

    // Convenience accessor for subclasses
    protected AppiumDriver getDriver() {
        return driver.get();
    }

}
