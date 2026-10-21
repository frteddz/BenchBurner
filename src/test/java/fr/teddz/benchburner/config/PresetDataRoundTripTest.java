package fr.teddz.benchburner.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

class PresetDataRoundTripTest {
@Test
void SnapshotCapturesChangesAndApplyMovesThem() {
ConfigData source = new ConfigData();
source.showFpsAverage = false;
source.showCpuCores = true;
source.graphWidth = 220;
source.opacity = 0.65;
source.scale = 1.75;
source.badColor = 0x123456;
source.fpsGoodThreshold = 90.0;
PresetData snapshot = Presets.snapshot(source);

ConfigData target = new ConfigData();
Presets.applyData(snapshot, target);

assertEquals(false, target.showFpsAverage);
assertEquals(true, target.showCpuCores);
assertEquals(220, target.graphWidth);
assertEquals(0.65, target.opacity);
assertEquals(1.75, target.scale);
assertEquals(0x123456, target.badColor);
assertEquals(90.0, target.fpsGoodThreshold);
assertEquals("standard", target.preset);
assertEquals(8, target.x);
assertEquals(8, target.y);
}

@Test
void ApplyReplacesPreviousValuesEntirely() {
ConfigData source = new ConfigData();
source.showCpuModule = false;
source.showBackground = false;
source.useCustomFont = false;
PresetData snapshot = Presets.snapshot(source);

ConfigData target = new ConfigData();
Presets.apply("full", target);
assertNotEquals(false, target.showCpuModule);

Presets.applyData(snapshot, target);
assertEquals(false, target.showCpuModule);
assertEquals(false, target.showBackground);
assertEquals(false, target.useCustomFont);
}
}
