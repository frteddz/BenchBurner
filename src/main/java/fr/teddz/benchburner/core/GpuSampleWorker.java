package fr.teddz.benchburner.core;

public final class GpuSampleWorker implements Runnable {
private static final long SAMPLE_INTERVAL_MS = 1000;

private volatile GpuSample latest = GpuSample.UNAVAILABLE;
private volatile boolean running = true;

public GpuSample latest() {
return latest;
}

public void stopSampling() {
running = false;
}

@Override
public void run() {
GpuSampler sampler = GpuSampler.create();
while (running) {
if (sampler != null) {
latest = sampler.sample();
}
try {
Thread.sleep(SAMPLE_INTERVAL_MS);
} catch (InterruptedException e) {
Thread.currentThread().interrupt();
return;
}
}
}
}
