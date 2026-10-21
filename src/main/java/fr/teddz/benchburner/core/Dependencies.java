package fr.teddz.benchburner.core;

import fr.teddz.benchburner.config.ConfigStore;

public final class Dependencies {
private final ConfigStore configStore;
private final BenchmarkManager benchmark;
private final FpsWindowCalculator fpsCalculator;
private final SystemStatsSampler statsSampler;
private final GpuSampleWorker gpuWorker;
private final CsvExporter csvExporter;

public Dependencies(ConfigStore configStore, BenchmarkManager benchmark, FpsWindowCalculator fpsCalculator, SystemStatsSampler statsSampler, GpuSampleWorker gpuWorker, CsvExporter csvExporter) {
this.configStore = configStore;
this.benchmark = benchmark;
this.fpsCalculator = fpsCalculator;
this.statsSampler = statsSampler;
this.gpuWorker = gpuWorker;
this.csvExporter = csvExporter;
}

public ConfigStore configStore() {
return this.configStore;
}

public BenchmarkManager benchmark() {
return this.benchmark;
}

public FpsWindowCalculator fpsCalculator() {
return this.fpsCalculator;
}

public SystemStatsSampler statsSampler() {
return this.statsSampler;
}

public GpuSampleWorker gpuWorker() {
return this.gpuWorker;
}

public CsvExporter csvExporter() {
return this.csvExporter;
}
}
