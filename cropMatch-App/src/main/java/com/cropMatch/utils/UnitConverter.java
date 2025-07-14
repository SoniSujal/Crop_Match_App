package com.cropMatch.utils;

import com.cropMatch.enums.CropUnit;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;

@Component
public final class UnitConverter {

    private static final Map<CropUnit, Double> toGramMap = new EnumMap<>(CropUnit.class);
    private static final Map<CropUnit, Double> toMillilitreMap = new EnumMap<>(CropUnit.class);
    private static final Map<CropUnit, Double> pieceBasedPackagingMap = new EnumMap<>(CropUnit.class);

    static {
        // Mass (base: gram)
        toGramMap.put(CropUnit.GRAM, 1.0);
        toGramMap.put(CropUnit.KILOGRAM, 1000.0);
        toGramMap.put(CropUnit.QUINTAL, 100000.0);  // 100 kg
        toGramMap.put(CropUnit.TONNE, 1000000.0);   // 1000 kg

        // Volume (base: millilitre)
        toMillilitreMap.put(CropUnit.MILLILITRE, 1.0);
        toMillilitreMap.put(CropUnit.LITRE, 1000.0);

        // Piece-based (estimated base: piece)
        pieceBasedPackagingMap.put(CropUnit.BOX, 25.0);
        pieceBasedPackagingMap.put(CropUnit.BAG, 12.5);
        pieceBasedPackagingMap.put(CropUnit.CRATE, 40.0);
        pieceBasedPackagingMap.put(CropUnit.BUNDLE, 7.5);
        pieceBasedPackagingMap.put(CropUnit.DOZEN, 12.0);
        pieceBasedPackagingMap.put(CropUnit.PIECE, 1.0);
    }

    private UnitConverter() {
        // Prevent instantiation
    }

    /**
     * Converts a value from one unit to another of the same type (mass, volume, piece-based).
     */
    public static double convert(double value, CropUnit from, CropUnit to) {
        if (from == to) return value;

        if (isMassConvertible(from) && isMassConvertible(to)) {
            double grams = value * toGramMap.get(from);
            return grams / toGramMap.get(to);
        }

        if (isVolumeConvertible(from) && isVolumeConvertible(to)) {
            double millilitres = value * toMillilitreMap.get(from);
            return millilitres / toMillilitreMap.get(to);
        }

        if (isPieceBased(from) && isPieceBased(to)) {
            double pieces = value * pieceBasedPackagingMap.getOrDefault(from, 1.0);
            return pieces / pieceBasedPackagingMap.getOrDefault(to, 1.0);
        }

        throw new IllegalArgumentException("Incompatible unit conversion: " + from + " to " + to);
    }

    /**
     * Returns the universal base unit for normalization:
     * - GRAM for mass
     * - MILLILITRE for volume
     * - PIECE for piece-based units
     */
    public static CropUnit getBaseUnit(CropUnit unit) {
        if (isMassConvertible(unit)) return CropUnit.GRAM;
        if (isVolumeConvertible(unit)) return CropUnit.MILLILITRE;
        if (isPieceBased(unit)) return CropUnit.PIECE;

        throw new IllegalArgumentException("No base unit for unknown type: " + unit);
    }

    public static boolean isMassConvertible(CropUnit unit) {
        return toGramMap.containsKey(unit);
    }

    public static boolean isVolumeConvertible(CropUnit unit) {
        return toMillilitreMap.containsKey(unit);
    }

    public static boolean isPieceBased(CropUnit unit) {
        return pieceBasedPackagingMap.containsKey(unit);
    }

    /**
     * Checks whether two units can be converted to each other.
     */
    public static boolean isConvertible(CropUnit from, CropUnit to) {
        return (isMassConvertible(from) && isMassConvertible(to)) ||
                (isVolumeConvertible(from) && isVolumeConvertible(to)) ||
                (isPieceBased(from) && isPieceBased(to)) ||
                from == to;
    }
}
