package fr.teddz.benchburner.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class FpsWindowCalculatorTest {
@Test
void steadyFramesGiveSteadyFps() {
FpsWindowCalculator calculator = new FpsWindowCalculator(64);
for (int i = 0; i < 24; i++) calculator.addFrameTime(10);
FpsStats stats = calculator.stats();
assertEquals(100, stats.currentFps(), 1);
assertEquals(100, stats.averageFps(), 1);
assertEquals(100, stats.onePercentLowFps(), 1);
assertEquals(100, stats.oneTenthPercentLowFps(), 1);
}

@Test
void alternatingFramesProduceLowValues() {
FpsWindowCalculator calculator = new FpsWindowCalculator(64);
for (int i = 0; i < 24; i++) calculator.addFrameTime(i % 2 == 0 ? 10 : 20);
FpsStats stats = calculator.stats();
assertEquals(66.6667, stats.currentFps(), 0.5);
assertEquals(66.6667, stats.averageFps(), 0.5);
assertEquals(50, stats.onePercentLowFps(), 0.5);
assertEquals(50, stats.oneTenthPercentLowFps(), 0.5);
}

@Test
void tooFewFramesReportNaN() {
FpsWindowCalculator calculator = new FpsWindowCalculator(64);
for (int i = 0; i < 5; i++) calculator.addFrameTime(10);
FpsStats stats = calculator.stats();
assertTrue(Double.isNaN(stats.currentFps()));
}

@Test
void copyLatestForwardsToRing() {
FpsWindowCalculator calculator = new FpsWindowCalculator(64);
for (int i = 1; i <= 30; i++) calculator.addFrameTime(i);
double[] out = new double[3];
int copied = calculator.copyLatest(out);
assertEquals(3, copied);
assertEquals(28, out[0], 0.0001);
assertEquals(29, out[1], 0.0001);
assertEquals(30, out[2], 0.0001);
}
}
