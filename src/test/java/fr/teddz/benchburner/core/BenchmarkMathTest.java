package fr.teddz.benchburner.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class BenchmarkMathTest {
@Test
void averageOfSimpleArray() {
assertEquals(2.0, BenchmarkMath.average(new double[] {1, 2, 3}), 0.0001);
}

@Test
void averageOfEmptyArrayIsNaN() {
assertTrue(Double.isNaN(BenchmarkMath.average(new double[0])));
}

@Test
void minAndMax() {
double[] values = {5, 2, 9, 4};
assertEquals(2, BenchmarkMath.min(values), 0.0001);
assertEquals(9, BenchmarkMath.max(values), 0.0001);
}

@Test
void worstAverageTakesHighestValues() {
double[] values = {1, 2, 3, 4};
assertEquals(4, BenchmarkMath.worstAverageFrametime(values, 0.25), 0.0001);
}

@Test
void worstAverageRoundsUpToAtLeastOneValue() {
double[] values = {1, 2, 3, 4, 5};
assertEquals(4.5, BenchmarkMath.worstAverageFrametime(values, 0.4), 0.0001);
assertEquals(5, BenchmarkMath.worstAverageFrametime(values, 0.05), 0.0001);
}

@Test
void worstAverageWithFractionAboveOneCoversAll() {
double[] values = {1, 2, 3, 4};
assertEquals(2.5, BenchmarkMath.worstAverageFrametime(values, 1.5), 0.0001);
}

@Test
void worstAverageRespectsExplicitLength() {
double[] values = {1, 2, 3, 4, 5};
assertEquals(2.5, BenchmarkMath.worstAverageFrametimeIn(values, 3, 0.34), 0.0001);
}

@Test
void emptyArrayGivesNaN() {
assertTrue(Double.isNaN(BenchmarkMath.worstAverageFrametime(new double[0], 0.01)));
}
}
