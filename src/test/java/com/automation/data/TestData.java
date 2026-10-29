package com.automation.data;

import com.automation.models.Address;
import com.automation.models.LoginScenario;
import com.automation.models.PaymentDetails;
import com.automation.models.User;
import com.automation.utils.JsonDataReader;

import java.util.List;

/** Single entry point for test data. Values come from src/test/resources/testdata/*.json. */
public final class TestData {

    // Product names as displayed in the catalog
    public static final String BACKPACK = "Sauce Labs Backpack";
    public static final String BIKE_LIGHT = "Sauce Labs Bike Light";

    private TestData() {
    }

    public static User standardUser() {
        return userWithRole("standard");
    }

    public static User lockedUser() {
        return userWithRole("locked");
    }

    public static Address address() {
        return JsonDataReader.read("testdata/address.json", Address.class);
    }

    public static PaymentDetails payment() {
        return JsonDataReader.read("testdata/payment.json", PaymentDetails.class);
    }

    public static List<LoginScenario> loginScenarios() {
        return JsonDataReader.readList("testdata/login-scenarios.json", LoginScenario.class);
    }

    private static User userWithRole(String role) {
        return JsonDataReader.readList("testdata/users.json", User.class).stream()
                .filter(user -> user.role().equals(role))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No test user with role: " + role));
    }
}
