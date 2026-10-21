package fr.teddz.benchburner.core;

public record SystemStats(
double cpuLoadTotalPercent,
double[] cpuLoadCorePercents,
double cpuTemperatureC,
double cpuClockMhz,
long jvmHeapUsedBytes,
long jvmHeapCommittedBytes,
long systemMemoryUsedBytes,
long systemMemoryTotalBytes) {
public SystemStats {
cpuLoadCorePercents = cpuLoadCorePercents == null ? new double[0] : cpuLoadCorePercents.clone();
}
}
