package fr.teddz.benchburner.b1.ui;

import java.util.List;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

final class PresetMenu {
private static final int HEADER_HEIGHT = 20;
private static final int ITEM_HEIGHT = 14;
private static final int MAX_VISIBLE = 8;
private static final int TEXT = 0xFFE0E0E0;
private static final int ACCENT = 0xFFFFFF55;
private static final int OUTLINE = 0xFFAAAAAA;
private static final int HOVER = 0xFF202020;
private static final int PANEL_BG = 0xF0101010;

private final List<String> options;
private int selected;
private boolean open;
private int scroll;
private int x;
private int y;
private int width;

PresetMenu(List<String> options, String current) {
this.options = options.isEmpty() ? List.of("Standard") : List.copyOf(options);
String key = current == null ? "" : current;
int index = this.options.indexOf(key);
if (index >= 0) {
this.selected = index;
} else {
index = this.options.indexOf("Standard");
this.selected = Math.max(0, index);
}
}

void setBounds(int x, int y, int width) {
this.x = x;
this.y = y;
this.width = width;
}

String selected() {
return this.options.get(this.selected);
}

boolean isOpen() {
return this.open;
}

void close() {
this.open = false;
}

private int visibleCount() {
return Math.min(this.options.size(), MAX_VISIBLE);
}

boolean handleClick(double mouseX, double mouseY) {
if (mouseX >= this.x && mouseX < this.x + this.width && mouseY >= this.y && mouseY < this.y + HEADER_HEIGHT) {
this.open = !this.open;
this.scroll = 0;
return true;
}
if (!this.open) {
return false;
}
int panelTop = this.y + HEADER_HEIGHT;
int panelHeight = this.visibleCount() * ITEM_HEIGHT + 2;
if (mouseX < this.x || mouseX >= this.x + this.width || mouseY < panelTop || mouseY >= panelTop + panelHeight) {
this.open = false;
return true;
}
int row = (int) (mouseY - panelTop - 1) / ITEM_HEIGHT + this.scroll;
if (row >= 0 && row < this.options.size()) {
this.selected = row;
}
this.open = false;
return true;
}

boolean mouseScrolled(double mouseX, double mouseY, double amount) {
if (!this.open) {
return false;
}
if (mouseX < this.x || mouseX >= this.x + this.width || mouseY < this.y) {
return false;
}
int maxScroll = Math.max(0, this.options.size() - this.visibleCount());
int next = this.scroll + (amount < 0 ? 1 : -1);
if (next < 0) next = 0;
if (next > maxScroll) next = maxScroll;
this.scroll = next;
return true;
}

void render(GuiGraphics graphics, Font font, double mouseX, double mouseY) {
graphics.fill(this.x, this.y, this.x + this.width, this.y + HEADER_HEIGHT, 0xFF000000);
Outline.draw(graphics, this.x, this.y, this.width, HEADER_HEIGHT, OUTLINE);
graphics.drawString(font, "Preset: " + this.selected(), this.x + 5, this.y + 6, TEXT, false);
graphics.drawString(font, this.open ? "^" : "v", this.x + this.width - 13, this.y + 6, OUTLINE, false);
if (!this.open) {
return;
}
int top = this.y + HEADER_HEIGHT + 1;
int panelHeight = this.visibleCount() * ITEM_HEIGHT + 2;
graphics.fill(this.x, top, this.x + this.width, top + panelHeight, PANEL_BG);
Outline.draw(graphics, this.x, top, this.width, panelHeight, OUTLINE);
for (int i = 0; i < this.visibleCount(); i++) {
int index = this.scroll + i;
if (index >= this.options.size()) break;
int rowY = top + 1 + i * ITEM_HEIGHT;
boolean hovered = mouseX >= this.x && mouseX < this.x + this.width && mouseY >= rowY && mouseY < rowY + ITEM_HEIGHT;
if (hovered) {
graphics.fill(this.x + 1, rowY, this.x + this.width - 1, rowY + ITEM_HEIGHT, HOVER);
}
int color = index == this.selected ? ACCENT : TEXT;
graphics.drawString(font, this.options.get(index), this.x + 5, rowY + 2, color, false);
}
if (this.options.size() > this.visibleCount()) {
int trackX = this.x + this.width - 5;
int trackTop = top + 1;
int trackHeight = panelHeight - 2;
int thumbHeight = Math.max(8, trackHeight * this.visibleCount() / this.options.size());
int travel = trackHeight - thumbHeight;
int thumbY = this.scroll >= this.options.size() - this.visibleCount()
? trackTop + travel
: trackTop + travel * this.scroll / Math.max(1, this.options.size() - this.visibleCount());
graphics.fill(trackX, trackTop, trackX + 3, trackTop + trackHeight, 0xFF2A2A2A);
graphics.fill(trackX, thumbY, trackX + 3, thumbY + thumbHeight, 0xFFAAAAAA);
}
}
}
