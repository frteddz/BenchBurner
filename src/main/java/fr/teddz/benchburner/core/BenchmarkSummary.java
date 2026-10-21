package fr.teddz.benchburner.core;

public record BenchmarkSummary(
double durationSeconds,
int frameCount,
double averageFps,
double minFps,
double maxFps,
double onePercentLowFps,
double oneTenthPercentLowFps,
double averageFrametimeMs) {
}
