package fr.teddz.benchburner;

import fr.teddz.benchburner.core.PlatformEntry;

public final class Buckets {
private Buckets() {
}

static PlatformEntry pick(String version) {
int[] parts = parse(version);
if (parts == null) return load(4);
if (parts[0] != 1) return load(4);
if (parts[1] > 21) return load(4);
if (parts[1] < 21) return load(1);
if (parts[2] >= 9) return load(3);
if (parts[2] >= 6) return load(2);
return load(1);
}

private static PlatformEntry load(int bucket) {
try {
return (PlatformEntry) Class.forName("fr.teddz.benchburner.b" + bucket + ".platform.Platform").getDeclaredConstructor().newInstance();
} catch (ReflectiveOperationException e) {
throw new IllegalStateException("benchburner bucket b" + bucket + " missing", e);
}
}

private static int[] parse(String version) {
try {
String[] split = version.split("\\.");
int major = Integer.parseInt(split[0]);
int minor = split.length > 1 ? Integer.parseInt(split[1]) : 0;
int patch = split.length > 2 ? Integer.parseInt(split[2]) : 0;
return new int[] {major, minor, patch};
} catch (NumberFormatException e) {
return null;
}
}
}
