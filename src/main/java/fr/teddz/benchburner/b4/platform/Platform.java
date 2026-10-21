package fr.teddz.benchburner.b4.platform;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Locale;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import com.mojang.blaze3d.platform.InputConstants;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.teddz.benchburner.b4.ui.OverlayConfigScreen;
import fr.teddz.benchburner.b4.ui.OverlayElement;
import fr.teddz.benchburner.config.ConfigStore;
import fr.teddz.benchburner.core.BenchmarkManager;
import fr.teddz.benchburner.core.BenchmarkResult;
import fr.teddz.benchburner.core.BenchmarkSummary;
import fr.teddz.benchburner.core.CsvExporter;
import fr.teddz.benchburner.core.Dependencies;
import fr.teddz.benchburner.core.PlatformEntry;

public final class Platform implements PlatformEntry {
private static final Logger LOGGER = LoggerFactory.getLogger("benchburner");

private ConfigStore configStore;

@Override
public void init(Dependencies dependencies) {
this.configStore = dependencies.configStore();
BenchmarkManager benchmark = dependencies.benchmark();
CsvExporter csvExporter = dependencies.csvExporter();
GpuProbe gpuProbe = new GpuProbe();
OverlayElement overlay = new OverlayElement(configStore.data(), dependencies.fpsCalculator(), dependencies.statsSampler(), dependencies.gpuWorker(), gpuProbe, benchmark);

HudElementRegistry.attachElementAfter(
VanillaHudElements.MISC_OVERLAYS,
Identifier.fromNamespaceAndPath("benchburner", "overlay"),
overlay);

KeyMapping.Category category = KeyMapping.Category.register(Identifier.fromNamespaceAndPath("benchburner", "main"));
WatchedKeyMapping benchmarkKey = (WatchedKeyMapping) KeyMappingHelper.registerKeyMapping(new WatchedKeyMapping(
"key.benchburner.toggle", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_R, category));
WatchedKeyMapping settingsKey = (WatchedKeyMapping) KeyMappingHelper.registerKeyMapping(new WatchedKeyMapping(
"key.benchburner.config", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_K, category));
WatchedKeyMapping exportKey = (WatchedKeyMapping) KeyMappingHelper.registerKeyMapping(new WatchedKeyMapping(
"key.benchburner.export", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_N, category));
WatchedKeyMapping worldKey = (WatchedKeyMapping) KeyMappingHelper.registerKeyMapping(new WatchedKeyMapping(
"key.benchburner.world", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_B, category));
WatchedKeyMapping overlayKey = (WatchedKeyMapping) KeyMappingHelper.registerKeyMapping(new WatchedKeyMapping(
"key.benchburner.overlay", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_O, category));

BenchWorld benchWorld = new BenchWorld(result -> writeCsv(csvExporter, result));
benchWorld.ensureExtracted();
ClientTickEvents.END_CLIENT_TICK.register(client -> {
while (benchmarkKey.consumeClick()) {
toggleBenchmark(benchmark, csvExporter);
}
while (exportKey.consumeClick()) {
exportLastBenchmark(benchmark, csvExporter);
}
boolean worldDown = isKeyDown(client, worldKey.watchedKey());
if (worldDown && !this.worldKeyDown && allowsMenuKeys(client.screen)) {
benchWorld.requestStart(client);
}
this.worldKeyDown = worldDown;
boolean settingsDown = isKeyDown(client, settingsKey.watchedKey());
if (settingsDown && !this.settingsKeyDown && allowsMenuKeys(client.screen)) {
openSettings(client, client.screen);
}
this.settingsKeyDown = settingsDown;
boolean overlayDown = isKeyDown(client, overlayKey.watchedKey());
if (overlayDown && !this.overlayKeyDown) {
overlay.toggle();
message(overlay.isVisible() ? "Overlay shown" : "Overlay hidden");
}
this.overlayKeyDown = overlayDown;
benchWorld.tick(client, configStore.data(), benchmark);
});

ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
if (!(screen instanceof TitleScreen) && !(screen instanceof PauseScreen)) return;
Button menuButton = Button.builder(Component.literal("BenchBurner"), button -> openSettings(client, screen))
.bounds(scaledWidth - 106, 10, 98, 20)
.build();
Screens.getWidgets(screen).add(menuButton);
});
}

private boolean worldKeyDown;
private boolean settingsKeyDown;
private boolean overlayKeyDown;

private static boolean allowsMenuKeys(Screen screen) {
return screen == null || screen instanceof TitleScreen || screen instanceof PauseScreen;
}

private static boolean isKeyDown(Minecraft client, InputConstants.Key key) {
long window = client.getWindow().handle();
return key.getValue() >= 0 && GLFW.glfwGetKey(window, key.getValue()) == GLFW.GLFW_PRESS;
}

private static final class WatchedKeyMapping extends KeyMapping {
WatchedKeyMapping(String name, InputConstants.Type type, int code, KeyMapping.Category category) {
super(name, type, code, category);
}

InputConstants.Key watchedKey() {
return this.key;
}
}

private void openSettings(Minecraft client, Screen returnScreen) {
client.setScreen(new OverlayConfigScreen(configStore, returnScreen));
}

private static void toggleBenchmark(BenchmarkManager benchmark, CsvExporter csvExporter) {
if (!benchmark.isRecording()) {
benchmark.start();
message("Benchmark recording started");
return;
}
BenchmarkResult result = benchmark.stop();
if (result == null) {
message("Benchmark stopped, but it did not catch a single frame. Skill issue");
return;
}
writeCsv(csvExporter, result);
}

private static void exportLastBenchmark(BenchmarkManager benchmark, CsvExporter csvExporter) {
BenchmarkResult result = benchmark.lastResult();
if (result == null) {
message("Nothing recorded yet");
return;
}
writeCsv(csvExporter, result);
}

private static void writeCsv(CsvExporter csvExporter, BenchmarkResult result) {
Path gameDirectory = net.fabricmc.loader.api.FabricLoader.getInstance().getGameDir();
try {
Path csv = csvExporter.export(gameDirectory, result.frameTimesMs());
message(summaryMessage(result.summary()));
message("Exported to " + csv.getFileName());
} catch (IOException e) {
LOGGER.error("benchburner export failed", e);
message("Export failed, the log knows why");
}
}

private static String summaryMessage(BenchmarkSummary summary) {
return "Benchmark over: avg " + String.format(Locale.ROOT, "%.1f", summary.averageFps())
+ "  min " + String.format(Locale.ROOT, "%.1f", summary.minFps())
+ "  max " + String.format(Locale.ROOT, "%.1f", summary.maxFps())
+ "  1% " + String.format(Locale.ROOT, "%.1f", summary.onePercentLowFps())
+ "  0.1% " + String.format(Locale.ROOT, "%.1f", summary.oneTenthPercentLowFps())
+ "  frame " + String.format(Locale.ROOT, "%.2f", summary.averageFrametimeMs()) + "ms";
}

static void message(String text) {
Minecraft.getInstance().gui.getChat().addClientSystemMessage(Component.literal(text));
}
}
