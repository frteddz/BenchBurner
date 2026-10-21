package fr.teddz.benchburner.core;

import java.util.Arrays;

public final class GrowableDoubleList {
private double[] values;
private int size;

public GrowableDoubleList(int initialCapacity) {
values = new double[Math.max(16, initialCapacity)];
}

public void add(double valueMs) {
if (!Double.isFinite(valueMs) || valueMs <= 0) return;
if (size == values.length) values = Arrays.copyOf(values, size * 2);
values[size++] = valueMs;
}

public int size() {
return size;
}

public double totalMs() {
double total = 0;
for (int i = 0; i < size; i++) total += values[i];
return total;
}

public double[] toArray() {
return Arrays.copyOf(values, size);
}
}
