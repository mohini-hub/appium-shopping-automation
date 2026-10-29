package com.automation.pages;

import io.appium.java_client.AppiumBy;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

/** Navigation drawer (hamburger menu). */
public class MenuPage extends BasePage {

    /** Confirmation buttons of the log-out dialogs: the standard positive button, or a button labelled LOG OUT / OK. */
    private static final By DIALOG_CONFIRM = AppiumBy.xpath(
            "//*[@resource-id='android:id/button1' or (contains(@class,'Button') and ("
                    + "translate(@text,'abcdefghijklmnopqrstuvwxyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ')='LOG OUT' or "
                    + "translate(@text,'abcdefghijklmnopqrstuvwxyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ')='OK'))]");

    /** Menu entries are TextViews with the id "itemTV" and the entry name as text. */
    private static By menuItem(String text) {
        return AppiumBy.xpath("//android.widget.TextView[@resource-id='" + APP_ID_PREFIX
                + "itemTV' and @text='" + text + "']");
    }

    @Step("Open the catalog from the menu")
    public CatalogPage goToCatalog() {
        click(menuItem("Catalog"));
        return new CatalogPage();
    }

    @Step("Open the login screen from the menu")
    public LoginPage goToLogin() {
        click(menuItem("Log In"));
        return new LoginPage();
    }

    @Step("Check whether the 'Log Out' menu item is visible")
    public boolean isLogoutVisible() {
        return isDisplayedWithin(menuItem("Log Out"), 5);
    }

    /** Short check used between tests, so a logged-out menu does not cost a long wait. */
    public boolean isLogoutVisibleQuickly() {
        return isDisplayedWithin(menuItem("Log Out"), 2);
    }

    @Step("Log out")
    public void logout() {
        click(menuItem("Log Out"));
        // Depending on the app version there are one or two dialogs ("Are you sure?" and "Logged out"), so each is optional
        for (int i = 0; i < 2; i++) {
            if (isDisplayedWithin(DIALOG_CONFIRM, 6)) {
                click(DIALOG_CONFIRM);
            }
        }
    }
}
