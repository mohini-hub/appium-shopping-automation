package com.automation.pages;

import io.appium.java_client.AppiumBy;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

/** Checkout step 3: review the order and place it. */
public class ReviewOrderPage extends BasePage {

    // The app reuses the id "paymentBtn" for the main button of every checkout step, so the label is used here
    private static final By PLACE_ORDER = AppiumBy.xpath("//android.widget.Button[@text='Place Order']");

    @Step("Place the order")
    public CheckoutCompletePage placeOrder() {
        if (!isDisplayedWithin(PLACE_ORDER, 10)) {
            scrollToText("Place Order");
        }
        click(PLACE_ORDER);
        return new CheckoutCompletePage();
    }
}
