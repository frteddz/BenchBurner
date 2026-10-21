package fr.teddz.benchburner.config;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public final class PresetStore {
private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
private static final Type FILE_TYPE = new TypeToken<Map<String, PresetData>>() {
}.getType();

private final Path file;
private final Map<String, PresetData> presets;

public PresetStore(Path configDirectory) {
file = configDirectory.resolve("benchburner-presets.json");
presets = new LinkedHashMap<>(read());
}

public Map<String, PresetData> all() {
return Collections.unmodifiableMap(new LinkedHashMap<>(presets));
}

public PresetData get(String rawName) {
String key = normalize(rawName);
for (Map.Entry<String, PresetData> entry : presets.entrySet()) {
if (entry.getKey().equalsIgnoreCase(key)) return entry.getValue();
}
return null;
}

public boolean save(String rawName, PresetData preset) {
String name = normalize(rawName);
if (name.isEmpty()) return false;
for (String reserved : Presets.NAMES) {
if (name.equalsIgnoreCase(reserved)) return false;
}
String storedKey = name;
for (String existing : presets.keySet()) {
if (existing.equalsIgnoreCase(name)) {
storedKey = existing;
break;
}
}
presets.put(storedKey, preset);
return write();
}

public static String normalize(String rawName) {
return rawName == null ? "" : rawName.trim();
}

public static String toJson(PresetData preset) {
return GSON.toJson(preset);
}

public static PresetData fromJson(String json) {
try {
return GSON.fromJson(json, PresetData.class);
} catch (RuntimeException e) {
return null;
}
}

private Map<String, PresetData> read() {
if (!Files.exists(file)) return new LinkedHashMap<>();
try {
Map<String, PresetData> loaded = GSON.fromJson(Files.readString(file), FILE_TYPE);
return loaded != null ? loaded : new LinkedHashMap<>();
} catch (IOException | RuntimeException e) {
System.err.println("benchburner could not read custom presets: " + e);
return new LinkedHashMap<>();
}
}

private boolean write() {
try {
Files.createDirectories(file.getParent());
Files.writeString(file, GSON.toJson(presets));
return true;
} catch (IOException e) {
System.err.println("benchburner could not save custom presets: " + e);
return false;
}
}
}
