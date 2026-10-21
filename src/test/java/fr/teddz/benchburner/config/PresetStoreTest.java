package fr.teddz.benchburner.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class PresetStoreTest {
@Test
void NamesKeepUserCasingAndRejectReserved(@TempDir Path dir) {
PresetStore store = new PresetStore(dir);
String json = PresetStore.toJson(Presets.snapshot(new ConfigData()));
PresetData preset = PresetStore.fromJson(json);

assertNotNull(preset);
assertTrue(store.save(" my preset ", preset));
assertEquals("my preset", store.all().keySet().iterator().next());
assertTrue(store.save("My Preset", preset));
assertEquals(1, store.all().size());
assertFalse(store.save("", preset));
assertFalse(store.save("full", preset));
assertFalse(store.save("Standard", preset));
}

@Test
void FileRoundTripsAcrossInstances(@TempDir Path dir) {
PresetStore first = new PresetStore(dir);
PresetData shared = Presets.snapshot(new ConfigData());
assertTrue(first.save("COOL PRESET", shared));

PresetStore second = new PresetStore(dir);
assertEquals(1, second.all().size());
assertEquals(shared, second.get("cool preset"));
}
}
