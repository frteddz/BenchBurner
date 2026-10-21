package fr.teddz.benchburner.core;

public final class SystemStatsSampler implements Runnable {
private static final long SAMPLE_INTERVAL_MS = 500;
private static final SystemStats EMPTY = new SystemStats(Double.NaN, new double[0], Double.NaN, Double.NaN, 0, 0, 0, 0);

private final SystemStatsReader reader = new SystemStatsReader();
private volatile SystemStats latest = EMPTY;
private volatile boolean running = true;

public SystemStats latest() {
return latest;
}

public void stopSampling() {
running = false;
}

@Override
public void run() {
while (running) {
try {
latest = reader.sample();
} catch (Throwable ignored) {
latest = EMPTY;
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
