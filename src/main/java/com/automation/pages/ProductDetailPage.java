package com.automation.pages;

import io.appium.java_client.AppiumBy;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

public class ProductDetailPage extends BasePage {

    // The same id ("cartBt") is used for the "Add to cart" button here and for "Proceed To Checkout" in the cart
    private static final By ADD_TO_CART = byId("cartBt");

    @Step("Check the product detail screen is displayed")
    public boolean isDisplayed() {
        return isPresentWithin(ADD_TO_CART, 15);
    }

    @Step("Add the product to the cart")
    public ProductDetailPage addToCart() {
        if (!isDisplayedWithin(ADD_TO_CART, 5)) {
            scrollToId("cartBt");
        }
        click(ADD_TO_CART);
        return this;
    }

    @Step("Open the cart")
    public CartPage goToCart() {
        tapCart();
        return new CartPage();
    }

    @Step("Go back to the catalog")
    public CatalogPage backToCatalog() {
        pressBack();
        return new CatalogPage();
    }
}
