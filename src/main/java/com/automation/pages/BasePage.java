package com.automation.pages;

import com.automation.config.ConfigReader;
import com.automation.driver.DriverManager;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Parent of all page objects. Holds the driver and the common actions
 * (wait, click, type, scroll) so that page classes only describe their own screen.
 */
public abstract class BasePage {

    /** Validation / error messages used across the app (resource ids contain "Error"). */
    protected static final By ERROR_MESSAGES =
            AppiumBy.xpath("//android.widget.TextView[contains(translate(@resource-id,'ERO','ero'),'error')]");

    /** Every element id in the My Demo App starts with this package prefix. */
    protected static final String APP_ID_PREFIX = "com.saucelabs.mydemoapp.android:id/";

    // The cart icon is part of the header on most screens
    private static final By CART_ICON = AppiumBy.accessibilityId("View cart");
    private static final By CART_COUNT = AppiumBy.id(APP_ID_PREFIX + "cartTV");

    protected final Logger log = LogManager.getLogger(getClass());
    protected final AndroidDriver driver;
    protected final WebDriverWait wait;

    protected BasePage() {
        this.driver = DriverManager.getDriver();
        this.wait = newWait(ConfigReader.getInt("explicitWait"));
    }

    /**
     * Creates a wait that keeps polling when a single call fails with a temporary driver error,
     * for example "Timed out waiting for the root AccessibilityNodeInfo" on a busy emulator.
     */
    private WebDriverWait newWait(int seconds) {
        WebDriverWait newWait = new WebDriverWait(driver, Duration.ofSeconds(seconds));
        newWait.ignoring(WebDriverException.class);
        return newWait;
    }

    // ------------------------------------------------------------------
    // Waiting
    // ------------------------------------------------------------------

    /** True when the element exists in the screen hierarchy, even if part of it is scrolled off screen. */
    protected boolean isPresentWithin(By locator, int seconds) {
        try {
            newWait(seconds).until(ExpectedConditions.presenceOfElementLocated(locator));
            return true;
        } catch (TimeoutException | NoSuchElementException e) {
            return false;
        }
    }

    protected WebElement waitForVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    /** Waits up to the given number of seconds. Returns false instead of throwing when nothing shows up. */
    protected boolean isDisplayedWithin(By locator, int seconds) {
        try {
            newWait(seconds).until(ExpectedConditions.visibilityOfElementLocated(locator));
            return true;
        } catch (TimeoutException | NoSuchElementException e) {
            return false;
        }
    }

    // ------------------------------------------------------------------
    // Element actions
    // ------------------------------------------------------------------

    protected void click(By locator) {
        wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
        demoPause();
    }

    protected void type(By locator, String text) {
        WebElement element = waitForVisible(locator);
        element.clear();
        if (hasText(text)) {
            element.sendKeys(text);
        }
        demoPause();
    }

    /**
     * Optional pause after every action so a screen recording can follow the test.
     * Set demoDelayMs=1500 in config.properties for a demo; keep 0 for normal runs.
     */
    private void demoPause() {
        int delay = ConfigReader.getOptional("demoDelayMs").map(Integer::parseInt).orElse(0);
        if (delay > 0) {
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    protected String getText(By locator) {
        return waitForVisible(locator).getText();
    }

    /** Text of every element matching the locator (waits for at least one to appear). */
    protected List<String> getTexts(By locator) {
        waitForVisible(locator);
        return driver.findElements(locator).stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
    }

    // ------------------------------------------------------------------
    // Accessibility-id shortcuts (scroll into view when needed)
    // ------------------------------------------------------------------

    protected void clickByDesc(String contentDesc) {
        By locator = AppiumBy.accessibilityId(contentDesc);
        if (!isDisplayedWithin(locator, 2)) {
            scrollToDescription(contentDesc);
        }
        click(locator);
    }

    protected void typeByDesc(String contentDesc, String text) {
        By locator = AppiumBy.accessibilityId(contentDesc);
        if (!isDisplayedWithin(locator, 2)) {
            scrollToDescription(contentDesc);
        }
        type(locator, text);
    }

    // ------------------------------------------------------------------
    // Scrolling / keyboard / navigation
    // ------------------------------------------------------------------

    /** Scrolls the first scrollable view until the element with this short id is on screen. */
    protected void scrollToId(String shortId) {
        driver.findElement(AppiumBy.androidUIAutomator(
                "new UiScrollable(new UiSelector().scrollable(true).instance(0))"
                        + ".scrollIntoView(new UiSelector().resourceId(\"" + APP_ID_PREFIX + shortId + "\"))"));
    }

    /** Makes sure the element is visible (scrolling when needed), then taps it. */
    protected void clickById(String shortId) {
        By locator = byId(shortId);
        if (!isDisplayedWithin(locator, 2)) {
            scrollToId(shortId);
        }
        click(locator);
    }

    /** Makes sure the field is visible (scrolling when needed), then replaces its content. */
    protected void typeById(String shortId, String text) {
        By locator = byId(shortId);
        if (!isDisplayedWithin(locator, 2)) {
            scrollToId(shortId);
        }
        type(locator, text);
    }

    protected void scrollToText(String text) {
        driver.findElement(AppiumBy.androidUIAutomator(
                "new UiScrollable(new UiSelector().scrollable(true).instance(0))"
                        + ".scrollIntoView(new UiSelector().text(\"" + text + "\"))"));
    }

    protected void scrollToDescription(String contentDesc) {
        driver.findElement(AppiumBy.androidUIAutomator(
                "new UiScrollable(new UiSelector().scrollable(true).instance(0))"
                        + ".scrollIntoView(new UiSelector().description(\"" + contentDesc + "\"))"));
    }

    protected void hideKeyboardIfShown() {
        try {
            if (driver.isKeyboardShown()) {
                driver.hideKeyboard();
            }
        } catch (WebDriverException ignored) {
            // The keyboard is already closed, nothing to do
        }
    }

    protected void pressBack() {
        driver.navigate().back();
    }

    protected static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    // ------------------------------------------------------------------
    // Cart header (shared by several screens)
    // ------------------------------------------------------------------

    protected void tapCart() {
        click(CART_ICON);
    }

    /** Logs every text currently on screen. Used when an expected element does not show up, to ease debugging. */
    protected void logVisibleTexts(String reason) {
        try {
            List<String> texts = driver.findElements(AppiumBy.xpath("//*[string-length(@text) > 0]")).stream()
                    .map(WebElement::getText)
                    .collect(Collectors.toList());
            log.warn("{} - texts on screen: {}", reason, texts);
        } catch (WebDriverException e) {
            log.warn("{} - could not read the screen: {}", reason, e.getMessage());
        }
    }

    /** Builds a locator for an element id of the app, for example byId("loginBtn"). */
    protected static By byId(String shortId) {
        return AppiumBy.id(APP_ID_PREFIX + shortId);
    }

    /** Number shown on the cart badge. Returns 0 when the badge is not displayed (empty cart). */
    public int getCartCount() {
        if (!isDisplayedWithin(CART_COUNT, 3)) {
            return 0;
        }
        try {
            String text = driver.findElement(CART_COUNT).getText().trim();
            return text.isEmpty() ? 0 : Integer.parseInt(text);
        } catch (NoSuchElementException | NumberFormatException e) {
            return 0;
        }
    }
}
