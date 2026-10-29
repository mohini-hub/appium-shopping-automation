package com.automation.tests;

import com.automation.base.BaseTest;
import com.automation.base.TestGroups;
import com.automation.data.LoginDataProvider;
import com.automation.data.TestData;
import com.automation.models.LoginScenario;
import com.automation.pages.CatalogPage;
import com.automation.pages.LoginPage;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.testng.Assert;
import org.testng.annotations.Test;

@Feature("Login")
public class LoginTest extends BaseTest {

    @Test(groups = TestGroups.SMOKE, description = "The login screen can be opened from the menu")
    @Story("Open login screen")
    @Severity(SeverityLevel.CRITICAL)
    public void loginScreenIsReachableFromMenu() {
        LoginPage login = new CatalogPage().openMenu().goToLogin();

        Assert.assertTrue(login.isDisplayed(), "The login screen should be displayed");
    }

    @Test(groups = TestGroups.SMOKE, description = "A valid user can log in")
    @Story("Successful login")
    @Severity(SeverityLevel.BLOCKER)
    public void validUserCanLogIn() {
        CatalogPage catalog = loginAsStandardUser();

        Assert.assertTrue(catalog.openMenu().isLogoutVisible(),
                "The menu should offer 'Log Out' after logging in");
    }

//    @Test(groups = TestGroups.REGRESSION,
//            dataProvider = "invalidLoginScenarios", dataProviderClass = LoginDataProvider.class,
//            description = "Invalid credentials are rejected with a clear error message")
//    @Story("Failed login")
//    @Severity(SeverityLevel.CRITICAL)
//    public void invalidCredentialsShowAnError(LoginScenario scenario) {
//        LoginPage login = new CatalogPage().openMenu().goToLogin();
//        login.login(scenario.username(), scenario.password());
//
//        String actual = login.getErrorMessage();
//        Assert.assertTrue(actual.toLowerCase().contains(scenario.expectedError().toLowerCase()),
//                "Scenario '" + scenario.name() + "': expected the error to contain '"
//                        + scenario.expectedError() + "' but was '" + actual + "'");
//    }

    @Test(groups = TestGroups.REGRESSION, description = "A locked-out user is told the account is locked")
    @Story("Failed login")
    @Severity(SeverityLevel.NORMAL)
    public void lockedUserCannotLogIn() {
        LoginPage login = new CatalogPage().openMenu().goToLogin();
        login.login(TestData.lockedUser());

        Assert.assertTrue(login.getErrorMessage().toLowerCase().contains("locked out"),
                "A locked-out user should see the 'locked out' message");
    }

    @Test(groups = TestGroups.REGRESSION, description = "A logged-in user can log out")
    @Story("Logout")
    @Severity(SeverityLevel.CRITICAL)
    public void loggedInUserCanLogOut() {
        CatalogPage catalog = loginAsStandardUser();

        catalog.openMenu().logout();

        Assert.assertFalse(catalog.openMenu().isLogoutVisible(),
                "'Log Out' should not be offered after logging out");
    }
}
