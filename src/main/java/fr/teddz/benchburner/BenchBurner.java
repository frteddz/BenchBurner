package fr.teddz.benchburner;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;

import fr.teddz.benchburner.config.ConfigStore;
import fr.teddz.benchburner.core.BenchmarkManager;
import fr.teddz.benchburner.core.CsvExporter;
import fr.teddz.benchburner.core.Dependencies;
import fr.teddz.benchburner.core.FpsWindowCalculator;
import fr.teddz.benchburner.core.GpuSampleWorker;
import fr.teddz.benchburner.core.PlatformEntry;
import fr.teddz.benchburner.core.SystemStatsSampler;

public final class BenchBurner implements ClientModInitializer {
@Override
public void onInitializeClient() {
ConfigStore configStore = new ConfigStore(FabricLoader.getInstance().getConfigDir());
BenchmarkManager benchmark = new BenchmarkManager();
FpsWindowCalculator fpsCalculator = new FpsWindowCalculator(24000);
SystemStatsSampler statsSampler = new SystemStatsSampler();
Thread statsThread = new Thread(statsSampler, "benchburner-stats");
statsThread.setDaemon(true);
statsThread.start();
GpuSampleWorker gpuWorker = new GpuSampleWorker();
Thread gpuThread = new Thread(gpuWorker, "benchburner-gpu");
gpuThread.setDaemon(true);
gpuThread.start();
Dependencies dependencies = new Dependencies(configStore, benchmark, fpsCalculator, statsSampler, gpuWorker, new CsvExporter());
PlatformEntry platform = Buckets.pick(gameVersion());
platform.init(dependencies);
}

private static String gameVersion() {
return FabricLoader.getInstance().getModContainer("minecraft")
.map(container -> container.getMetadata().getVersion().getFriendlyString())
.orElse("");
}
}
