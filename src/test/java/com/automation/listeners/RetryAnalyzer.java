package com.automation.listeners;

import com.automation.config.ConfigReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/** Re-runs a failed test up to 'retryCount' times (config.properties) to absorb flaky failures. */
public class RetryAnalyzer implements IRetryAnalyzer {

    private static final Logger LOG = LogManager.getLogger(RetryAnalyzer.class);

    private final int maxRetries = ConfigReader.getInt("retryCount");
    private int attempts = 0;

    @Override
    public boolean retry(ITestResult result) {
        if (attempts < maxRetries) {
            attempts++;
            LOG.warn("Retrying {} (attempt {} of {})", result.getMethod().getMethodName(), attempts, maxRetries);
            return true;
        }
        return false;
    }
}
