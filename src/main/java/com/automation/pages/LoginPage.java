package com.automation.pages;

import com.automation.models.User;
import io.appium.java_client.AppiumBy;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;

import java.util.stream.Collectors;

public class LoginPage extends BasePage {

    private static final By USERNAME = byId("nameET");
    private static final By PASSWORD = byId("passwordET");
    private static final By LOGIN_BUTTON = byId("loginBtn");
    // Validation messages (ids containing "error") and the credential / locked-out messages shown by the app.
    // Any element type is accepted, so the message is found whether it is a label, a dialog text or a toast.
    private static final By LOGIN_ERRORS = AppiumBy.xpath(
            "//*[contains(translate(@resource-id,'ERO','ero'),'error')"
                    + " or contains(@text,'do not match') or contains(@text,'not match')"
                    + " or contains(@text,'locked') or contains(@text,'Invalid')]");

    @Step("Check the login screen is displayed")
    public boolean isDisplayed() {
        return isDisplayedWithin(LOGIN_BUTTON, 15);
    }

    @Step("Log in with username '{username}'")
    public void login(String username, String password) {
        type(USERNAME, username);
        type(PASSWORD, password);
        hideKeyboardIfShown();
        click(LOGIN_BUTTON);
    }

    @Step("Log in as a test user")
    public void login(User user) {
        login(user.username(), user.password());
    }

    /** Waits until at least one error label has text, then returns all error texts joined with a space. */
    @Step("Read the login error message")
    public String getErrorMessage() {
        try {
            return wait.until(new ExpectedCondition<String>() {
                @Override
                public String apply(WebDriver webDriver) {
                    String joined = webDriver.findElements(LOGIN_ERRORS).stream()
                            .map(WebElement::getText)
                            .filter(text -> text != null && !text.isBlank())
                            .collect(Collectors.joining(" "));
                    return joined.isEmpty() ? null : joined;
                }
            });
        } catch (TimeoutException e) {
            logVisibleTexts("No login error message found");
            throw e;
        }
    }
}
