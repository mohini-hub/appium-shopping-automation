package com.automation.models;

/** Sort options offered by the product catalog. The label is the text shown in the sort dialog. */
public enum SortOption {

    NAME_ASCENDING("Name - Ascending"),
    NAME_DESCENDING("Name - Descending"),
    PRICE_ASCENDING("Price - Ascending"),
    PRICE_DESCENDING("Price - Descending");

    private final String label;

    SortOption(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }
}
