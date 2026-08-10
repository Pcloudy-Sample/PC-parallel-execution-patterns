package com.yourapp.config;

import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.http.ClientConfig;
import org.testng.annotations.*;

import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * pCloudy Parallel Execution — Pattern 03: By Device Type
 * Selenium Remote WebDriver configuration for pCloudy browser cloud
 *
 * For web / hybrid app testing across different browser + OS combinations.
 * Each @Test(groups = "tier:flagship") etc. maps to a parallel slot.
 *
 * Required environment variables:
 *   PCLOUDY_USERNAME   — your pCloudy account email
 *   PCLOUDY_API_KEY    — API key from pCloudy dashboard
 */
public class PCloudySeleniumBase {

    protected static ThreadLocal<WebDriver> driver = new ThreadLocal<>();

    private static final String PCLOUDY_GRID =
        "https://browser.device.pcloudy.com/seleniumcloud/wd/hub";

    /**
     * Browser tier configurations.
     * Each entry represents one parallel pCloudy browser slot.
     */
    enum BrowserTier {
        CHROME("Chrome", "Windows"),
        FIREFOX("Firefox", "Windows"),
        EDGE("Edge", "Windows"),
        SAFARI("Safari", "macOS");

        final String browser;
        final String os;

        BrowserTier(String browser, String os) {
            this.browser = browser;
            this.os = os;
        }
    }

    @Parameters({"browserTier"})
    @BeforeMethod
    public void setUp(@Optional("chrome") String browserTierName) throws Exception {
        BrowserTier tier = BrowserTier.valueOf(browserTierName.toUpperCase().replace("-", "_"));

        String username = System.getenv("PCLOUDY_USERNAME");
        String apiKey   = System.getenv("PCLOUDY_API_KEY");
        String baseUrl  = System.getProperty("baseUrl", "https://browser.device.pcloudy.com");
        String local    = System.getProperty("local", "false");
        String autoHeal = System.getProperty("autoHeal", "false");

        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("browserName", tier.browser.toLowerCase());

        // pCloudy-specific capabilities (matching sample-project selenium-java)
        Map<String, Object> pCloudyCaps = new HashMap<>();
        pCloudyCaps.put("userName", username);
        pCloudyCaps.put("accessKey", apiKey);
        pCloudyCaps.put("os", tier.os);
        pCloudyCaps.put("local", stringToBoolean(local));
        pCloudyCaps.put("autoHeal", stringToBoolean(autoHeal));
        pCloudyCaps.put("pCloudy_EnableVideo", true);
        pCloudyCaps.put("tag", "browser-tier:" + browserTierName);
        pCloudyCaps.put("name", tier.browser + " " + tier.os);
        pCloudyCaps.put("build", "pattern-03-selenium-browser-tier");
        pCloudyCaps.put("project", "parallel-execution-patterns");

        capabilities.setCapability("pcloudy:options", pCloudyCaps);

        ClientConfig clientConfig = ClientConfig.defaultConfig()
            .readTimeout(Duration.ofMinutes(15))
            .connectionTimeout(Duration.ofMinutes(15));

        driver.set((RemoteWebDriver)
            RemoteWebDriver.builder()
                .address(new URL(baseUrl + "/seleniumcloud/wd/hub"))
                .oneOf(capabilities)
                .config(clientConfig)
                .build());

        System.out.printf("[%s] Driver started — %s %s%n",
            tier.name(), tier.browser, tier.os);
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        if (driver.get() != null) {
            driver.get().quit();
            driver.remove();
        }
    }

    protected WebDriver getDriver() {
        return driver.get();
    }

    private boolean stringToBoolean(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Boolean value cannot be null");
        }
        String lower = value.trim().toLowerCase();
        if (!"true".equals(lower) && !"false".equals(lower)) {
            throw new IllegalArgumentException("Invalid boolean value: " + value);
        }
        return Boolean.parseBoolean(lower);
    }
}
