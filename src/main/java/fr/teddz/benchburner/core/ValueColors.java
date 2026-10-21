package fr.teddz.benchburner.core;

public final class ValueColors {
private ValueColors() {
}

public static int lowIsBad(double value, double badBelow, double goodAbove, int badColor, int mediumColor, int goodColor) {
if (!Double.isFinite(value)) return 0;
if (value < badBelow) return badColor;
if (value < goodAbove) return mediumColor;
return goodColor;
}

public static int highIsBad(double value, double mediumAbove, double badAbove, int badColor, int mediumColor, int goodColor) {
if (!Double.isFinite(value)) return 0;
if (value > badAbove) return badColor;
if (value > mediumAbove) return mediumColor;
return goodColor;
}
}
