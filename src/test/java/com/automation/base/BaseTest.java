package com.automation.base;

import com.automation.data.TestData;
import com.automation.driver.AppiumServerManager;
import com.automation.driver.DriverManager;
import com.automation.pages.CartPage;
import com.automation.pages.CatalogPage;
import com.automation.pages.MenuPage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * Parent of all test classes.
 * The whole suite shares ONE Appium session, so the app is installed and started only once.
 * Before every test the app is brought back to a clean state through the UI:
 * catalog screen, logged out, empty cart. Screenshots on failure are handled by TestListener.
 */
public abstract class BaseTest {

    private static final String APP_PACKAGE = "com.saucelabs.mydemoapp.android";

    protected final Logger log = LogManager.getLogger(getClass());

    @BeforeSuite(alwaysRun = true)
    public void startAppiumServer() {
        AppiumServerManager.startIfConfigured();
    }

    @AfterSuite(alwaysRun = true)
    public void stopSession() {
        DriverManager.quitDriver();
        AppiumServerManager.stop();
    }

    @BeforeMethod(alwaysRun = true)
    public void setUp(Method method) {
        log.info("Preparing test: {}", method.getName());
        DriverManager.initDriver();
        returnToCleanState();
    }

    /** Catalog screen, logged out, empty cart. If the UI route fails, the app is restarted once as a fallback. */
    private void returnToCleanState() {
        if (DriverManager.consumeAppResetRequired()) {
            log.info("A previous test sorted the catalog, which makes the app crash later. Clearing the app data.");
            DriverManager.getDriver().executeScript("mobile: clearApp", Map.of("appId", APP_PACKAGE));
            DriverManager.getDriver().activateApp(APP_PACKAGE);
        }
        try {
            cleanThroughUi();
        } catch (RuntimeException firstFailure) {
            log.warn("Could not reset the app through the UI ({}). Restarting the app once.", firstFailure.getMessage());
            try {
                DriverManager.getDriver().terminateApp(APP_PACKAGE);
                DriverManager.getDriver().activateApp(APP_PACKAGE);
                cleanThroughUi();
            } catch (RuntimeException secondFailure) {
                // The session itself may have died: start a new one
                log.warn("The session is not usable ({}). Starting a new session.", secondFailure.getMessage());
                DriverManager.quitDriver();
                DriverManager.initDriver();
            }
        }
    }

    private void cleanThroughUi() {
        CatalogPage nav = new CatalogPage(); // the header (menu and cart icons) is the same on every screen
        MenuPage menu = nav.openMenu();
        if (menu.isLogoutVisibleQuickly()) {
            menu.logout();
            menu = nav.openMenu();
        }
        menu.goToCatalog();
        Assert.assertTrue(nav.isLoaded(), "The catalog should be displayed before the test starts");

        if (nav.getCartCount() > 0) {
            CartPage cart = nav.goToCart();
            cart.removeAllItems();
            nav.openMenu().goToCatalog();
        }
    }

    /** Logs in through the menu with the standard test user and returns the catalog. */
    protected CatalogPage loginAsStandardUser() {
        CatalogPage catalog = new CatalogPage();
        catalog.openMenu().goToLogin().login(TestData.standardUser());
        Assert.assertTrue(catalog.isLoaded(), "The catalog should be displayed after a successful login");
        return catalog;
    }
}
