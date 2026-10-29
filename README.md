# Appium Shopping App Automation

Android UI test automation framework for the Sauce Labs **My Demo App**, built with
**Appium 2 (UiAutomator2)**, **Java 17**, **TestNG**, **Maven** and the **Page Object Model**.
Reports are generated with **Allure**, and a GitHub Actions workflow is included.

## Highlights

- Page Object Model with a shared `BasePage` (explicit waits, scrolling, accessibility-id helpers)
- Typed test data models (Java records) loaded from JSON files
- Data-driven tests with TestNG `@DataProvider`
- Smoke and regression suites selected with TestNG groups
- Allure reporting with features, stories, severities and `@Step` annotations
- Screenshot and page-source attachments on failure
- Automatic retry of flaky tests (configurable)
- Log4j2 logging to console and `target/logs/automation.log`
- Optional automatic start and stop of the local Appium server
- ThreadLocal driver, ready for parallel execution
- Configuration with command-line overrides
- GitHub Actions workflow running the smoke suite on an Android emulator

## Test coverage

| Feature | Tests |
|---|---|
| Login | open login screen, valid login, 4 data-driven invalid logins (wrong password, locked user, empty username, empty password), locked user, logout |
| Catalog | products are displayed, open product details, sort by name (A-Z, Z-A), sort by price (low-high, high-low) |
| Cart | empty on startup, add one product, add two products, remove a product |
| Checkout | full purchase flow, continue shopping after an order, guest is asked to log in, empty shipping form validation |

Tests tagged `smoke` cover the critical journeys; every test belongs to either `smoke` or `regression`.

## Project structure

```
appium-shopping-automation/
├── pom.xml
├── testng.xml                         # full regression suite
├── testng-smoke.xml                   # smoke suite (group "smoke")
├── apps/                              # put the APK here
├── .github/workflows/android-tests.yml
└── src/
    ├── main/
    │   ├── java/com/automation/
    │   │   ├── config/                # ConfigReader
    │   │   ├── driver/                # DriverManager, AppiumServerManager
    │   │   ├── models/                # User, Address, PaymentDetails, LoginScenario, SortOption
    │   │   ├── pages/                 # BasePage and one class per screen
    │   │   └── utils/                 # ScreenshotUtil, JsonDataReader, PriceUtils
    │   └── resources/
    │       ├── config.properties
    │       └── log4j2.xml
    └── test/
        ├── java/com/automation/
        │   ├── base/                  # BaseTest, TestGroups
        │   ├── data/                  # TestData, LoginDataProvider
        │   ├── listeners/             # TestListener, RetryAnalyzer, AnnotationTransformer
        │   └── tests/                 # LoginTest, CatalogTest, CartTest, CheckoutTest
        └── resources/
            ├── allure.properties
            └── testdata/              # users, address, payment, login scenarios (JSON)
```

## Prerequisites

- JDK 17 or newer
- Maven 3.9 or newer
- Node.js 18 or newer
- Android Studio with an emulator (or a real device with USB debugging) and `ANDROID_HOME` set
- Appium 2 with the UiAutomator2 driver:

```bash
npm install -g appium
appium driver install uiautomator2
appium driver doctor uiautomator2     # verifies the Android setup
```

## Setup

1. Download the My Demo App APK from https://github.com/saucelabs/my-demo-app-android/releases
   and place it in `apps/`.
2. Update `appPath` and `deviceName` in `src/main/resources/config.properties`
   (`adb devices` lists the connected device).
3. Start the emulator.

## Running the tests

```bash
# Terminal 1: start the Appium server (skip this if appium.autoStart=true)
appium

# Terminal 2: full regression suite
mvn clean test

# Smoke suite only
mvn clean test -Dsuite.file=testng-smoke.xml

# Override configuration values
mvn clean test -DdeviceName=emulator-5556 -DappPath=apps/other.apk -DretryCount=0

# Let the framework start and stop Appium itself
mvn clean test -Dappium.autoStart=true
```

## Reports

| Report | Command / location |
|---|---|
| Allure report | `mvn allure:serve` (opens the report in the browser) |
| TestNG / Surefire | `target/surefire-reports/index.html` |
| Failure screenshots | `target/screenshots/` |
| Logs | `target/logs/automation.log` |

## Configuration

All keys live in `src/main/resources/config.properties` and can be overridden with `-D<key>=<value>`.

| Key | Purpose | Default |
|---|---|---|
| `deviceName` | Emulator or device name | `emulator-5554` |
| `platformVersion`, `udid` | Optional device selection | empty |
| `appiumServerUrl` | Appium 2 server URL | `http://127.0.0.1:4723` |
| `appium.autoStart` | Start a local Appium server automatically | `false` |
| `appPath` | APK under test | `apps/mda-2.2.0-25.apk` |
| `noReset`, `fullReset` | App state handling between sessions | `false` |
| `explicitWait`, `implicitWait` | Waits in seconds | `15`, `0` |
| `retryCount` | Retries for a failed test | `1` |

## Design decisions

- **Page Objects return the next page**, so tests read like the user journey:
  `catalog.openProduct(...).addToCart().goToCart().proceedToCheckout()`.
- **Tests contain assertions only**; locators and screen logic live in the page classes.
- **Explicit waits only** (implicit wait is 0) to avoid unpredictable timing.
- **Every test starts a fresh session**, so tests are independent and can run in any order.
- **Test data lives in JSON**, not in test code.

## Locators

Locators target the Sauce Labs My Demo App and can differ between app versions. If a test fails with
"no such element", open the app in **Appium Inspector**, find the element and update the locator
constant in the matching class under `src/main/java/com/automation/pages`. On failure the page source
is attached to the Allure report, which makes locator problems easy to diagnose.

## Continuous integration

`.github/workflows/android-tests.yml` runs the smoke suite on an Android emulator and uploads the
Allure results, screenshots and logs as build artifacts. Define a repository variable named `APK_URL`
that points to the APK of the app under test.

## Roadmap

- [ ] Parallel execution on several emulators
- [ ] iOS support with the XCUITest driver
- [ ] Publishing the Allure report to GitHub Pages
- [ ] Biometrics, WebView and QR scanner screens
