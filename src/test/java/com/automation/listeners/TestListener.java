package com.automation.listeners;

import com.automation.driver.DriverManager;
import com.automation.utils.ScreenshotUtil;
import io.qameta.allure.Allure;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.ByteArrayInputStream;
import java.nio.file.Path;

/**
 * Logs the lifecycle of every test. When a test fails it saves a screenshot and attaches the
 * screenshot and the current page source (useful for fixing locators) to the Allure report.
 */
public class TestListener implements ITestListener {

    private static final Logger LOG = LogManager.getLogger(TestListener.class);

    @Override
    public void onTestStart(ITestResult result) {
        LOG.info("STARTED  {}", describe(result));
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        LOG.info("PASSED   {}", describe(result));
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        LOG.warn("SKIPPED  {}", describe(result));
    }

    @Override
    public void onTestFailure(ITestResult result) {
        LOG.error("FAILED   {}", describe(result), result.getThrowable());
        if (!DriverManager.hasDriver()) {
            return;
        }
        try {
            byte[] screenshot = ScreenshotUtil.takeScreenshot(DriverManager.getDriver());
            Path saved = ScreenshotUtil.save(screenshot, result.getMethod().getMethodName());
            if (saved != null) {
                LOG.info("Screenshot saved: {}", saved);
            }
            Allure.addAttachment("Screenshot on failure", "image/png",
                    new ByteArrayInputStream(screenshot), "png");
            Allure.addAttachment("Page source on failure", "text/xml",
                    DriverManager.getDriver().getPageSource());
        } catch (RuntimeException e) {
            LOG.warn("Could not collect failure evidence: {}", e.getMessage());
        }
    }

    private static String describe(ITestResult result) {
        return result.getTestClass().getRealClass().getSimpleName() + "." + result.getMethod().getMethodName();
    }
}
