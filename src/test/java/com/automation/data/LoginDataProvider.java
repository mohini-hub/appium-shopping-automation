package com.automation.data;

import com.automation.models.LoginScenario;
import org.testng.annotations.DataProvider;

import java.util.List;

public final class LoginDataProvider {

    private LoginDataProvider() {
    }

    @DataProvider(name = "invalidLoginScenarios")
    public static Object[][] invalidLoginScenarios() {
        List<LoginScenario> scenarios = TestData.loginScenarios();
        Object[][] data = new Object[scenarios.size()][1];
        for (int i = 0; i < scenarios.size(); i++) {
            data[i][0] = scenarios.get(i);
        }
        return data;
    }
}
