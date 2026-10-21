package fr.teddz.benchburner.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class CsvExporterTest {
@TempDir
Path tempDirectory;

@Test
void writesHeaderAndOneRowPerFrame() throws Exception {
CsvExporter exporter = new CsvExporter();
Path file = exporter.export(tempDirectory, new double[] {3, 4});
assertTrue(Files.exists(file));
String name = file.getFileName().toString();
assertTrue(name.startsWith("benchburner-run-"));
assertTrue(name.endsWith(".csv"));
String content = Files.readString(file);
String[] lines = content.split("\n");
assertEquals("frame,frametime_ms,fps", lines[0].trim());
assertEquals("1,3.000,333.33", lines[1].trim());
assertEquals("2,4.000,250.00", lines[2].trim());
}
}
