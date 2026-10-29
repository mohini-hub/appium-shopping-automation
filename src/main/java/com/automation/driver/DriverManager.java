package com.automation.driver;

import com.automation.config.ConfigReader;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Creates and quits the AndroidDriver.
 * The driver is stored in a ThreadLocal so every test thread gets its own session,
 * which keeps the framework ready for parallel execution.
 */
public final class DriverManager {

    private static final Logger LOG = LogManager.getLogger(DriverManager.class);
    private static final ThreadLocal<AndroidDriver> DRIVER = new ThreadLocal<>();

    // Set by tests that leave the app in a state that makes it crash later (see CatalogPage.sortBy)
    private static volatile boolean appResetRequired;

    private DriverManager() {
    }

    public static void markAppResetRequired() {
        appResetRequired = true;
    }

    /** Returns true once after markAppResetRequired() was called. */
    public static boolean consumeAppResetRequired() {
        boolean required = appResetRequired;
        appResetRequired = false;
        return required;
    }

    /** Starts the session once. When a session already exists it is reused (the whole suite shares one session). */
    public static void initDriver() {
        if (DRIVER.get() != null) {
            return;
        }
        String runOn = ConfigReader.get("runOn").toLowerCase();
        URL serverUrl;
        UiAutomator2Options options;
        switch (runOn) {
            case "sauce" -> {
                serverUrl = sauceUrl();
                options = sauceOptions();
            }
            default -> {
                serverUrl = localUrl();
                options = localOptions();
            }
        }

        LOG.info("Starting '{}' session (server: {})", runOn, serverUrl.getHost());
        AndroidDriver driver = new AndroidDriver(serverUrl, options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(ConfigReader.getInt("implicitWait")));
        DRIVER.set(driver);
    }

    private static URL localUrl() {
        String url = ConfigReader.get("appiumServerUrl");
        try {
            return URI.create(url).toURL();
        } catch (MalformedURLException e) {
            throw new IllegalStateException("Invalid appiumServerUrl: " + url, e);
        }
    }

    private static UiAutomator2Options localOptions() {
        File apk = new File(ConfigReader.get("appPath"));
        if (!apk.exists()) {
            throw new IllegalStateException("APK not found: " + apk.getAbsolutePath()
                    + ". Place the APK in the apps/ folder and update 'appPath' in config.properties.");
        }

        UiAutomator2Options options = new UiAutomator2Options();
        options.setPlatformName(ConfigReader.get("platformName"));
        options.setAutomationName(ConfigReader.get("automationName"));
        options.setDeviceName(ConfigReader.get("deviceName"));
        options.setApp(apk.getAbsolutePath());
        options.setNoReset(ConfigReader.getBoolean("noReset"));
        options.setFullReset(ConfigReader.getBoolean("fullReset"));
        options.setAutoGrantPermissions(true);
        // The app shows a splash screen and moves on quickly, so accept whichever activity comes first
        options.setAppWaitActivity("*");
        options.setNewCommandTimeout(Duration.ofSeconds(ConfigReader.getInt("newCommandTimeoutSeconds")));
        ConfigReader.getOptional("platformVersion").ifPresent(options::setPlatformVersion);
        ConfigReader.getOptional("udid").ifPresent(options::setUdid);

        // Slow emulators need more than Appium's default 20 seconds for adb commands (values in milliseconds)
        options.setCapability("appium:uiautomator2ServerInstallTimeout", ConfigReader.getInt("uiautomator2ServerInstallTimeout"));
        options.setCapability("appium:uiautomator2ServerLaunchTimeout", ConfigReader.getInt("uiautomator2ServerLaunchTimeout"));
        options.setCapability("appium:adbExecTimeout", ConfigReader.getInt("adbExecTimeout"));
        options.setCapability("appium:androidInstallTimeout", ConfigReader.getInt("androidInstallTimeout"));

        // Keep the emulator responsive: no window animations, and no logcat capture (the emulator logs a lot of noise)
        options.setCapability("appium:disableWindowAnimation", true);
        options.setCapability("appium:skipLogcatCapture", true);
        return options;
    }

    // ---- Sauce Labs (cloud device, video recorded automatically) ----

    /** Reads a credential from a -D system property / config value, or from an environment variable. */
    private static String credential(String propertyKey, String envName) {
        return ConfigReader.getOptional(propertyKey)
                .or(() -> Optional.ofNullable(System.getenv(envName)).filter(v -> !v.isBlank()))
                .orElseThrow(() -> new IllegalStateException("Missing Sauce Labs credential. Set the environment variable "
                        + envName + " (or pass -D" + propertyKey + "=...)."));
    }

    private static URL sauceUrl() {
        String region = ConfigReader.get("sauce.region");
        try {
            return URI.create("https://ondemand." + region + ".saucelabs.com/wd/hub").toURL();
        } catch (MalformedURLException e) {
            throw new IllegalStateException("Invalid sauce.region: " + region, e);
        }
    }

    private static UiAutomator2Options sauceOptions() {
        UiAutomator2Options options = new UiAutomator2Options();
        options.setPlatformName("android");
        options.setAutomationName("UiAutomator2");
        options.setDeviceName(ConfigReader.get("sauce.deviceName"));
        options.setPlatformVersion(ConfigReader.get("sauce.platformVersion"));
        options.setApp("storage:filename=" + ConfigReader.get("sauce.appFileName"));
        options.setAutoGrantPermissions(true);

        Map<String, Object> sauce = new HashMap<>();
        sauce.put("username", credential("sauce.username", "SAUCE_USERNAME"));
        sauce.put("accessKey", credential("sauce.accessKey", "SAUCE_ACCESS_KEY"));
        sauce.put("build", ConfigReader.get("sauce.buildName"));
        sauce.put("name", "Appium shopping automation");
        options.setCapability("sauce:options", sauce);
        return options;
    }

    public static AndroidDriver getDriver() {
        AndroidDriver driver = DRIVER.get();
        if (driver == null) {
            throw new IllegalStateException("Driver is not initialised. Did BaseTest.setUp() run?");
        }
        return driver;
    }

    public static boolean hasDriver() {
        return DRIVER.get() != null;
    }

    public static void quitDriver() {
        AndroidDriver driver = DRIVER.get();
        if (driver == null) {
            return;
        }
        try {
            driver.quit();
            LOG.info("Session closed");
        } finally {
            DRIVER.remove();
        }
    }
}
