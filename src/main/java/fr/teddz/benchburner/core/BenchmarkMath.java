package fr.teddz.benchburner.core;

import java.util.Arrays;

public final class BenchmarkMath {
private BenchmarkMath() {
}

public static double average(double[] values) {
return averageIn(values, values.length);
}

public static double averageIn(double[] values, int length) {
if (length <= 0 || length > values.length) return Double.NaN;
double total = 0;
for (int i = 0; i < length; i++) total += values[i];
return total / length;
}

public static double min(double[] values) {
if (values.length == 0) return Double.NaN;
double smallest = values[0];
for (int i = 1; i < values.length; i++) smallest = Math.min(smallest, values[i]);
return smallest;
}

public static double max(double[] values) {
if (values.length == 0) return Double.NaN;
double largest = values[0];
for (int i = 1; i < values.length; i++) largest = Math.max(largest, values[i]);
return largest;
}

public static double worstAverageFrametime(double[] values, double fraction) {
return worstAverageFrametimeIn(values, values.length, fraction);
}

public static double worstAverageFrametimeIn(double[] values, int length, double fraction) {
if (length <= 0 || length > values.length) return Double.NaN;
double[] copy = Arrays.copyOf(values, length);
Arrays.sort(copy);
int worstCount = (int) Math.ceil(copy.length * fraction);
if (worstCount < 1) worstCount = 1;
if (worstCount > copy.length) worstCount = copy.length;
double total = 0;
for (int i = copy.length - worstCount; i < copy.length; i++) total += copy[i];
return total / worstCount;
}
}
