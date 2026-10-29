package com.automation.tests;

import com.automation.base.BaseTest;
import com.automation.base.TestGroups;
import com.automation.data.TestData;
import com.automation.models.SortOption;
import com.automation.pages.CatalogPage;
import com.automation.pages.ProductDetailPage;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Feature("Product catalog")
public class CatalogTest extends BaseTest {

    @Test(groups = TestGroups.SMOKE, description = "The catalog shows products when the app starts")
    @Story("Browse products")
    @Severity(SeverityLevel.BLOCKER)
    public void catalogShowsProducts() {
        CatalogPage catalog = new CatalogPage();

        Assert.assertTrue(catalog.isLoaded(), "The catalog should load");
        Assert.assertFalse(catalog.getVisibleProductNames().isEmpty(), "At least one product should be visible");
    }

    @Test(groups = TestGroups.REGRESSION, description = "A product opens its detail screen")
    @Story("Browse products")
    @Severity(SeverityLevel.NORMAL)
    public void productOpensItsDetailScreen() {
        CatalogPage catalog = new CatalogPage();
        Assert.assertTrue(catalog.isLoaded(), "The catalog should load");

        ProductDetailPage details = catalog.openProduct(TestData.BACKPACK);

        Assert.assertTrue(details.isDisplayed(), "The product detail screen should be displayed");
    }

    @Test(groups = TestGroups.REGRESSION, description = "Products can be sorted by name (A-Z)")
    @Story("Sort products")
    @Severity(SeverityLevel.NORMAL)
    public void productsCanBeSortedByNameAscending() {
        CatalogPage catalog = new CatalogPage();
        Assert.assertTrue(catalog.isLoaded(), "The catalog should load");

        List<String> names = catalog.sortBy(SortOption.NAME_ASCENDING).getVisibleProductNames();

        assertSorted(names, String.CASE_INSENSITIVE_ORDER, "Names should be sorted A-Z");
    }

    @Test(groups = TestGroups.REGRESSION, description = "Products can be sorted by name (Z-A)")
    @Story("Sort products")
    @Severity(SeverityLevel.NORMAL)
    public void productsCanBeSortedByNameDescending() {
        CatalogPage catalog = new CatalogPage();
        Assert.assertTrue(catalog.isLoaded(), "The catalog should load");

        List<String> names = catalog.sortBy(SortOption.NAME_DESCENDING).getVisibleProductNames();

        assertSorted(names, String.CASE_INSENSITIVE_ORDER.reversed(), "Names should be sorted Z-A");
    }

    @Test(groups = TestGroups.REGRESSION, description = "Products can be sorted by price (low to high)")
    @Story("Sort products")
    @Severity(SeverityLevel.NORMAL)
    public void productsCanBeSortedByPriceAscending() {
        CatalogPage catalog = new CatalogPage();
        Assert.assertTrue(catalog.isLoaded(), "The catalog should load");

        List<BigDecimal> prices = catalog.sortBy(SortOption.PRICE_ASCENDING).getVisibleProductPrices();

        assertSorted(prices, Comparator.<BigDecimal>naturalOrder(), "Prices should be sorted low to high");
    }

    @Test(groups = TestGroups.REGRESSION, description = "Products can be sorted by price (high to low)")
    @Story("Sort products")
    @Severity(SeverityLevel.NORMAL)
    public void productsCanBeSortedByPriceDescending() {
        CatalogPage catalog = new CatalogPage();
        Assert.assertTrue(catalog.isLoaded(), "The catalog should load");

        List<BigDecimal> prices = catalog.sortBy(SortOption.PRICE_DESCENDING).getVisibleProductPrices();

        assertSorted(prices, Comparator.<BigDecimal>reverseOrder(), "Prices should be sorted high to low");
    }

    /** Only the products visible on screen are compared; a visible slice of a sorted list is sorted too. */
    private static <T> void assertSorted(List<T> actual, Comparator<? super T> comparator, String message) {
        List<T> expected = new ArrayList<>(actual);
        expected.sort(comparator);
        Assert.assertEquals(actual, expected, message);
    }
}
