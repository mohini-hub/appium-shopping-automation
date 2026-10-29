package com.automation.pages;

import com.automation.driver.DriverManager;
import com.automation.models.SortOption;
import com.automation.utils.PriceUtils;
import io.appium.java_client.AppiumBy;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/** Home screen with the product list. */
public class CatalogPage extends BasePage {

    private static final By MENU_BUTTON = AppiumBy.accessibilityId("View menu");
    private static final By SORT_BUTTON = byId("sortIV");
    private static final By PRODUCT_TITLES = byId("titleTV");
    private static final By PRODUCT_PRICES = byId("priceTV");

    @Step("Check the product catalog is loaded")
    public boolean isLoaded() {
        return isDisplayedWithin(PRODUCT_TITLES, 30);
    }

    @Step("Open the navigation menu")
    public MenuPage openMenu() {
        click(MENU_BUTTON);
        return new MenuPage();
    }

    @Step("Open product '{productName}'")
    public ProductDetailPage openProduct(String productName) {
        // Exact text match: the catalog also contains "Sauce Labs Backpack (green)" and similar names
        By title = AppiumBy.xpath("//android.widget.TextView[@resource-id='" + APP_ID_PREFIX
                + "titleTV' and @text='" + productName + "']");
        By image = AppiumBy.xpath("(//android.widget.TextView[@resource-id='" + APP_ID_PREFIX
                + "titleTV' and @text='" + productName + "']/..//android.widget.ImageView[@resource-id='"
                + APP_ID_PREFIX + "productIV'])[1]");
        if (!isDisplayedWithin(title, 3)) {
            scrollToText(productName);
        }
        ProductDetailPage details = new ProductDetailPage();
        // Tapping the product picture opens the details; if nothing opens, tap the title instead
        for (By target : new By[] {image, title}) {
            if (isDisplayedWithin(target, 2)) {
                click(target);
                if (details.isDisplayed()) {
                    return details;
                }
            }
        }
        return details;
    }

    @Step("Sort products by '{option}'")
    public CatalogPage sortBy(SortOption option) {
        click(SORT_BUTTON);
        click(AppiumBy.xpath("//*[@text='" + option.label() + "']"));
        waitForVisible(PRODUCT_TITLES);
        // My Demo App 2.3.0 crashes when a product is tapped after the list was sorted, even after a restart
        // (ProductCatalogFragment, ArrayIndexOutOfBounds / NullPointerException), so the app data is cleared before the next test.
        DriverManager.markAppResetRequired();
        return this;
    }

    @Step("Read the names of the visible products")
    public List<String> getVisibleProductNames() {
        return getTexts(PRODUCT_TITLES);
    }

    @Step("Read the prices of the visible products")
    public List<BigDecimal> getVisibleProductPrices() {
        return getTexts(PRODUCT_PRICES).stream()
                .map(PriceUtils::parse)
                .collect(Collectors.toList());
    }

    @Step("Open the cart")
    public CartPage goToCart() {
        tapCart();
        return new CartPage();
    }
}
