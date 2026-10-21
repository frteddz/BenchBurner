package fr.teddz.benchburner.b2.platform;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import net.fabricmc.loader.api.FabricLoader;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.teddz.benchburner.config.ConfigData;
import fr.teddz.benchburner.core.BenchmarkManager;
import fr.teddz.benchburner.core.BenchmarkResult;

public final class BenchWorld {
private static final Logger LOGGER = LoggerFactory.getLogger("benchburner");
private static final String WORLD_NAME = "BenchBurner";
private static final int WARMUP_TICKS = 100;

enum State {
IDLE,
PENDING_OPEN,
LOADING,
WARMUP,
RECORDING
}

interface Finisher {
void finish(BenchmarkResult result);
}

private final Finisher finisher;
private State state = State.IDLE;
private int ticks;

BenchWorld(Finisher finisher) {
this.finisher = finisher;
}

void requestStart(Minecraft client) {
if (this.state != State.IDLE) {
Platform.message("A benchmark world run is already in progress");
return;
}
ensureExtracted();
if (client.level == null) {
open(client);
return;
}
client.disconnectWithSavingScreen();
this.state = State.PENDING_OPEN;
}

void tick(Minecraft client, ConfigData data, BenchmarkManager benchmark) {
if (this.state == State.IDLE) return;
if (client.level == null) {
if (this.state == State.PENDING_OPEN) {
if (client.screen instanceof TitleScreen) open(client);
return;
}
if (this.state == State.LOADING || this.state == State.WARMUP || this.state == State.RECORDING) {
if (benchmark.isRecording()) benchmark.stop();
this.state = State.IDLE;
Platform.message("Auto run cancelled");
}
return;
}
switch (this.state) {
case LOADING -> {
if (client.getSingleplayerServer() != null) {
prep(client.getSingleplayerServer());
this.ticks = 0;
this.state = State.WARMUP;
Platform.message("Benchmark world ready, warmup 5 seconds");
}
}
case WARMUP -> {
this.ticks++;
if (this.ticks >= WARMUP_TICKS) {
benchmark.start();
this.ticks = 0;
this.state = State.RECORDING;
Platform.message("Recording for " + data.autoRunSeconds + " seconds");
}
}
case RECORDING -> {
this.ticks++;
if (this.ticks >= data.autoRunSeconds * 20) {
BenchmarkResult result = benchmark.stop();
this.state = State.IDLE;
finisher.finish(result);
}
}
default -> {
}
}
}

private void open(Minecraft client) {
Platform.message("Opening the benchmark world");
client.createWorldOpenFlows().openWorld(WORLD_NAME, () -> {});
this.state = State.LOADING;
}

private void prep(MinecraftServer server) {
CommandSourceStack stack = server.createCommandSourceStack();
run(server, stack, "time set noon");
run(server, stack, "gamerule doDaylightCycle false");
run(server, stack, "gamerule doWeatherCycle false");
run(server, stack, "gamerule doMobSpawning false");
run(server, stack, "gamerule doInsomnia false");
run(server, stack, "gamerule doPatrolSpawning false");
run(server, stack, "gamerule doFireTick false");
BlockPos spawn = server.overworld().getSharedSpawnPos();
run(server, stack, String.format(Locale.ROOT, "tp @p %.1f %.1f %.1f", spawn.getX() + 0.5, spawn.getY(), spawn.getZ() + 0.5));
}

private void run(MinecraftServer server, CommandSourceStack stack, String command) {
try {
server.getCommands().performPrefixedCommand(stack, command);
} catch (RuntimeException e) {
LOGGER.error("benchburner world prep failed for {}", command, e);
}
}

void ensureExtracted() {
Path saves = FabricLoader.getInstance().getGameDir().resolve("saves");
Path target = saves.resolve(WORLD_NAME);
if (Files.exists(target.resolve("level.dat"))) return;
try (InputStream zip = BenchWorld.class.getResourceAsStream("/assets/benchburner/world/benchburner-world.zip")) {
if (zip == null) {
LOGGER.error("benchburner world resource missing");
return;
}
try (ZipInputStream entries = new ZipInputStream(zip)) {
ZipEntry entry;
while ((entry = entries.getNextEntry()) != null) {
String name = entry.getName();
if (name.endsWith("/")) continue;
if (name.startsWith(WORLD_NAME + "/")) name = name.substring(WORLD_NAME.length() + 1);
if (name.isEmpty() || name.contains("..")) continue;
Path out = target.resolve(name);
Files.createDirectories(out.getParent());
Files.copy(entries, out, StandardCopyOption.REPLACE_EXISTING);
}
}
LOGGER.info("benchburner world added to saves");
} catch (IOException e) {
LOGGER.error("benchburner world extract failed", e);
}
}
}
