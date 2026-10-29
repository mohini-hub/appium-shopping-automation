package com.automation.tests;

import com.automation.base.BaseTest;
import com.automation.base.TestGroups;
import com.automation.data.TestData;
import com.automation.pages.CartPage;
import com.automation.pages.CatalogPage;
import com.automation.pages.ProductDetailPage;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.testng.Assert;
import org.testng.annotations.Test;

@Feature("Shopping cart")
public class CartTest extends BaseTest {

    @Test(groups = TestGroups.REGRESSION, description = "The cart badge is empty before anything is added")
    @Story("Cart badge")
    @Severity(SeverityLevel.MINOR)
    public void cartIsEmptyOnStartup() {
        CatalogPage catalog = new CatalogPage();
        Assert.assertTrue(catalog.isLoaded(), "The catalog should load");

        Assert.assertEquals(catalog.getCartCount(), 0, "A new session should start with an empty cart");
    }

    @Test(groups = TestGroups.SMOKE, description = "A product added to the cart appears in the cart")
    @Story("Add to cart")
    @Severity(SeverityLevel.BLOCKER)
    public void addedProductAppearsInCart() {
        CatalogPage catalog = new CatalogPage();
        Assert.assertTrue(catalog.isLoaded(), "The catalog should load");

        ProductDetailPage details = catalog.openProduct(TestData.BACKPACK).addToCart();
        Assert.assertEquals(details.getCartCount(), 1, "The cart badge should show 1 item");

        CartPage cart = details.goToCart();
        Assert.assertTrue(cart.isProductInCart(TestData.BACKPACK),
                TestData.BACKPACK + " should be listed in the cart");
    }

//    @Test(groups = TestGroups.REGRESSION, description = "Two different products can be added to the cart")
//    @Story("Add to cart")
//    @Severity(SeverityLevel.CRITICAL)
//    public void twoProductsCanBeAddedToCart() {
//        CatalogPage catalog = new CatalogPage();
//        Assert.assertTrue(catalog.isLoaded(), "The catalog should load");
//
//        catalog.openProduct(TestData.BACKPACK).addToCart().backToCatalog();
//        ProductDetailPage second = catalog.openProduct(TestData.BIKE_LIGHT).addToCart();
//        Assert.assertEquals(second.getCartCount(), 2, "The cart badge should show 2 items");
//
//        CartPage cart = second.goToCart();
//        Assert.assertTrue(cart.isProductInCart(TestData.BACKPACK), TestData.BACKPACK + " should be in the cart");
//        Assert.assertTrue(cart.isProductInCart(TestData.BIKE_LIGHT), TestData.BIKE_LIGHT + " should be in the cart");
//    }

    @Test(groups = TestGroups.REGRESSION, description = "A product can be removed from the cart")
    @Story("Remove from cart")
    @Severity(SeverityLevel.CRITICAL)
    public void productCanBeRemovedFromCart() {
        CatalogPage catalog = new CatalogPage();
        Assert.assertTrue(catalog.isLoaded(), "The catalog should load");

        CartPage cart = catalog.openProduct(TestData.BACKPACK).addToCart().goToCart();
        Assert.assertTrue(cart.isProductInCart(TestData.BACKPACK), "The product should be in the cart first");

        cart.removeFirstItem();

        Assert.assertTrue(cart.isEmpty(), "The cart should be empty after removing the only item");
    }
}
