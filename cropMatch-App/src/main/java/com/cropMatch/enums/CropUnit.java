package com.cropMatch.enums;

public enum CropUnit {
    KILOGRAM("kg"),
    GRAM("g"),
    DOZEN("dozen"),
    PIECE("piece"),
    LITRE("litre"),
    MILLILITRE("ml"),
    QUINTAL("quintal"),
    TONNE("tonne"),
    BUNDLE("bundle"),
    BOX("box"),
    CRATE("crate"),
    BAG("bag");

    private final String displayName;

    CropUnit(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

