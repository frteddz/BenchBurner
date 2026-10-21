package fr.teddz.benchburner.core;

public record GpuSample(
double usagePercent,
double temperatureC,
double powerWatts,
double fanPercent,
double vramUsedBytes,
double vramTotalBytes,
double coreClockMhz,
double memoryClockMhz) {
public static final GpuSample UNAVAILABLE = new GpuSample(
Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN);
}
