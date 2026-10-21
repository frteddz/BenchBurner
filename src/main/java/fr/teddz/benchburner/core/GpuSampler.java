package fr.teddz.benchburner.core;

public final class GpuSampler {
private final NvidiaGpuReader nvidia;
private final AmdGpuReader amd;

private GpuSampler(NvidiaGpuReader nvidia, AmdGpuReader amd) {
this.nvidia = nvidia;
this.amd = amd;
}

public static GpuSampler create() {
NvidiaGpuReader nvidia = NvidiaGpuReader.create();
if (nvidia != null) return new GpuSampler(nvidia, null);
AmdGpuReader amd = AmdGpuReader.create();
if (amd != null) return new GpuSampler(null, amd);
return new GpuSampler(null, null);
}

public GpuSample sample() {
if (nvidia != null) return nvidia.sample();
if (amd != null) return amd.sample();
return GpuSample.UNAVAILABLE;
}
}
