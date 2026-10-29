package com.automation.pages;

import io.appium.java_client.AppiumBy;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

/** Confirmation screen shown after an order has been placed. */
public class CheckoutCompletePage extends BasePage {

    private static final By TITLE = AppiumBy.xpath("//android.widget.TextView[@text='Checkout Complete']");
    private static final By CONTINUE_SHOPPING = AppiumBy.xpath("//android.widget.Button[@text='Continue Shopping']");

    @Step("Check the order confirmation is displayed")
    public boolean isDisplayed() {
        return isDisplayedWithin(TITLE, 20);
    }

    @Step("Continue shopping")
    public CatalogPage continueShopping() {
        if (!isDisplayedWithin(CONTINUE_SHOPPING, 5)) {
            scrollToText("Continue Shopping");
        }
        click(CONTINUE_SHOPPING);
        return new CatalogPage();
    }
}
