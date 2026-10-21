package fr.teddz.benchburner.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ValueColorsTest {
private static final int BAD = 0xFF4D4D;
private static final int MEDIUM = 0xFFD70F;
private static final int GOOD = 0x4DD26E;

@Test
void lowIsBadReturnsRedBelowTheBadThreshold() {
assertEquals(BAD, ValueColors.lowIsBad(20, 30, 60, BAD, MEDIUM, GOOD));
}

@Test
void lowIsBadReturnsYellowBetweenThresholds() {
assertEquals(MEDIUM, ValueColors.lowIsBad(45, 30, 60, BAD, MEDIUM, GOOD));
}

@Test
void lowIsBadReturnsGreenAboveTheGoodThreshold() {
assertEquals(GOOD, ValueColors.lowIsBad(120, 30, 60, BAD, MEDIUM, GOOD));
}

@Test
void highIsBadReturnsRedAboveTheBadThreshold() {
assertEquals(BAD, ValueColors.highIsBad(95, 60, 90, BAD, MEDIUM, GOOD));
}

@Test
void highIsBadReturnsYellowBetweenThresholds() {
assertEquals(MEDIUM, ValueColors.highIsBad(75, 60, 90, BAD, MEDIUM, GOOD));
}

@Test
void highIsBadReturnsGreenBelowTheMediumThreshold() {
assertEquals(GOOD, ValueColors.highIsBad(30, 60, 90, BAD, MEDIUM, GOOD));
}

@Test
void notANumberFallsBackToDefaultColor() {
assertEquals(0, ValueColors.lowIsBad(Double.NaN, 30, 60, BAD, MEDIUM, GOOD));
assertEquals(0, ValueColors.highIsBad(Double.NaN, 60, 90, BAD, MEDIUM, GOOD));
}

@Test
void zeroIsStillARealColor() {
assertEquals(BAD, ValueColors.lowIsBad(0.0, 30, 60, BAD, MEDIUM, GOOD));
}
}
