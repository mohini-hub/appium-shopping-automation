# Appium Shopping App Automation

![Java](https://img.shields.io/badge/Java-17-orange)
![Appium](https://img.shields.io/badge/Appium-2.x-purple)
![TestNG](https://img.shields.io/badge/TestNG-7.10-red)
![Allure](https://img.shields.io/badge/Report-Allure-yellow)
![License](https://img.shields.io/badge/License-MIT-blue)

Android UI test automation framework for the Sauce Labs **My Demo App**, built with
**Appium 2 (UiAutomator2)**, **Java 17**, **TestNG**, **Maven** and the **Page Object Model**.
It covers login, catalog, cart and checkout, produces **Allure** reports and runs locally, on a
cloud device (Sauce Labs) or in **GitHub Actions**.

## Highlights

- **Page Object Model** with a shared `BasePage` (explicit waits, scrolling, accessibility-id helpers).
  Page methods return the next page, so a test reads like the user journey.
- **One Appium session for the whole suite.** The app is installed and launched once. Before every test
  the app is brought back to a clean state through the UI (catalog screen, logged out, empty cart), so
  tests stay independent without paying for a new session each time.
- **Data-driven tests** with TestNG `@DataProvider`; test data lives in JSON files and is loaded into
  typed Java records.
- **Smoke and regression suites**, selected with TestNG groups.
- **Allure reporting** with features, stories, severities and `@Step` annotations.
- **Failure evidence:** a screenshot and the page source are saved and attached to the report.
- **Configurable retry** of failed tests (`RetryAnalyzer` installed by an `IAnnotationTransformer`).
- **Log4j2** logging to the console and `target/logs/automation.log`.
- **Run targets:** local emulator or USB device, or a Sauce Labs cloud device, chosen with one setting.
- **Demo mode:** an optional pause after every action, so a screen recording is easy to follow.
- **ThreadLocal driver** and configuration with command-line overrides.
- **GitHub Actions** workflow that runs the smoke suite on an Android emulator.

## Test coverage

| Feature | Scenarios |
|---|---|
| Login | open the login screen, valid login, locked-out user, logout, 3 data-driven invalid logins (locked user, empty username, empty password) |
| Catalog | products are displayed, open product details, sort by name (A-Z, Z-A), sort by price (low-high, high-low) |
| Cart | empty on startup, add one product, remove a product |
| Checkout | full purchase flow, continue shopping after an order, guest is asked to log in, empty shipping form validation |

Tests tagged `smoke` cover the critical journeys; every test belongs to either `smoke` or `regression`.

## Tech stack

| Area | Tool |
|---|---|
| Language / build | Java 17, Maven |
| Mobile automation | Appium 2, UiAutomator2 driver, Appium Java client 9.3.0, Selenium 4.23 |
| Test runner | TestNG 7.10 |
| Reporting | Allure 2.29 |
| Test data | JSON + Jackson |
| Logging | Log4j2 |
| CI | GitHub Actions |

## Project structure

```
appium-shopping-automation/
├── pom.xml
├── testng.xml                         # full regression suite
├── testng-smoke.xml                   # smoke suite (group "smoke")
├── apps/                              # put the APK here (not committed)
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
        │   ├── base/                  # BaseTest (session + clean state), TestGroups
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
- An Android emulator (or a real device with USB debugging) and `adb` on your machine
- Appium 2 with the UiAutomator2 driver:

```bash
npm install -g appium
appium driver install uiautomator2
appium driver doctor uiautomator2     # verifies the Android setup
```

## Setup

1. Download the My Demo App APK (`mda-2.3.0-27.apk`) from
   https://github.com/saucelabs/my-demo-app-android/releases and place it in `apps/`.
2. Start an emulator or connect a device, then check it with `adb devices`.
3. In `src/main/resources/config.properties` set `deviceName` (and `udid` if several devices are
   connected) and `appPath`.

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

# Slow down every action for a screen recording
mvn clean test -DdemoDelayMs=1500
```

### Running on Sauce Labs (cloud device)

1. Upload the APK in the Sauce Labs *App Management* page.
2. Set the credentials as environment variables (never commit them):
   `SAUCE_USERNAME` and `SAUCE_ACCESS_KEY`.
3. Run with the cloud target. `sauce.region` must match the data center of your account.

```bash
mvn clean test -DrunOn=sauce -Dsauce.region=eu-central-1
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
| `runOn` | `local` (emulator / USB device) or `sauce` (Sauce Labs) | `local` |
| `deviceName`, `udid` | Emulator or device selection | `emulator-5554`, empty |
| `appiumServerUrl` | Appium 2 server URL | `http://127.0.0.1:4723` |
| `appium.autoStart` | Start a local Appium server automatically | `false` |
| `appPath` | APK under test | `apps/mda-2.3.0-27.apk` |
| `noReset`, `fullReset` | App state handling at session start | `false` |
| `explicitWait`, `implicitWait` | Waits in seconds | `40`, `0` |
| `retryCount` | Retries for a failed test | `1` |
| `demoDelayMs` | Pause after every tap/typing, for recordings | `0` |
| `*Timeout` keys | Appium timeouts for slow computers (milliseconds) | `120000` |

## Design decisions

- **Page Objects return the next page**, so tests read like the user journey:
  `catalog.openProduct(...).addToCart().goToCart().proceedToCheckout()`.
- **Tests contain assertions only**; locators and screen logic live in the page classes.
- **Explicit waits only** (implicit wait is 0) to avoid unpredictable timing.
- **One session, clean state per test.** Starting a session takes minutes on a slow machine, so the
  suite reuses it. `BaseTest` resets the app through the UI before every test and restarts the app
  as a last resort if that fails.
- **Test data lives in JSON**, not in test code.
- **Credentials are read from environment variables**, never from the repository.

## Locators

Locators target the Sauce Labs My Demo App **v2.3.0** and can differ between app versions. Most elements
are located by their `resource-id` or accessibility id. If a test fails with "no such element",
dump the screen (Appium Inspector, or `adb shell uiautomator dump`), find the element and update the
locator constant in the matching class under `src/main/java/com/automation/pages`. On failure the
page source is attached to the Allure report, which makes locator problems easy to diagnose.

## Continuous integration

`.github/workflows/android-tests.yml` runs the smoke suite on an Android emulator and uploads the
Allure results, screenshots and logs as build artifacts. It is started manually (Actions > Android UI Tests >
Run workflow), because the APK is not stored in the repository. Before running it, define a repository variable
named `APK_URL` that points to the APK of the app under test.

The main way to run the suite is locally on an emulator, see the setup section above.

## Roadmap

- [ ] Parallel execution on several devices
- [ ] Publishing the Allure report to GitHub Pages
- [ ] iOS support with the XCUITest driver
- [ ] Biometrics, WebView and QR scanner screens

## License

Released under the [MIT License](LICENSE).
