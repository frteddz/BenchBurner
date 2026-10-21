package fr.teddz.benchburner.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class PresetsTest {
@TempDir
Path tempDirectory;

@Test
void minimalPresetTrimsEverythingToTheBasics() {
ConfigData data = new ConfigData();
Presets.apply("minimal", data);
assertTrue(data.showFpsModule);
assertTrue(data.showGraphModule);
assertTrue(data.showBenchmarkModule);
assertFalse(data.showFpsAverage);
assertFalse(data.showGpuModule);
assertFalse(data.showCpuModule);
assertFalse(data.showRamModule);
assertEquals("minimal", data.preset);
}

@Test
void standardPresetKeepsEverythingButCoreGrids() {
ConfigData data = new ConfigData();
Presets.apply("full", data);
Presets.apply("standard", data);
assertTrue(data.showFpsModule);
assertTrue(data.showFpsZeroPointOneLow);
assertTrue(data.showGpuModule);
assertTrue(data.showGpuVram);
assertTrue(data.showCpuModule);
assertFalse(data.showCpuCores);
assertTrue(data.showRamModule);
assertTrue(data.showGraphModule);
assertEquals("standard", data.preset);
}

@Test
void fullPresetTurnsEverythingOn() {
ConfigData data = new ConfigData();
Presets.apply("minimal", data);
Presets.apply("full", data);
assertTrue(data.showCpuCores);
assertTrue(data.showGpuModule);
assertTrue(data.showGpuFan);
assertTrue(data.showJvmLine);
assertEquals("full", data.preset);
}

@Test
void unknownPresetChangesNothing() {
ConfigData data = new ConfigData();
data.showCpuCores = true;
Presets.apply("potato", data);
assertTrue(data.showCpuCores);
assertEquals("standard", data.preset);
}

@Test
void configRoundTrips() throws Exception {
Path config = tempDirectory.resolve("benchburner.json");
ConfigStore saveStore = new ConfigStore(tempDirectory);
saveStore.data().x = 42;
saveStore.data().scale = 1.75;
saveStore.data().fpsBadThreshold = 25;
saveStore.data().badColor = 0xFF2200;
saveStore.save();
assertTrue(Files.exists(config));

ConfigStore loadStore = new ConfigStore(tempDirectory);
assertEquals(42, loadStore.data().x);
assertEquals(1.75, loadStore.data().scale, 0.0001);
assertEquals(25, loadStore.data().fpsBadThreshold, 0.0001);
assertEquals(0xFF2200, loadStore.data().badColor);
}
}
