package fr.teddz.benchburner.b2.ui;

import net.minecraft.client.gui.GuiGraphics;

final class Outline {
private Outline() {
}

static void draw(GuiGraphics graphics, int x, int y, int width, int height, int color) {
graphics.fill(x, y, x + width, y + 1, color);
graphics.fill(x, y + height - 1, x + width, y + height, color);
graphics.fill(x, y, x + 1, y + height, color);
graphics.fill(x + width - 1, y, x + width, y + height, color);
}
}
