package fr.teddz.benchburner.core;

public record FpsStats(double currentFps, double averageFps, double onePercentLowFps, double oneTenthPercentLowFps) {
}
