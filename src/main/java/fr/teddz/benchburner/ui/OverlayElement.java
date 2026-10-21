package fr.teddz.benchburner.ui;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;

import fr.teddz.benchburner.config.ConfigData;
import fr.teddz.benchburner.core.BenchmarkManager;
import fr.teddz.benchburner.core.FpsWindowCalculator;
import fr.teddz.benchburner.core.GpuSampleWorker;
import fr.teddz.benchburner.core.SystemStats;
import fr.teddz.benchburner.core.SystemStatsSampler;
import fr.teddz.benchburner.platform.GpuProbe;

public final class OverlayElement implements HudElement {
private static final double STALE_FRAME_LIMIT_MS = 1000.0;

private final ConfigData settings;
private final FpsWindowCalculator fpsCalculator;
private final SystemStatsSampler statsSampler;
private final GpuSampleWorker gpuWorker;
private final GpuProbe gpuProbe;
private final BenchmarkManager benchmark;
private final OverlayRenderer renderer = new OverlayRenderer();
private long lastRenderNanos = -1;
private boolean visible = true;

public OverlayElement(ConfigData settings, FpsWindowCalculator fpsCalculator, SystemStatsSampler statsSampler, GpuSampleWorker gpuWorker, GpuProbe gpuProbe, BenchmarkManager benchmark) {
this.settings = settings;
this.fpsCalculator = fpsCalculator;
this.statsSampler = statsSampler;
this.gpuWorker = gpuWorker;
this.gpuProbe = gpuProbe;
this.benchmark = benchmark;
}

@Override
public void render(GuiGraphics graphics, DeltaTracker tickCounter) {
long now = System.nanoTime();
if (lastRenderNanos >= 0) {
double frameTimeMs = (now - lastRenderNanos) / 1_000_000.0;
if (frameTimeMs > 0 && frameTimeMs < STALE_FRAME_LIMIT_MS) {
fpsCalculator.addFrameTime(frameTimeMs);
benchmark.recordFrame(frameTimeMs);
}
}
lastRenderNanos = now;
if (!this.visible) return;
SystemStats stats = statsSampler.latest();
renderer.draw(graphics, settings, fpsCalculator, stats, gpuWorker.latest(), gpuProbe.renderer(), benchmark);
}

public void toggle() {
this.visible = !this.visible;
}

public boolean isVisible() {
return this.visible;
}
}
