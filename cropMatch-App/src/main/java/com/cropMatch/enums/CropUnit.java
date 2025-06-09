package com.cropMatch.enums;

public enum CropUnit {
    KILOGRAM("Kilogram"),
    GRAM("Gram"),
    DOZEN("Dozen"),
    PIECE("Piece"),
    LITRE("Litre"),
    MILLILITRE("Millilitre"),
    QUINTAL("Quintal"),
    TONNE("Tonne"),
    BUNDLE("Bundle"),
    BOX("Box"),
    CRATE("Crate"),
    BAG("Bag");

    private final String displayName;

    CropUnit(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

