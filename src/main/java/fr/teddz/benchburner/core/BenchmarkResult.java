package fr.teddz.benchburner.core;

public record BenchmarkResult(BenchmarkSummary summary, double[] frameTimesMs) {
}
