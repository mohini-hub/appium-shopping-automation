package com.automation.driver;

import com.automation.config.ConfigReader;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.appium.java_client.service.local.flags.GeneralServerFlag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URI;

/**
 * Optionally starts a local Appium server for the whole test run.
 * Enable it with appium.autoStart=true (requires the 'appium' command to be on the PATH).
 * When it is disabled, start the server yourself with: appium
 */
public final class AppiumServerManager {

    private static final Logger LOG = LogManager.getLogger(AppiumServerManager.class);
    private static final int DEFAULT_PORT = 4723;

    private static AppiumDriverLocalService service;

    private AppiumServerManager() {
    }

    public static synchronized void startIfConfigured() {
        if (!ConfigReader.getBoolean("appium.autoStart") || service != null) {
            return;
        }

        URI uri = URI.create(ConfigReader.get("appiumServerUrl"));
        int port = uri.getPort() == -1 ? DEFAULT_PORT : uri.getPort();

        service = new AppiumServiceBuilder()
                .withIPAddress(uri.getHost())
                .usingPort(port)
                .withArgument(GeneralServerFlag.LOG_LEVEL, "error")
                .build();
        service.start();
        LOG.info("Appium server started on {}:{}", uri.getHost(), port);
    }

    public static synchronized void stop() {
        if (service != null && service.isRunning()) {
            service.stop();
            LOG.info("Appium server stopped");
        }
        service = null;
    }
}
