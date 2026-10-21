package fr.teddz.benchburner.core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

public final class AmdGpuReader {
private final Path deviceDirectory;
private final Path hwmonDirectory;

private AmdGpuReader(Path deviceDirectory, Path hwmonDirectory) {
this.deviceDirectory = deviceDirectory;
this.hwmonDirectory = hwmonDirectory;
}

public static AmdGpuReader create() {
Path drmRoot = Path.of("/sys/class/drm");
try {
List<Path> cards;
try (var stream = Files.list(drmRoot)) {
cards = stream
.filter(path -> path.getFileName().toString().startsWith("card"))
.filter(path -> path.getFileName().toString().length() > 4)
.filter(path -> Files.isDirectory(path))
.sorted()
.toList();
}
for (Path card : cards) {
Path device = card.resolve("device");
String vendor = readTrim(device.resolve("vendor"));
if (!vendor.equals("0x1002")) continue;
Path hwmon = findHwmon(device);
return new AmdGpuReader(device, hwmon);
}
} catch (IOException e) {
return null;
}
return null;
}

public GpuSample sample() {
return parse(
readTrim(deviceDirectory.resolve("gpu_busy_percent")),
readTrim(hwmonDirectory.resolve("temp1_input")),
readTrim(hwmonDirectory.resolve("power1_average")),
readTrim(deviceDirectory.resolve("mem_info_vram_used")),
readTrim(deviceDirectory.resolve("mem_info_vram_total")));
}

public static GpuSample parse(String busyRaw, String tempRaw, String powerRaw, String vramUsedRaw, String vramTotalRaw) {
return new GpuSample(
parseScaled(busyRaw, 1.0),
parseScaled(tempRaw, 0.001),
parseScaled(powerRaw, 0.000001),
Double.NaN,
parseScaled(vramUsedRaw, 1.0),
parseScaled(vramTotalRaw, 1.0),
Double.NaN,
Double.NaN);
}

public static double parseScaled(String raw, double scale) {
if (raw == null || raw.isEmpty()) return Double.NaN;
try {
double value = Double.parseDouble(raw.trim()) * scale;
if (!Double.isFinite(value) || value < 0) return Double.NaN;
return value;
} catch (NumberFormatException e) {
return Double.NaN;
}
}

private static Path findHwmon(Path device) {
Path hwmonRoot = device.resolve("hwmon");
if (!Files.isDirectory(hwmonRoot)) return null;
try {
List<Path> entries;
try (var stream = Files.list(hwmonRoot)) {
entries = stream
.filter(path -> path.getFileName().toString().startsWith("hwmon"))
.filter(path -> Files.isDirectory(path))
.sorted()
.toList();
}
for (Path entry : entries) {
if (Files.exists(entry.resolve("temp1_input"))) return entry;
}
for (Path entry : entries) {
return entry;
}
} catch (IOException e) {
return null;
}
return null;
}

private static String readTrim(Path path) {
try {
return Files.readString(path).trim();
} catch (IOException e) {
return "";
}
}
}
