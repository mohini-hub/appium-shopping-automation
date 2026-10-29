package com.automation.models;

/** Payment card used in the checkout flow (testdata/payment.json). */
public record PaymentDetails(
        String cardHolder,
        String cardNumber,
        String expiry,
        String cvv) {
}
