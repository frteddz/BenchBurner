package fr.teddz.benchburner.b4.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;

import fr.teddz.benchburner.config.ConfigData;
import fr.teddz.benchburner.core.BenchmarkManager;
import fr.teddz.benchburner.core.BenchmarkSummary;
import fr.teddz.benchburner.core.FpsStats;
import fr.teddz.benchburner.core.FpsWindowCalculator;
import fr.teddz.benchburner.core.GpuSample;
import fr.teddz.benchburner.core.SystemStats;
import fr.teddz.benchburner.core.ValueColors;

public final class OverlayRenderer {
private static final Identifier COOLVETICA_FONT_ID = Identifier.fromNamespaceAndPath("benchburner", "coolvetica");
private static final FontDescription COOLVETICA_FONT = new FontDescription.Resource(COOLVETICA_FONT_ID);
private static final int PADDING = 4;
private static final int MODULE_GAP = 3;
private static final int LABEL_OFFSET = 4;
private static final int MAX_GPU_TEXT = 44;
private static final int NO_COLOR = 0;

private record Row(String label, String value, int valueColor) {
}

private record ModuleContent(List<Row> rows, boolean hasGraph) {
}

private double[] graphScratch = new double[110];

public void draw(GuiGraphicsExtractor graphics, ConfigData settings, FpsWindowCalculator fpsCalculator, SystemStats stats, GpuSample gpu, String gpuName, BenchmarkManager benchmark) {
Font font = Minecraft.getInstance().font;
if (font == null) return;
FpsStats fps = fpsCalculator.stats();
if (graphScratch.length != Math.max(32, settings.graphWidth)) {
graphScratch = new double[Math.max(32, settings.graphWidth)];
}
Style baseStyle = settings.useCustomFont ? Style.EMPTY.withFont(COOLVETICA_FONT) : Style.EMPTY;

List<ModuleContent> modules = collectModules(settings, fps, stats, gpu, gpuName, benchmark);
if (modules.isEmpty()) return;

int lineHeight = font.lineHeight + 2;
int[] labelWidths = new int[modules.size()];
int contentWidth = 0;
for (int i = 0; i < modules.size(); i++) {
ModuleContent module = modules.get(i);
int labelWidth = 0;
int moduleWidth = module.hasGraph() ? Math.max(0, settings.graphWidth) : 0;
for (Row row : module.rows()) {
int labelSpan = measure(font, baseStyle, row.label());
labelWidth = Math.max(labelWidth, labelSpan);
moduleWidth = Math.max(moduleWidth, labelSpan + LABEL_OFFSET + measure(font, baseStyle, row.value()));
}
labelWidths[i] = labelWidth;
contentWidth = Math.max(contentWidth, moduleWidth);
}
int boxWidth = contentWidth + PADDING * 2;

double scale = Math.clamp(settings.scale, 0.5, 3.0);
double opacity = Math.clamp(settings.opacity, 0.1, 1.0);
int textAlpha = (int) (opacity * 255);
int textArgb = (textAlpha << 24) | (settings.textColor & 0xFFFFFF);
int backgroundArgb = ((int) (opacity * 150)) << 24;

int[] boxTops = new int[modules.size()];
int[] boxHeights = new int[modules.size()];
int cursorY = 0;
for (int i = 0; i < modules.size(); i++) {
ModuleContent module = modules.get(i);
int height = PADDING * 2 + module.rows().size() * lineHeight;
if (module.hasGraph()) height += settings.graphHeight + 2;
boxTops[i] = cursorY;
boxHeights[i] = height;
cursorY += height + MODULE_GAP;
}
int totalHeight = boxTops[modules.size() - 1] + boxHeights[modules.size() - 1];

var pose = graphics.pose();
pose.pushMatrix();
pose.translate(settings.x, settings.y);
pose.scale((float) scale, (float) scale);

if (settings.showBackground) {
graphics.fill(0, 0, boxWidth, totalHeight, backgroundArgb);
}

for (int i = 0; i < modules.size(); i++) {
ModuleContent module = modules.get(i);
int top = boxTops[i];
int height = boxHeights[i];
int rowY = top + PADDING;
int labelWidth = labelWidths[i];
for (Row row : module.rows()) {
int valueRgb = row.valueColor() == NO_COLOR ? settings.textColor & 0xFFFFFF : row.valueColor();
int valueArgb = (textAlpha << 24) | (valueRgb & 0xFFFFFF);
graphics.text(font, component(row.label(), baseStyle), PADDING, rowY, textArgb, false);
graphics.text(font, component(row.value(), baseStyle), PADDING + labelWidth + LABEL_OFFSET, rowY, valueArgb, false);
rowY += lineHeight;
}
if (module.hasGraph()) {
drawGraph(graphics, fpsCalculator, settings, textAlpha, PADDING, top + height - PADDING);
}
}

pose.popMatrix();
}

private void drawGraph(GuiGraphicsExtractor graphics, FpsWindowCalculator fpsCalculator, ConfigData settings, int textAlpha, int x, int baseline) {
int count = fpsCalculator.copyLatest(graphScratch);
if (count < 2) return;
double maxMs = 1;
for (int i = 0; i < count; i++) {
maxMs = Math.max(maxMs, graphScratch[i]);
}
double mediumMs = settings.fpsGoodThreshold > 0 ? 1000.0 / settings.fpsGoodThreshold : 50;
double badMs = settings.fpsBadThreshold > 0 ? 1000.0 / settings.fpsBadThreshold : 100;
for (int i = 0; i < count; i++) {
int columnHeight = (int) Math.ceil(graphScratch[i] / maxMs * settings.graphHeight);
columnHeight = Math.clamp(columnHeight, 1, settings.graphHeight);
int rgb = ValueColors.highIsBad(graphScratch[i], mediumMs, badMs, settings.badColor, settings.mediumColor, settings.goodColor);
if (rgb == NO_COLOR) rgb = settings.textColor;
graphics.fill(x + i, baseline - columnHeight, x + i + 1, baseline, (textAlpha << 24) | (rgb & 0xFFFFFF));
}
}

private List<ModuleContent> collectModules(ConfigData settings, FpsStats fps, SystemStats stats, GpuSample gpu, String gpuName, BenchmarkManager benchmark) {
ArrayList<ModuleContent> modules = new ArrayList<>();
if (settings.showFpsModule) {
ArrayList<Row> rows = new ArrayList<>();
if (Double.isFinite(fps.currentFps())) {
rows.add(row("FPS:", oneDecimal(fps.currentFps()), fpsColor(settings, fps.currentFps())));
if (settings.showFpsAverage) {
rows.add(row("AVG:", oneDecimal(fps.averageFps()), fpsColor(settings, fps.averageFps())));
}
if (settings.showFpsOnePercentLow) {
rows.add(row("1% LOW:", oneDecimal(fps.onePercentLowFps()), fpsColor(settings, fps.onePercentLowFps())));
}
if (settings.showFpsZeroPointOneLow) {
rows.add(row("0.1% LOW:", oneDecimal(fps.oneTenthPercentLowFps()), fpsColor(settings, fps.oneTenthPercentLowFps())));
}
}
pushModule(modules, rows, false);
}
if (settings.showGpuModule) {
ArrayList<Row> rows = new ArrayList<>();
if (settings.showGpuName && !gpuName.isEmpty()) {
rows.add(row("GPU:", truncate(gpuName), NO_COLOR));
}
if (settings.showGpuUsage && Double.isFinite(gpu.usagePercent())) {
rows.add(row("USAGE:", oneDecimal(gpu.usagePercent()) + "%", usageColor(settings, gpu.usagePercent())));
}
if (settings.showGpuTemperature && Double.isFinite(gpu.temperatureC()) && gpu.temperatureC() > 1) {
rows.add(row("TEMP:", oneDecimal(gpu.temperatureC()) + "C", temperatureColor(settings, gpu.temperatureC())));
}
if (settings.showGpuPower && Double.isFinite(gpu.powerWatts()) && gpu.powerWatts() > 0) {
rows.add(row("POWER:", oneDecimal(gpu.powerWatts()) + "W", NO_COLOR));
}
if (settings.showGpuFan && Double.isFinite(gpu.fanPercent()) && gpu.fanPercent() >= 0) {
rows.add(row("FAN:", zeroDecimal(gpu.fanPercent()) + "%", usageColor(settings, gpu.fanPercent())));
}
if (settings.showGpuVram && gpu.vramTotalBytes() > 0) {
rows.add(row("VRAM:", gigabytes(gpu.vramUsedBytes()) + "G/" + gigabytes(gpu.vramTotalBytes()) + "G", usageColor(settings, gpu.vramUsedBytes() / gpu.vramTotalBytes() * 100)));
}
if (settings.showGpuCoreClock && Double.isFinite(gpu.coreClockMhz()) && gpu.coreClockMhz() > 0) {
rows.add(row("C.CLOCK:", zeroDecimal(gpu.coreClockMhz()) + "MHZ", NO_COLOR));
}
if (settings.showGpuMemoryClock && Double.isFinite(gpu.memoryClockMhz()) && gpu.memoryClockMhz() > 0) {
rows.add(row("M.CLOCK:", zeroDecimal(gpu.memoryClockMhz()) + "MHZ", NO_COLOR));
}
pushModule(modules, rows, false);
}
if (settings.showCpuModule) {
ArrayList<Row> rows = new ArrayList<>();
if (Double.isFinite(stats.cpuLoadTotalPercent())) {
rows.add(row("CPU:", oneDecimal(stats.cpuLoadTotalPercent()) + "%", usageColor(settings, stats.cpuLoadTotalPercent())));
}
if (settings.showCpuTemperature && Double.isFinite(stats.cpuTemperatureC()) && stats.cpuTemperatureC() > 1) {
rows.add(row("TEMP:", oneDecimal(stats.cpuTemperatureC()) + "C", temperatureColor(settings, stats.cpuTemperatureC())));
}
if (settings.showCpuClock && Double.isFinite(stats.cpuClockMhz()) && stats.cpuClockMhz() > 0) {
rows.add(row("CLOCK:", zeroDecimal(stats.cpuClockMhz()) + "MHZ", NO_COLOR));
}
double[] coreLoads = stats.cpuLoadCorePercents();
if (settings.showCpuCores && coreLoads.length > 0) {
for (int i = 0; i < coreLoads.length; i += 2) {
StringBuilder value = new StringBuilder();
double worst = 0;
for (int j = 0; j < 2 && i + j < coreLoads.length; j++) {
if (j > 0) value.append("   ");
double load = Double.isFinite(coreLoads[i + j]) ? coreLoads[i + j] : 0;
worst = Math.max(worst, load);
value.append("C").append(i + j).append(" ").append(String.format(Locale.ROOT, "%.0f", load)).append("%");
}
rows.add(row("CORES:", value.toString(), usageColor(settings, worst)));
}
}
pushModule(modules, rows, false);
}
if (settings.showRamModule) {
ArrayList<Row> rows = new ArrayList<>();
if (settings.showSystemRamLine && stats.systemMemoryTotalBytes() > 0) {
double usedPercent = (double) stats.systemMemoryUsedBytes() / stats.systemMemoryTotalBytes() * 100.0;
rows.add(row("RAM:", gigabytes(stats.systemMemoryUsedBytes()) + "G/" + gigabytes(stats.systemMemoryTotalBytes()) + "G", usageColor(settings, usedPercent)));
}
if (settings.showJvmLine && stats.jvmHeapCommittedBytes() > 0) {
rows.add(row("JVM:", gigabytes(stats.jvmHeapUsedBytes()) + "G/" + gigabytes(stats.jvmHeapCommittedBytes()) + "G", NO_COLOR));
}
pushModule(modules, rows, false);
}
if (settings.showGraphModule) {
modules.add(new ModuleContent(List.of(), true));
}
if (settings.showBenchmarkModule) {
ArrayList<Row> rows = new ArrayList<>();
if (benchmark.isRecording()) {
rows.add(row("REC:", String.format(Locale.ROOT, "%.1fS", benchmark.recordedSeconds()), settings.badColor & 0xFFFFFF));
}
BenchmarkSummary summary = benchmark.visibleSummary(System.nanoTime());
if (summary != null) {
for (String line : collectSummaryLines(summary)) {
rows.add(row("", line, NO_COLOR));
}
}
pushModule(modules, rows, false);
}
return modules;
}

private List<String> collectSummaryLines(BenchmarkSummary summary) {
ArrayList<String> lines = new ArrayList<>();
lines.add("BENCHMARK OVER");
lines.add("TIME " + oneDecimal(summary.durationSeconds()) + "S  FRAMES " + summary.frameCount());
lines.add("AVG " + oneDecimal(summary.averageFps()) + "  MAX " + oneDecimal(summary.maxFps()) + "  MIN " + oneDecimal(summary.minFps()));
lines.add("1% " + oneDecimal(summary.onePercentLowFps()) + "  0.1% " + oneDecimal(summary.oneTenthPercentLowFps()));
lines.add("FRAME AVG " + twoDecimals(summary.averageFrametimeMs()) + "MS");
return lines;
}

private void pushModule(List<ModuleContent> modules, ArrayList<Row> rows, boolean hasGraph) {
if (rows.isEmpty() && !hasGraph) return;
modules.add(new ModuleContent(List.copyOf(rows), hasGraph));
}

private int fpsColor(ConfigData settings, double value) {
return ValueColors.lowIsBad(value, settings.fpsBadThreshold, settings.fpsGoodThreshold, settings.badColor, settings.mediumColor, settings.goodColor);
}

private int usageColor(ConfigData settings, double value) {
return ValueColors.highIsBad(value, settings.usageMediumThreshold, settings.usageBadThreshold, settings.badColor, settings.mediumColor, settings.goodColor);
}

private int temperatureColor(ConfigData settings, double value) {
return ValueColors.highIsBad(value, settings.temperatureMediumThreshold, settings.temperatureBadThreshold, settings.badColor, settings.mediumColor, settings.goodColor);
}

private static Row row(String label, String value, int valueColor) {
return new Row(label, value, valueColor);
}

private static Component component(String text, Style style) {
return Component.literal(text).withStyle(style);
}

private static int measure(Font font, Style style, String text) {
return font.width(Component.literal(text).withStyle(style));
}

private static String truncate(String value) {
return value.length() > MAX_GPU_TEXT ? value.substring(0, MAX_GPU_TEXT) : value;
}

private static String oneDecimal(double value) {
return Double.isFinite(value) ? String.format(Locale.ROOT, "%.1f", value) : "--";
}

private static String twoDecimals(double value) {
return Double.isFinite(value) ? String.format(Locale.ROOT, "%.2f", value) : "--";
}

private static String zeroDecimal(double value) {
return Double.isFinite(value) ? String.format(Locale.ROOT, "%.0f", value) : "--";
}

private static String gigabytes(double bytes) {
return String.format(Locale.ROOT, "%.1f", bytes / 1073741824.0);
}
}
