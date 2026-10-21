package fr.teddz.benchburner.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class AmdGpuReaderTest {
@Test
void parsesSysfsValuesWithRealUnits() {
GpuSample sample = AmdGpuReader.parse("45", "71000", "123456789", "536870912", "4294967296");
assertEquals(45.0, sample.usagePercent(), 0.0001);
assertEquals(71.0, sample.temperatureC(), 0.0001);
assertEquals(123.456789, sample.powerWatts(), 0.000001);
assertEquals(536870912.0, sample.vramUsedBytes(), 1.0);
assertEquals(4294967296.0, sample.vramTotalBytes(), 1.0);
assertTrue(Double.isNaN(sample.fanPercent()));
assertTrue(Double.isNaN(sample.coreClockMhz()));
}

@Test
void missingFilesBecomeNaNWithoutThrowing() {
GpuSample sample = AmdGpuReader.parse("", "", "", "", "");
assertTrue(Double.isNaN(sample.usagePercent()));
assertTrue(Double.isNaN(sample.temperatureC()));
assertTrue(Double.isNaN(sample.powerWatts()));
assertTrue(Double.isNaN(sample.vramUsedBytes()));
assertTrue(Double.isNaN(sample.vramTotalBytes()));
}

@Test
void garbageValuesBecomeNaN() {
GpuSample sample = AmdGpuReader.parse("not a number", "-1", "N/A", "x", "x");
assertTrue(Double.isNaN(sample.usagePercent()));
assertTrue(Double.isNaN(sample.temperatureC()));
assertTrue(Double.isNaN(sample.powerWatts()));
assertTrue(Double.isNaN(sample.vramUsedBytes()));
}

@Test
void missingAllFilesYieldsUnavailableEquivalentShape() {
GpuSample sample = AmdGpuReader.parse("", "", "", "", "");
assertTrue(Double.isNaN(sample.usagePercent()) && Double.isNaN(sample.powerWatts()));
}
}
