package fr.teddz.benchburner.core;

public final class FrameRingBuffer {
private final double[] values;
private int head;
private int count;

public FrameRingBuffer(int capacity) {
values = new double[Math.max(1, capacity)];
}

public int count() {
return count;
}

public int capacity() {
return values.length;
}

public void add(double frameTimeMs) {
if (!Double.isFinite(frameTimeMs) || frameTimeMs <= 0) return;
values[head] = frameTimeMs;
head++;
if (head == values.length) head = 0;
if (count < values.length) count++;
}

public void copyInto(double[] out) {
int length = values.length;
int start = (head - count + length) % length;
for (int i = 0; i < count; i++) out[i] = values[(start + i) % length];
}

public int copyLatest(double[] out) {
int length = values.length;
int take = Math.min(count, out.length);
if (take == 0) return 0;
int start = (head - take + length) % length;
for (int i = 0; i < take; i++) out[i] = values[(start + i) % length];
return take;
}
}
