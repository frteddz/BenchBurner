package fr.teddz.benchburner.core;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

public final class NvidiaGpuReader {
private static final String QUERY = "utilization.gpu,temperature.gpu,power.draw,memory.used,memory.total,clocks.current.graphics,clocks.current.memory,fan.speed";

public static NvidiaGpuReader create() {
try {
Process process = new ProcessBuilder("nvidia-smi", "-L").start();
if (!process.waitFor(2, TimeUnit.SECONDS)) {
process.destroyForcibly();
return null;
}
if (process.exitValue() != 0) {
process.destroyForcibly();
return null;
}
return new NvidiaGpuReader();
} catch (IOException | InterruptedException e) {
return null;
}
}

public GpuSample sample() {
try {
Process process = new ProcessBuilder(
"nvidia-smi",
"--query-gpu=" + QUERY,
"--format=csv,noheader,nounits").start();
InputStream stream = process.getInputStream();
String output = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
if (!process.waitFor(2, TimeUnit.SECONDS)) {
process.destroyForcibly();
return GpuSample.UNAVAILABLE;
}
if (process.exitValue() != 0 || output.isBlank()) {
return GpuSample.UNAVAILABLE;
}
return parseNvidiaCsv(output);
} catch (IOException | InterruptedException e) {
return GpuSample.UNAVAILABLE;
}
}

public static GpuSample parseNvidiaCsv(String output) {
String line = output.trim();
int newline = line.indexOf('\n');
if (newline >= 0) line = line.substring(0, newline);
String[] parts = line.split(",");
if (parts.length < 8) return GpuSample.UNAVAILABLE;
return new GpuSample(
parseNumber(parts[0]),
parseNumber(parts[1]),
parseNumber(parts[2]),
parseNumber(parts[7]),
parseNumber(parts[3]) * 1048576.0,
parseNumber(parts[4]) * 1048576.0,
parseNumber(parts[5]),
parseNumber(parts[6]));
}

public static double parseNumber(String token) {
if (token == null) return Double.NaN;
String clean = token.trim();
if (clean.isEmpty() || clean.indexOf('[') >= 0) return Double.NaN;
try {
double value = Double.parseDouble(clean);
if (!Double.isFinite(value) || value < 0) return Double.NaN;
return value;
} catch (NumberFormatException e) {
return Double.NaN;
}
}
}
