package fr.teddz.benchburner.core;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;

import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.Sensors;

public final class SystemStatsReader {
private final CentralProcessor processor;
private final GlobalMemory memory;
private final Sensors sensors;
private long[] systemTicks = new long[0];
private long[][] processorTicks = new long[0][];

public SystemStatsReader() {
CentralProcessor cpu = null;
GlobalMemory mem = null;
Sensors chip = null;
try {
HardwareAbstractionLayer hardware = new SystemInfo().getHardware();
cpu = hardware.getProcessor();
mem = hardware.getMemory();
chip = hardware.getSensors();
} catch (Throwable ignored) {
}
processor = cpu;
memory = mem;
sensors = chip;
if (processor != null) {
try {
systemTicks = processor.getSystemCpuLoadTicks();
processorTicks = processor.getProcessorCpuLoadTicks();
} catch (Throwable ignored) {
}
}
}

public SystemStats sample() {
double cpuTotal = Double.NaN;
double[] cpuCores = new double[0];
if (processor != null) {
try {
cpuTotal = processor.getSystemCpuLoadBetweenTicks(systemTicks) * 100.0;
} catch (Throwable ignored) {
}
try {
double[] loads = processor.getProcessorCpuLoadBetweenTicks(processorTicks);
cpuCores = new double[loads.length];
for (int i = 0; i < loads.length; i++) cpuCores[i] = loads[i] * 100.0;
} catch (Throwable ignored) {
}
try {
systemTicks = processor.getSystemCpuLoadTicks();
} catch (Throwable ignored) {
}
try {
processorTicks = processor.getProcessorCpuLoadTicks();
} catch (Throwable ignored) {
}
}
double temperature = Double.NaN;
if (sensors != null) {
try {
double read = sensors.getCpuTemperature();
if (Double.isFinite(read) && read > 0) temperature = read;
} catch (Throwable ignored) {
}
}
double clockMhz = Double.NaN;
if (processor != null) {
try {
long[] frequencies = processor.getCurrentFreq();
double total = 0;
int counted = 0;
for (long frequency : frequencies) {
if (frequency > 0) {
total += frequency;
counted++;
}
}
if (counted > 0) clockMhz = total / counted / 1000000.0;
} catch (Throwable ignored) {
}
}
long systemUsed = 0;
long systemTotal = 0;
if (memory != null) {
try {
systemTotal = memory.getTotal();
systemUsed = systemTotal - memory.getAvailable();
} catch (Throwable ignored) {
}
}
long jvmUsed = 0;
long jvmCommitted = 0;
try {
MemoryUsage heap = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
jvmUsed = heap.getUsed();
jvmCommitted = heap.getCommitted();
} catch (Throwable ignored) {
}
return new SystemStats(cpuTotal, cpuCores, temperature, clockMhz, jvmUsed, jvmCommitted, systemUsed, systemTotal);
}
}
