package fr.teddz.benchburner.config;

public class ConfigData {
public boolean showFpsModule = true;
public boolean showFpsAverage = true;
public boolean showFpsOnePercentLow = true;
public boolean showFpsZeroPointOneLow = true;

public boolean showGpuModule = true;
public boolean showGpuName = true;
public boolean showGpuUsage = true;
public boolean showGpuTemperature = true;
public boolean showGpuPower = true;
public boolean showGpuFan = true;
public boolean showGpuVram = true;
public boolean showGpuCoreClock = true;
public boolean showGpuMemoryClock = true;

public boolean showCpuModule = true;
public boolean showCpuTemperature = true;
public boolean showCpuClock = true;
public boolean showCpuCores = false;

public boolean showRamModule = true;
public boolean showJvmLine = true;
public boolean showSystemRamLine = true;

public boolean showGraphModule = true;
public boolean showBenchmarkModule = true;

public int graphWidth = 110;
public int graphHeight = 24;

public boolean showBackground = true;
public boolean useCustomFont = true;
public int x = 8;
public int y = 8;
public double scale = 1.0;
public double opacity = 0.85;
public int textColor = 0xEAEAEA;
public int badColor = 0xFF4D4D;
public int mediumColor = 0xFFD70F;
public int goodColor = 0x4DD26E;
public double fpsBadThreshold = 30;
public double fpsGoodThreshold = 60;
public double usageBadThreshold = 90;
public double usageMediumThreshold = 60;
public double temperatureBadThreshold = 85;
public double temperatureMediumThreshold = 65;
public String preset = "standard";
public int autoRunSeconds = 60;
}
