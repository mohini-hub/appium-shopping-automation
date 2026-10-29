package com.automation.tests;

import com.automation.base.BaseTest;
import com.automation.base.TestGroups;
import com.automation.data.TestData;
import com.automation.pages.CartPage;
import com.automation.pages.CatalogPage;
import com.automation.pages.CheckoutCompletePage;
import com.automation.pages.LoginPage;
import com.automation.pages.ShippingAddressPage;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.testng.Assert;
import org.testng.annotations.Test;

@Feature("Checkout")
public class CheckoutTest extends BaseTest {

    @Test(groups = TestGroups.SMOKE, description = "A logged-in user can complete a purchase end to end")
    @Story("Place an order")
    @Severity(SeverityLevel.BLOCKER)
    public void loggedInUserCanCompleteCheckout() {
        CheckoutCompletePage confirmation = placeOrderAsStandardUser();

        Assert.assertTrue(confirmation.isDisplayed(), "The 'Checkout Complete' screen should be displayed");
    }

    @Test(groups = TestGroups.REGRESSION, description = "After an order the user can go back to shopping with an empty cart")
    @Story("Place an order")
    @Severity(SeverityLevel.NORMAL)
    public void userCanContinueShoppingAfterOrder() {
        CatalogPage catalog = placeOrderAsStandardUser().continueShopping();

        Assert.assertTrue(catalog.isLoaded(), "The catalog should be displayed again");
        Assert.assertEquals(catalog.getCartCount(), 0, "The cart should be empty after an order");
    }

    @Test(groups = TestGroups.REGRESSION, description = "A guest is asked to log in before checking out")
    @Story("Checkout access")
    @Severity(SeverityLevel.CRITICAL)
    public void guestIsAskedToLogInBeforeCheckout() {
        CatalogPage catalog = new CatalogPage();
        Assert.assertTrue(catalog.isLoaded(), "The catalog should load");

        LoginPage login = catalog.openProduct(TestData.BACKPACK)
                .addToCart()
                .goToCart()
                .proceedToCheckoutAsGuest();

        Assert.assertTrue(login.isDisplayed(), "The login screen should appear when a guest starts checkout");
    }

    @Test(groups = TestGroups.REGRESSION, description = "The shipping form rejects an empty submission")
    @Story("Checkout validation")
    @Severity(SeverityLevel.NORMAL)
    public void emptyShippingFormShowsValidationErrors() {
        CatalogPage catalog = loginAsStandardUser();
        ShippingAddressPage shipping = catalog.openProduct(TestData.BACKPACK)
                .addToCart()
                .goToCart()
                .proceedToCheckout();
        Assert.assertTrue(shipping.isDisplayed(), "The shipping address screen should be displayed");

        shipping.submitEmptyForm();

        Assert.assertTrue(shipping.hasValidationErrors(), "Validation errors should be shown for the empty form");
    }

    /** Full purchase flow shared by several tests. */
    private CheckoutCompletePage placeOrderAsStandardUser() {
        CatalogPage catalog = loginAsStandardUser();

        CartPage cart = catalog.openProduct(TestData.BACKPACK).addToCart().goToCart();
        Assert.assertTrue(cart.isProductInCart(TestData.BACKPACK), "The product should be in the cart");

        return cart.proceedToCheckout()
                .fillAddress(TestData.address())
                .continueToPayment()
                .fillPaymentDetails(TestData.payment())
                .reviewOrder()
                .placeOrder();
    }
}
