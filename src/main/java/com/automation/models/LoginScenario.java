package com.automation.models;

/** One data-driven negative login case (testdata/login-scenarios.json). */
public record LoginScenario(
        String name,
        String username,
        String password,
        String expectedError) {

    @Override
    public String toString() {
        return name;
    }
}
