package fr.teddz.benchburner.config;

import java.util.Locale;

public final class Presets {
public static final String[] NAMES = {"Minimal", "Standard", "Full"};

private Presets() {
}

public static void apply(String name, ConfigData data) {
String key = name.toLowerCase(Locale.ROOT);
switch (key) {
case "minimal" -> {
resetRows(data);
data.showFpsModule = true;
data.showFpsAverage = false;
data.showFpsOnePercentLow = false;
data.showFpsZeroPointOneLow = false;
data.showGraphModule = true;
data.showBenchmarkModule = true;
data.showBackground = true;
data.useCustomFont = true;
data.preset = "minimal";
}
case "standard" -> {
resetRows(data);
data.showFpsModule = true;
data.showFpsAverage = true;
data.showFpsOnePercentLow = true;
data.showFpsZeroPointOneLow = true;
data.showGpuModule = true;
data.showGpuName = true;
data.showGpuUsage = true;
data.showGpuTemperature = true;
data.showGpuPower = true;
data.showGpuFan = true;
data.showGpuVram = true;
data.showGpuCoreClock = true;
data.showGpuMemoryClock = true;
data.showCpuModule = true;
data.showCpuTemperature = true;
data.showCpuClock = true;
data.showCpuCores = false;
data.showRamModule = true;
data.showJvmLine = true;
data.showSystemRamLine = true;
data.showGraphModule = true;
data.showBenchmarkModule = true;
data.showBackground = true;
data.useCustomFont = true;
data.preset = "standard";
}
case "full" -> {
resetRows(data);
setRows(data, true);
data.showBackground = true;
data.useCustomFont = true;
data.preset = "full";
}
default -> {
}
}
}

private static void resetRows(ConfigData data) {
setRows(data, false);
}

private static void setRows(ConfigData data, boolean value) {
data.showFpsModule = value;
data.showFpsAverage = value;
data.showFpsOnePercentLow = value;
data.showFpsZeroPointOneLow = value;
data.showGpuModule = value;
data.showGpuName = value;
data.showGpuUsage = value;
data.showGpuTemperature = value;
data.showGpuPower = value;
data.showGpuFan = value;
data.showGpuVram = value;
data.showGpuCoreClock = value;
data.showGpuMemoryClock = value;
data.showCpuModule = value;
data.showCpuTemperature = value;
data.showCpuClock = value;
data.showCpuCores = value;
data.showRamModule = value;
data.showJvmLine = value;
data.showSystemRamLine = value;
data.showGraphModule = value;
data.showBenchmarkModule = value;
}

public static boolean isAnyRowVisible(ConfigData data) {
return data.showFpsModule || data.showGpuModule || data.showCpuModule || data.showRamModule
|| data.showGraphModule || data.showBenchmarkModule;
}

public static PresetData snapshot(ConfigData data) {
return new PresetData(
data.showFpsModule,
data.showFpsAverage,
data.showFpsOnePercentLow,
data.showFpsZeroPointOneLow,
data.showGpuModule,
data.showGpuName,
data.showGpuUsage,
data.showGpuTemperature,
data.showGpuPower,
data.showGpuFan,
data.showGpuVram,
data.showGpuCoreClock,
data.showGpuMemoryClock,
data.showCpuModule,
data.showCpuTemperature,
data.showCpuClock,
data.showCpuCores,
data.showRamModule,
data.showJvmLine,
data.showSystemRamLine,
data.showGraphModule,
data.showBenchmarkModule,
data.graphWidth,
data.graphHeight,
data.showBackground,
data.useCustomFont,
data.scale,
data.opacity,
data.textColor,
data.badColor,
data.mediumColor,
data.goodColor,
data.fpsBadThreshold,
data.fpsGoodThreshold,
data.usageBadThreshold,
data.usageMediumThreshold,
data.temperatureBadThreshold,
data.temperatureMediumThreshold);
}

public static void applyData(PresetData preset, ConfigData data) {
data.showFpsModule = preset.showFpsModule();
data.showFpsAverage = preset.showFpsAverage();
data.showFpsOnePercentLow = preset.showFpsOnePercentLow();
data.showFpsZeroPointOneLow = preset.showFpsZeroPointOneLow();
data.showGpuModule = preset.showGpuModule();
data.showGpuName = preset.showGpuName();
data.showGpuUsage = preset.showGpuUsage();
data.showGpuTemperature = preset.showGpuTemperature();
data.showGpuPower = preset.showGpuPower();
data.showGpuFan = preset.showGpuFan();
data.showGpuVram = preset.showGpuVram();
data.showGpuCoreClock = preset.showGpuCoreClock();
data.showGpuMemoryClock = preset.showGpuMemoryClock();
data.showCpuModule = preset.showCpuModule();
data.showCpuTemperature = preset.showCpuTemperature();
data.showCpuClock = preset.showCpuClock();
data.showCpuCores = preset.showCpuCores();
data.showRamModule = preset.showRamModule();
data.showJvmLine = preset.showJvmLine();
data.showSystemRamLine = preset.showSystemRamLine();
data.showGraphModule = preset.showGraphModule();
data.showBenchmarkModule = preset.showBenchmarkModule();
data.graphWidth = preset.graphWidth();
data.graphHeight = preset.graphHeight();
data.showBackground = preset.showBackground();
data.useCustomFont = preset.useCustomFont();
data.scale = preset.scale();
data.opacity = preset.opacity();
data.textColor = preset.textColor();
data.badColor = preset.badColor();
data.mediumColor = preset.mediumColor();
data.goodColor = preset.goodColor();
data.fpsBadThreshold = preset.fpsBadThreshold();
data.fpsGoodThreshold = preset.fpsGoodThreshold();
data.usageBadThreshold = preset.usageBadThreshold();
data.usageMediumThreshold = preset.usageMediumThreshold();
data.temperatureBadThreshold = preset.temperatureBadThreshold();
data.temperatureMediumThreshold = preset.temperatureMediumThreshold();
}
}
