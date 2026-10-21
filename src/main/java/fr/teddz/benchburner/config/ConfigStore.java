package fr.teddz.benchburner.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public final class ConfigStore {
private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

private final Path file;
private final ConfigData data;

public ConfigStore(Path configDirectory) {
file = configDirectory.resolve("benchburner.json");
data = load();
}

public ConfigData data() {
return data;
}

public void save() {
try {
Files.createDirectories(file.getParent());
Files.writeString(file, GSON.toJson(data));
} catch (IOException e) {
System.err.println("benchburner could not save config: " + e);
}
}

private ConfigData load() {
if (!Files.exists(file)) return new ConfigData();
try {
ConfigData loaded = GSON.fromJson(Files.readString(file), ConfigData.class);
return loaded != null ? loaded : new ConfigData();
} catch (IOException | RuntimeException e) {
System.err.println("benchburner could not load config: " + e);
return new ConfigData();
}
}
}
