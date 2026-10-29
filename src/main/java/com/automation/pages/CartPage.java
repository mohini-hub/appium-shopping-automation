package com.automation.pages;

import io.appium.java_client.AppiumBy;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

public class CartPage extends BasePage {

    private static final By CHECKOUT_BUTTON = byId("cartBt");
    private static final By REMOVE_ITEM = byId("removeBt");
    private static final By EMPTY_CART =
            AppiumBy.xpath("//*[@text='No Items' or @text='Go Shopping']");

    @Step("Check '{productName}' is in the cart")
    public boolean isProductInCart(String productName) {
        return isDisplayedWithin(
                AppiumBy.xpath("//android.widget.TextView[@resource-id='" + APP_ID_PREFIX
                        + "titleTV' and @text='" + productName + "']"), 15);
    }

    @Step("Remove the first item from the cart")
    public CartPage removeFirstItem() {
        click(REMOVE_ITEM);
        return this;
    }

    @Step("Remove every item from the cart")
    public CartPage removeAllItems() {
        int guard = 0;
        while (guard++ < 10 && isDisplayedWithin(REMOVE_ITEM, 2)) {
            click(REMOVE_ITEM);
        }
        return this;
    }

    @Step("Check the cart is empty")
    public boolean isEmpty() {
        return isDisplayedWithin(EMPTY_CART, 15);
    }

    /** Use when the user is already logged in: the next screen is the shipping address form. */
    @Step("Proceed to checkout")
    public ShippingAddressPage proceedToCheckout() {
        click(CHECKOUT_BUTTON);
        return new ShippingAddressPage();
    }

    /** Use when nobody is logged in: the app asks the user to log in first. */
    @Step("Proceed to checkout without being logged in")
    public LoginPage proceedToCheckoutAsGuest() {
        click(CHECKOUT_BUTTON);
        return new LoginPage();
    }
}
