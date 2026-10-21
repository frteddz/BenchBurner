package fr.teddz.benchburner.core;

public final class FpsWindowCalculator {
private static final long UPDATE_INTERVAL_NANOS = 50_000_000L;
private static final int SMOOTH_FRAMES = 16;

private final FrameRingBuffer ring;
private final double[] scratch;
private FpsStats cached = new FpsStats(Double.NaN, Double.NaN, Double.NaN, Double.NaN);
private boolean dirty;
private long lastRecomputeNanos;

public FpsWindowCalculator(int windowFrames) {
ring = new FrameRingBuffer(windowFrames);
scratch = new double[Math.max(1, windowFrames)];
}

public void addFrameTime(double frameTimeMs) {
ring.add(frameTimeMs);
dirty = true;
}

public FpsStats stats() {
long now = System.nanoTime();
if (dirty && (lastRecomputeNanos == 0 || now - lastRecomputeNanos >= UPDATE_INTERVAL_NANOS)) {
cached = compute();
lastRecomputeNanos = now;
dirty = false;
}
return cached;
}

public int copyLatest(double[] out) {
return ring.copyLatest(out);
}

private FpsStats compute() {
int count = ring.count();
if (count < SMOOTH_FRAMES) {
return new FpsStats(Double.NaN, Double.NaN, Double.NaN, Double.NaN);
}
ring.copyInto(scratch);
double total = 0;
for (int i = 0; i < count; i++) {
total += scratch[i];
}
double recentTotal = 0;
for (int i = count - SMOOTH_FRAMES; i < count; i++) {
recentTotal += scratch[i];
}
double windowAverageMs = total / count;
double recentAverageMs = recentTotal / SMOOTH_FRAMES;
double onePercentLow = BenchmarkMath.worstAverageFrametimeIn(scratch, count, 0.01);
double oneTenthPercentLow = BenchmarkMath.worstAverageFrametimeIn(scratch, count, 0.001);
return new FpsStats(
1000.0 / recentAverageMs,
1000.0 / windowAverageMs,
1000.0 / onePercentLow,
1000.0 / oneTenthPercentLow);
}
}
