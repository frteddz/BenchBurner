package fr.teddz.benchburner.core;

public final class BenchmarkManager {
private static final long SUMMARY_DISPLAY_NANOS = 10_000_000_000L;

private GrowableDoubleList recordedFrames;
private BenchmarkSummary lastSummary;
private BenchmarkResult lastResult;
private long summaryUntilNanos;

public boolean isRecording() {
return recordedFrames != null;
}

public void start() {
if (recordedFrames != null) return;
recordedFrames = new GrowableDoubleList(4096);
lastSummary = null;
lastResult = null;
summaryUntilNanos = 0;
}

public BenchmarkResult stop() {
if (recordedFrames == null) return null;
GrowableDoubleList frames = recordedFrames;
recordedFrames = null;
if (frames.size() == 0) return null;
double[] frameTimes = frames.toArray();
BenchmarkSummary summary = buildSummary(frameTimes, frames.totalMs());
lastSummary = summary;
lastResult = new BenchmarkResult(summary, frameTimes);
summaryUntilNanos = System.nanoTime() + SUMMARY_DISPLAY_NANOS;
return lastResult;
}

public BenchmarkResult lastResult() {
return lastResult;
}

public void recordFrame(double frameTimeMs) {
if (recordedFrames != null) recordedFrames.add(frameTimeMs);
}

public double recordedSeconds() {
return recordedFrames == null ? 0 : recordedFrames.totalMs() / 1000.0;
}

public BenchmarkSummary visibleSummary(long nowNanos) {
if (lastSummary == null || nowNanos > summaryUntilNanos) return null;
return lastSummary;
}

private static BenchmarkSummary buildSummary(double[] frameTimes, double totalMs) {
double averageMs = BenchmarkMath.average(frameTimes);
double slowestFrameMs = BenchmarkMath.max(frameTimes);
double fastestFrameMs = BenchmarkMath.min(frameTimes);
double onePercentLow = BenchmarkMath.worstAverageFrametime(frameTimes, 0.01);
double oneTenthPercentLow = BenchmarkMath.worstAverageFrametime(frameTimes, 0.001);
return new BenchmarkSummary(
totalMs / 1000.0,
frameTimes.length,
1000.0 / averageMs,
1000.0 / slowestFrameMs,
1000.0 / fastestFrameMs,
1000.0 / onePercentLow,
1000.0 / oneTenthPercentLow,
averageMs);
}
}
