package fr.teddz.benchburner.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class NvidiaGpuReaderTest {
@Test
void parsesAFullHealthyLine() {
GpuSample sample = NvidiaGpuReader.parseNvidiaCsv("12, 61, 139.45, 875, 8192, 2535, 8000, 32");
assertEquals(12.0, sample.usagePercent(), 0.0001);
assertEquals(61.0, sample.temperatureC(), 0.0001);
assertEquals(139.45, sample.powerWatts(), 0.0001);
assertEquals(32.0, sample.fanPercent(), 0.0001);
assertEquals(875 * 1048576.0, sample.vramUsedBytes(), 1.0);
assertEquals(8192 * 1048576.0, sample.vramTotalBytes(), 1.0);
assertEquals(2535.0, sample.coreClockMhz(), 0.0001);
assertEquals(8000.0, sample.memoryClockMhz(), 0.0001);
}

@Test
void bracketedMissingValuesBecomeNaN() {
GpuSample sample = NvidiaGpuReader.parseNvidiaCsv("0, 45, [N/A], 512, 8192, 300, 400, [Not Supported]");
assertEquals(0.0, sample.usagePercent(), 0.0001);
assertEquals(45.0, sample.temperatureC(), 0.0001);
assertTrue(Double.isNaN(sample.powerWatts()));
assertTrue(Double.isNaN(sample.fanPercent()));
assertEquals(512 * 1048576.0, sample.vramUsedBytes(), 1.0);
assertEquals(400.0, sample.memoryClockMhz(), 0.0001);
}

@Test
void onlyTheFirstGpuLineIsUsed() {
GpuSample sample = NvidiaGpuReader.parseNvidiaCsv("10, 50, 100.0, 1000, 8192, 2000, 6000, 20\n99, 99, 999.0, 9999, 9999, 9999, 9999, 99");
assertEquals(10.0, sample.usagePercent(), 0.0001);
assertEquals(20.0, sample.fanPercent(), 0.0001);
}

@Test
void shortLinesAreUnavailable() {
assertEquals(GpuSample.UNAVAILABLE, NvidiaGpuReader.parseNvidiaCsv("1, 2, 3"));
assertEquals(GpuSample.UNAVAILABLE, NvidiaGpuReader.parseNvidiaCsv(""));
}

@Test
void parseNumberRejectsGarbageAndNegatives() {
assertTrue(Double.isNaN(NvidiaGpuReader.parseNumber("garbage")));
assertTrue(Double.isNaN(NvidiaGpuReader.parseNumber("-5")));
assertTrue(Double.isNaN(NvidiaGpuReader.parseNumber(null)));
assertEquals(7.5, NvidiaGpuReader.parseNumber(" 7.5 "), 0.0001);
}
}
