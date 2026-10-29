package com.automation.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class ScreenshotUtil {

    private static final Logger LOG = LogManager.getLogger(ScreenshotUtil.class);
    private static final DateTimeFormatter TIMESTAMP = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");
    private static final Path OUTPUT_DIR = Path.of("target", "screenshots");

    private ScreenshotUtil() {
    }

    /** Takes a screenshot and returns the PNG bytes. */
    public static byte[] takeScreenshot(WebDriver driver) {
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
    }

    /** Saves PNG bytes to target/screenshots and returns the file path (or null if saving failed). */
    public static Path save(byte[] png, String name) {
        try {
            Files.createDirectories(OUTPUT_DIR);
            Path file = OUTPUT_DIR.resolve(name + "_" + LocalDateTime.now().format(TIMESTAMP) + ".png");
            Files.write(file, png);
            return file;
        } catch (IOException e) {
            LOG.warn("Could not save screenshot: {}", e.getMessage());
            return null;
        }
    }
}
