package com.automation.pages;

import com.automation.models.PaymentDetails;
import io.qameta.allure.Step;

/** Checkout step 2: payment method. */
public class PaymentPage extends BasePage {

    @Step("Check the payment screen is displayed")
    public boolean isDisplayed() {
        return isDisplayedWithin(byId("cardNumberET"), 15);
    }

    @Step("Fill in the payment details")
    public PaymentPage fillPaymentDetails(PaymentDetails payment) {
        typeById("nameET", payment.cardHolder());
        typeById("cardNumberET", payment.cardNumber());
        typeById("expirationDateET", payment.expiry());
        typeById("securityCodeET", payment.cvv());
        hideKeyboardIfShown();
        return this;
    }

    @Step("Continue to the order review")
    public ReviewOrderPage reviewOrder() {
        clickById("paymentBtn");
        return new ReviewOrderPage();
    }
}
