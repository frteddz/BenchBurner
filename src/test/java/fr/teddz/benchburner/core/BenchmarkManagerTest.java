package fr.teddz.benchburner.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class BenchmarkManagerTest {
@Test
void lifecycleAndSummaryMath() {
BenchmarkManager manager = new BenchmarkManager();
assertNull(manager.stop());
manager.start();
assertTrue(manager.isRecording());
manager.recordFrame(10);
manager.recordFrame(20);
manager.recordFrame(30);
manager.recordFrame(40);
assertEquals(0.1, manager.recordedSeconds(), 0.0001);
BenchmarkResult result = manager.stop();
assertEquals(4, result.frameTimesMs().length);
BenchmarkSummary summary = result.summary();
assertEquals(0.1, summary.durationSeconds(), 0.0001);
assertEquals(4, summary.frameCount());
assertEquals(40, summary.averageFps(), 0.001);
assertEquals(25, summary.minFps(), 0.001);
assertEquals(100, summary.maxFps(), 0.001);
assertEquals(25, summary.onePercentLowFps(), 0.001);
assertEquals(25, summary.oneTenthPercentLowFps(), 0.001);
assertEquals(25, summary.averageFrametimeMs(), 0.001);
assertFalse(manager.isRecording());
}

@Test
void summaryStaysVisibleForAWhileAfterStop() {
BenchmarkManager manager = new BenchmarkManager();
manager.start();
manager.recordFrame(10);
BenchmarkResult result = manager.stop();
long now = System.nanoTime();
BenchmarkSummary visible = manager.visibleSummary(now);
assertEquals(result.summary(), visible);
}

@Test
void startingTwiceDoesNotResetTheRun() {
BenchmarkManager manager = new BenchmarkManager();
manager.start();
manager.recordFrame(10);
manager.start();
assertTrue(manager.recordedSeconds() > 0);
}

@Test
void lastResultSurvivesUntilTheNextRunStarts() {
BenchmarkManager manager = new BenchmarkManager();
assertNull(manager.lastResult());
manager.start();
manager.recordFrame(10);
manager.recordFrame(20);
manager.stop();
assertEquals(2, manager.lastResult().frameTimesMs().length);
manager.start();
assertNull(manager.lastResult());
}

@Test
void recordingIgnoresInvalidFrames() {
BenchmarkManager manager = new BenchmarkManager();
manager.start();
manager.recordFrame(Double.NaN);
manager.recordFrame(-1);
manager.recordFrame(0);
manager.recordFrame(16);
BenchmarkResult result = manager.stop();
assertEquals(1, result.frameTimesMs().length);
}
}
