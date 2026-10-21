package fr.teddz.benchburner.core;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public final class CsvExporter {
private static final DateTimeFormatter FILE_STAMP = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

public Path export(Path directory, double[] frameTimesMs) throws IOException {
String stamp = LocalDateTime.now().format(FILE_STAMP);
Path target = directory.resolve("benchburner-run-" + stamp + ".csv");
try (BufferedWriter writer = Files.newBufferedWriter(target, StandardCharsets.UTF_8)) {
writer.write("frame,frametime_ms,fps");
writer.newLine();
for (int i = 0; i < frameTimesMs.length; i++) {
double ms = frameTimesMs[i];
double fps = ms > 0 ? 1000.0 / ms : 0;
writer.write((i + 1) + "," + String.format(Locale.ROOT, "%.3f", ms) + "," + String.format(Locale.ROOT, "%.2f", fps));
writer.newLine();
}
}
return target;
}
}
