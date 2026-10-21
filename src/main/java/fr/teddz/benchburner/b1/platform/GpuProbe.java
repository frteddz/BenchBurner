package fr.teddz.benchburner.b1.platform;

import org.lwjgl.opengl.GL11;

public final class GpuProbe {
private String renderer;

public String renderer() {
if (renderer == null) {
try {
renderer = GL11.glGetString(GL11.GL_RENDERER);
} catch (Throwable ignored) {
renderer = "";
}
if (renderer == null) renderer = "";
}
return renderer;
}
}
