package com.automation.utils;

import java.math.BigDecimal;

public final class PriceUtils {

    private PriceUtils() {
    }

    /** Converts display text such as "$ 29.99" into a BigDecimal (29.99). */
    public static BigDecimal parse(String priceText) {
        String cleaned = priceText == null ? "" : priceText.replaceAll("[^0-9.]", "");
        if (cleaned.isEmpty()) {
            throw new IllegalArgumentException("No price found in text: '" + priceText + "'");
        }
        return new BigDecimal(cleaned);
    }
}
