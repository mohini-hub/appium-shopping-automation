package com.automation.pages;

import com.automation.models.Address;
import io.qameta.allure.Step;

/** Checkout step 1: shipping address. */
public class ShippingAddressPage extends BasePage {

    @Step("Check the shipping address screen is displayed")
    public boolean isDisplayed() {
        return isDisplayedWithin(byId("address1ET"), 15);
    }

    @Step("Fill in the shipping address")
    public ShippingAddressPage fillAddress(Address address) {
        typeById("fullNameET", address.fullName());
        typeById("address1ET", address.addressLine1());
        typeById("address2ET", address.addressLine2());
        typeById("cityET", address.city());
        typeById("stateET", address.state());
        typeById("zipET", address.zipCode());
        typeById("countryET", address.country());
        hideKeyboardIfShown();
        return this;
    }

    @Step("Continue to payment")
    public PaymentPage continueToPayment() {
        clickById("paymentBtn");
        return new PaymentPage();
    }

    /** The app pre-fills the form with the logged-in user's data, so the required fields are cleared first. */
    @Step("Submit the address form with the required fields cleared")
    public ShippingAddressPage submitEmptyForm() {
        typeById("fullNameET", "");
        typeById("address1ET", "");
        typeById("cityET", "");
        typeById("zipET", "");
        typeById("countryET", "");
        hideKeyboardIfShown();
        clickById("paymentBtn");
        return this;
    }

    @Step("Check validation errors are displayed")
    public boolean hasValidationErrors() {
        return isDisplayedWithin(ERROR_MESSAGES, 8);
    }
}
