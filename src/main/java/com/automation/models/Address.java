package com.automation.models;

/** Shipping address used in the checkout flow (testdata/address.json). */
public record Address(
        String fullName,
        String addressLine1,
        String addressLine2,
        String city,
        String state,
        String zipCode,
        String country) {
}
