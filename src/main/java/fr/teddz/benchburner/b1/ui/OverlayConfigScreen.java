package fr.teddz.benchburner.b1.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;

import org.lwjgl.glfw.GLFW;

import fr.teddz.benchburner.config.ConfigData;
import fr.teddz.benchburner.config.ConfigStore;
import fr.teddz.benchburner.config.PresetData;
import fr.teddz.benchburner.config.PresetStore;
import fr.teddz.benchburner.config.Presets;

public final class OverlayConfigScreen extends Screen {
private static final String[] COLOR_NAMES = {"White", "Yellow", "Orange", "Red", "Green", "Cyan", "Pink"};
private static final int[] COLOR_VALUES = {0xF2F2F2, 0xFFD70F, 0xFF7A29, 0xFF4D4D, 0x4DD26E, 0x3DD6C4, 0xE06EC7};

private static final int TEXT = 0xFFE0E0E0;
private static final int ACCENT = 0xFFFFFF55;
private static final int DIM = 0xFF7F7F7F;
private static final int OFF = 0xFF909090;
private static final int OUTLINE = 0xFF555555;
private static final int OUTLINE_HOVER = 0xFFAAAAAA;
private static final int ROW_BG = 0xFF101010;
private static final int ROW_HOVER = 0xFF1C1C1C;
private static final int SWATCH_BG = 0xFF000000;

private static final int ROW_HEIGHT = 22;

private final ConfigStore store;
private final ConfigData data;
private final Screen parent;
private final PresetStore presetStore;
private final LinkedHashMap<String, Boolean> sectionOpen = new LinkedHashMap<>();
private PresetMenu presetMenu;
private SettingsList settingsList;
private EditBox presetNameBox;
private String presetNameText = "";
private String notice = "";
private int listTop;
private int listBottom;

public OverlayConfigScreen(ConfigStore configStore, Screen returnScreen) {
super(Component.literal("BenchBurner settings"));
this.store = configStore;
this.data = configStore.data();
this.parent = returnScreen;
this.presetStore = new PresetStore(FabricLoader.getInstance().getConfigDir());
for (String key : List.of("fps", "gpu", "cpu", "ram", "graph", "bench", "look", "colors", "share")) {
this.sectionOpen.put(key, true);
}
}

private record ToggleRowSpec(String name, BooleanSupplier getter, BiConsumer<ConfigData, Boolean> setter) {
}

private record StepperRowSpec(String name, DoubleSupplier getter, DoubleConsumer setter, double step, double min, double max, String format) {
}

private record ColorRowSpec(String name, IntSupplier getter, BiConsumer<ConfigData, Integer> setter) {
}

private record ActionRowSpec(String label, Runnable action) {
}

@Override
protected void init() {
this.presetMenu = new PresetMenu(this.collectPresetOptions(), PresetStore.normalize(this.data.preset));
this.presetMenu.setBounds(10, 30, 170);

this.presetNameBox = new EditBox(this.font, 188, 30, 150, 20, Component.literal("Preset name"));
this.presetNameBox.setMaxLength(24);
this.presetNameBox.setHint(Component.literal("Preset name"));
this.presetNameBox.setValue(this.presetNameText);
this.presetNameBox.setResponder(value -> this.presetNameText = value);
this.addRenderableWidget(this.presetNameBox);
this.addRenderableWidget(Button.builder(Component.literal("Save"), pressed -> this.savePreset())
.bounds(342, 30, 56, 20)
.build());

int doneY = this.height - 28;
int top = 56;
int listHeight = Math.max(40, doneY - 4 - top);
this.listTop = top;
this.listBottom = top + listHeight;
this.settingsList = new SettingsList(this.minecraft, this.width - 20, listHeight, top, top + listHeight, ROW_HEIGHT);
this.addRenderableWidget(this.settingsList);
this.settingsList.publishRows(buildRows());

this.addRenderableWidget(Button.builder(Component.literal("Done"), pressed -> this.onClose())
.bounds(this.width / 2 - 100, doneY, 200, 20)
.build());
}

private List<String> collectPresetOptions() {
ArrayList<String> options = new ArrayList<>(List.of(Presets.NAMES));
options.addAll(this.presetStore.all().keySet());
return options;
}

private List<SettingsList.Row> buildRows() {
ArrayList<SettingsList.Row> rows = new ArrayList<>();

rows.add(sectionRow("FPS module", "fps"));
if (this.isOpen("fps")) {
rows.add(toggleRow(new ToggleRowSpec("FPS", () -> data.showFpsModule, (d, v) -> d.showFpsModule = v)));
rows.add(toggleRow(new ToggleRowSpec("Avg FPS", () -> data.showFpsAverage, (d, v) -> d.showFpsAverage = v)));
rows.add(toggleRow(new ToggleRowSpec("1% LOW", () -> data.showFpsOnePercentLow, (d, v) -> d.showFpsOnePercentLow = v)));
rows.add(toggleRow(new ToggleRowSpec("0.1% LOW", () -> data.showFpsZeroPointOneLow, (d, v) -> d.showFpsZeroPointOneLow = v)));
}

rows.add(sectionRow("GPU module", "gpu"));
if (this.isOpen("gpu")) {
rows.add(toggleRow(new ToggleRowSpec("GPU", () -> data.showGpuModule, (d, v) -> d.showGpuModule = v)));
rows.add(toggleRow(new ToggleRowSpec("Show name", () -> data.showGpuName, (d, v) -> d.showGpuName = v)));
rows.add(toggleRow(new ToggleRowSpec("Usage", () -> data.showGpuUsage, (d, v) -> d.showGpuUsage = v)));
rows.add(toggleRow(new ToggleRowSpec("Temp", () -> data.showGpuTemperature, (d, v) -> d.showGpuTemperature = v)));
rows.add(toggleRow(new ToggleRowSpec("Power", () -> data.showGpuPower, (d, v) -> d.showGpuPower = v)));
rows.add(toggleRow(new ToggleRowSpec("Fan", () -> data.showGpuFan, (d, v) -> d.showGpuFan = v)));
rows.add(toggleRow(new ToggleRowSpec("VRAM", () -> data.showGpuVram, (d, v) -> d.showGpuVram = v)));
rows.add(toggleRow(new ToggleRowSpec("Core clock", () -> data.showGpuCoreClock, (d, v) -> d.showGpuCoreClock = v)));
rows.add(toggleRow(new ToggleRowSpec("Memory clock", () -> data.showGpuMemoryClock, (d, v) -> d.showGpuMemoryClock = v)));
}

rows.add(sectionRow("CPU module", "cpu"));
if (this.isOpen("cpu")) {
rows.add(toggleRow(new ToggleRowSpec("CPU", () -> data.showCpuModule, (d, v) -> d.showCpuModule = v)));
rows.add(toggleRow(new ToggleRowSpec("Temp", () -> data.showCpuTemperature, (d, v) -> d.showCpuTemperature = v)));
rows.add(toggleRow(new ToggleRowSpec("CLOCK", () -> data.showCpuClock, (d, v) -> d.showCpuClock = v)));
rows.add(toggleRow(new ToggleRowSpec("Per core grid", () -> data.showCpuCores, (d, v) -> d.showCpuCores = v)));
}

rows.add(sectionRow("RAM module", "ram"));
if (this.isOpen("ram")) {
rows.add(toggleRow(new ToggleRowSpec("RAM", () -> data.showRamModule, (d, v) -> d.showRamModule = v)));
rows.add(toggleRow(new ToggleRowSpec("JVM RAM", () -> data.showJvmLine, (d, v) -> d.showJvmLine = v)));
rows.add(toggleRow(new ToggleRowSpec("System line", () -> data.showSystemRamLine, (d, v) -> d.showSystemRamLine = v)));
}

rows.add(sectionRow("Graph module", "graph"));
if (this.isOpen("graph")) {
rows.add(toggleRow(new ToggleRowSpec("Graph", () -> data.showGraphModule, (d, v) -> d.showGraphModule = v)));
rows.add(stepperRow(new StepperRowSpec("Graph width", () -> data.graphWidth, v -> data.graphWidth = (int) v, 10, 40, 320, "%.0f")));
rows.add(stepperRow(new StepperRowSpec("Graph height", () -> data.graphHeight, v -> data.graphHeight = (int) v, 2, 10, 80, "%.0f")));
}

rows.add(sectionRow("Benchmark module", "bench"));
if (this.isOpen("bench")) {
rows.add(toggleRow(new ToggleRowSpec("Rec and summary", () -> data.showBenchmarkModule, (d, v) -> d.showBenchmarkModule = v)));
rows.add(stepperRow(new StepperRowSpec("Auto run length", () -> data.autoRunSeconds, v -> data.autoRunSeconds = (int) v, 10, 10, 600, "%.0f")));
}

rows.add(sectionRow("Look and feel", "look"));
if (this.isOpen("look")) {
rows.add(toggleRow(new ToggleRowSpec("Background", () -> data.showBackground, (d, v) -> d.showBackground = v)));
rows.add(toggleRow(new ToggleRowSpec("Custom font", () -> data.useCustomFont, (d, v) -> d.useCustomFont = v)));
rows.add(stepperRow(new StepperRowSpec("Scale", () -> data.scale, v -> data.scale = v, 0.05, 0.5, 3.0, "%.2fX")));
rows.add(stepperRow(new StepperRowSpec("Opacity", () -> data.opacity, v -> data.opacity = v, 0.05, 0.1, 1.0, "%.0f%%")));
rows.add(colorRow(new ColorRowSpec("Text color", () -> data.textColor, (d, v) -> d.textColor = v)));
}

rows.add(sectionRow("Warning colors", "colors"));
if (this.isOpen("colors")) {
rows.add(colorRow(new ColorRowSpec("Bad color", () -> data.badColor, (d, v) -> d.badColor = v)));
rows.add(colorRow(new ColorRowSpec("Medium color", () -> data.mediumColor, (d, v) -> d.mediumColor = v)));
rows.add(colorRow(new ColorRowSpec("Good color", () -> data.goodColor, (d, v) -> d.goodColor = v)));
rows.add(stepperRow(new StepperRowSpec("FPS red below", () -> data.fpsBadThreshold, v -> data.fpsBadThreshold = v, 5, 5, 200, "%.0f")));
rows.add(stepperRow(new StepperRowSpec("FPS green above", () -> data.fpsGoodThreshold, v -> data.fpsGoodThreshold = v, 5, 10, 240, "%.0f")));
rows.add(stepperRow(new StepperRowSpec("Usage red above", () -> data.usageBadThreshold, v -> data.usageBadThreshold = v, 5, 50, 100, "%.0f%%")));
rows.add(stepperRow(new StepperRowSpec("Usage yellow above", () -> data.usageMediumThreshold, v -> data.usageMediumThreshold = v, 5, 10, 100, "%.0f%%")));
rows.add(stepperRow(new StepperRowSpec("Temp red above", () -> data.temperatureBadThreshold, v -> data.temperatureBadThreshold = v, 5, 60, 110, "%.0fC")));
rows.add(stepperRow(new StepperRowSpec("Temp yellow above", () -> data.temperatureMediumThreshold, v -> data.temperatureMediumThreshold = v, 5, 30, 110, "%.0fC")));
}

rows.add(sectionRow("Sharing", "share"));
if (this.isOpen("share")) {
rows.add(actionRow(new ActionRowSpec("Export settings to clipboard", this::exportSettings)));
rows.add(actionRow(new ActionRowSpec("Import settings from clipboard", this::importSettings)));
}

return rows;
}

private SettingsList.Row toggleRow(ToggleRowSpec spec) {
return new SettingsList.ToggleRow(data, spec);
}

private SettingsList.Row stepperRow(StepperRowSpec spec) {
return new SettingsList.StepperRow(data, spec);
}

private SettingsList.Row colorRow(ColorRowSpec spec) {
return new SettingsList.ColorRow(data, spec);
}

private SettingsList.Row actionRow(ActionRowSpec spec) {
return new SettingsList.ActionRow(spec);
}

private SettingsList.Row sectionRow(String name, String key) {
return new SettingsList.SectionHeaderRow(name, this.isOpen(key), () -> this.toggleSection(key));
}

private boolean isOpen(String key) {
return this.sectionOpen.getOrDefault(key, true);
}

private void toggleSection(String key) {
this.sectionOpen.put(key, !this.isOpen(key));
this.settingsList.publishRows(buildRows());
}

private static int indexOfColor(int color) {
for (int i = 0; i < COLOR_VALUES.length; i++) {
if (COLOR_VALUES[i] == color) return i;
}
return 0;
}

@Override
public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
super.render(graphics, mouseX, mouseY, delta);
graphics.drawCenteredString(this.font, "BenchBurner overlay settings", this.width / 2, 12, 0xFFFFFFFF);
this.presetMenu.render(graphics, this.font, mouseX, mouseY);

String version = FabricLoader.getInstance().getModContainer("benchburner")
.map(container -> "benchburner " + container.getMetadata().getVersion().getFriendlyString())
.orElse("benchburner");
graphics.drawString(this.font, version, this.width - this.font.width(version) - 6, this.height - 14, 0xFFAAAAAA, false);
String footer = this.notice;
graphics.drawString(this.font, footer, 6, this.height - 14, this.notice.isEmpty() ? DIM : ACCENT, false);
}

@Override
public boolean mouseClicked(double mouseX, double mouseY, int button) {
if (this.presetMenu.handleClick(mouseX, mouseY)) {
playClick();
this.applyPresetChoice(this.presetMenu.selected());
return true;
}
for (SettingsList.Row row : this.settingsList.children()) {
int rowTop = row.rowY();
if (rowTop + ROW_HEIGHT <= this.listTop || rowTop >= this.listBottom) continue;
boolean columnHit = mouseX >= row.rowX() && mouseX < row.rowX() + row.rowWidth();
boolean rowHit = mouseY >= rowTop && mouseY < rowTop + ROW_HEIGHT;
boolean visibleHit = mouseY >= this.listTop && mouseY < this.listBottom;
if (!columnHit || !rowHit || !visibleHit) continue;
if (row.handlePress(mouseX, mouseY, button)) {
playClick();
return true;
}
}
return super.mouseClicked(mouseX, mouseY, button);
}

private void applyPresetChoice(String chosen) {
PresetData custom = this.presetStore.get(chosen);
if (custom != null) {
Presets.applyData(custom, this.data);
this.data.preset = PresetStore.normalize(chosen);
return;
}
Presets.apply(chosen.toLowerCase(Locale.ROOT), this.data);
}

private void savePreset() {
String name = PresetStore.normalize(this.presetNameBox.getValue());
if (name.isEmpty()) {
this.notice = "Type a name for the preset";
return;
}
for (String reserved : Presets.NAMES) {
if (name.equals(reserved)) {
this.notice = "That name is reserved";
return;
}
}
if (!this.presetStore.save(name, Presets.snapshot(this.data))) {
this.notice = "Could not save the preset";
return;
}
this.data.preset = name;
this.notice = "Saved preset " + name;
this.rebuildWidgets();
}

private void exportSettings() {
String json = PresetStore.toJson(Presets.snapshot(this.data));
this.minecraft.keyboardHandler.setClipboard(json);
this.notice = "Exported to clipboard";
}

private void importSettings() {
PresetData imported = PresetStore.fromJson(this.minecraft.keyboardHandler.getClipboard());
if (imported == null) {
this.notice = "Clipboard has no valid preset";
return;
}
this.presetStore.save("Imported", imported);
Presets.applyData(imported, this.data);
this.data.preset = "Imported";
this.notice = "Imported settings from clipboard";
this.rebuildWidgets();
}

@Override
public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
if (this.presetMenu.mouseScrolled(mouseX, mouseY, delta)) {
return true;
}
return super.mouseScrolled(mouseX, mouseY, delta);
}

@Override
public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
if (keyCode == GLFW.GLFW_KEY_ESCAPE && this.presetMenu.isOpen()) {
this.presetMenu.close();
return true;
}
return super.keyPressed(keyCode, scanCode, modifiers);
}

private void playClick() {
Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
}

@Override
public void onClose() {
this.minecraft.setScreen(this.parent);
}

@Override
public void removed() {
this.store.save();
}

final class SettingsList extends ObjectSelectionList<SettingsList.Row> {
SettingsList(Minecraft client, int width, int height, int y0, int y1, int itemHeight) {
super(client, width, height, y0, y1, itemHeight);
}

void publishRows(java.util.Collection<Row> rows) {
this.replaceEntries(rows);
}

@Override
public int getRowWidth() {
return this.width - 14;
}

public abstract static class Row extends ObjectSelectionList.Entry<Row> {
final ConfigData data;
private int rowX;
private int rowY;
private int rowWidth;
private int rowHeight = ROW_HEIGHT;

Row(ConfigData data) {
this.data = data;
}

@Override
public Component getNarration() {
return Component.literal("overlay setting");
}

abstract boolean handlePress(double mouseX, double mouseY, int button);

@Override
public void render(GuiGraphics graphics, int index, int y, int x, int width, int height, int mouseX, int mouseY, boolean hovered, float tickDelta) {
this.rowX = x;
this.rowY = y;
this.rowWidth = width;
this.rowHeight = height;
this.draw(graphics, hovered);
}

abstract void draw(GuiGraphics graphics, boolean hovered);

boolean inBounds(double mouseX, double mouseY) {
return mouseX >= this.rowX && mouseX < this.rowX + this.rowWidth
&& mouseY >= this.rowY && mouseY < this.rowY + this.rowHeight;
}

int rowX() {
return this.rowX;
}

int rowY() {
return this.rowY;
}

int rowWidth() {
return this.rowWidth;
}

void panel(GuiGraphics graphics, boolean hovered) {
int x = this.rowX;
int y = this.rowY;
int width = this.rowWidth;
int height = this.rowHeight;
graphics.fill(x, y, x + width, y + height, hovered ? ROW_HOVER : ROW_BG);
Outline.draw(graphics, x, y, width, height, hovered ? OUTLINE_HOVER : OUTLINE);
}
}

static final class ToggleRow extends Row {
private final ToggleRowSpec spec;

ToggleRow(ConfigData data, ToggleRowSpec spec) {
super(data);
this.spec = spec;
}

@Override
boolean handlePress(double mouseX, double mouseY, int button) {
if (!this.inBounds(mouseX, mouseY)) return false;
this.spec.setter().accept(this.data, button != 1);
return true;
}

@Override
void draw(GuiGraphics graphics, boolean hovered) {
panel(graphics, hovered);
String state = this.spec.getter().getAsBoolean() ? "on" : "off";
boolean on = this.spec.getter().getAsBoolean();
var font = Minecraft.getInstance().font;
graphics.drawString(font, this.spec.name(), this.rowX() + 6, this.rowY() + 7, TEXT, false);
int stateX = this.rowX() + this.rowWidth() - 6 - font.width(state);
int hintX = stateX - 10 - font.width("L on  R off");
graphics.drawString(font, "L on  R off", hintX, this.rowY() + 7, DIM, false);
graphics.drawString(font, state, stateX, this.rowY() + 7, on ? ACCENT : OFF, false);
}
}

static final class StepperRow extends Row {
private final StepperRowSpec spec;

StepperRow(ConfigData data, StepperRowSpec spec) {
super(data);
this.spec = spec;
}

@Override
boolean handlePress(double mouseX, double mouseY, int button) {
if (!this.inBounds(mouseX, mouseY)) return false;
writeQuiet(button == 1 ? this.spec.step() : -this.spec.step());
return true;
}

private void writeQuiet(double delta) {
double stepped = this.spec.getter().getAsDouble() + delta;
if (this.spec.step() >= 1) stepped = Math.round(stepped);
else stepped = Math.round(stepped / this.spec.step()) * this.spec.step();
if (stepped < this.spec.min()) stepped = this.spec.min();
if (stepped > this.spec.max()) stepped = this.spec.max();
this.spec.setter().accept(stepped);
}

@Override
void draw(GuiGraphics graphics, boolean hovered) {
panel(graphics, hovered);
String valueText = String.format(Locale.ROOT, this.spec.format(), this.spec.getter().getAsDouble());
var font = Minecraft.getInstance().font;
int x = this.rowX();
int y = this.rowY();
graphics.drawString(font, "L-", x + 6, y + 7, OFF, false);
graphics.drawString(font, "R+", x + 28, y + 7, OFF, false);
graphics.drawString(font, this.spec.name(), x + 52, y + 7, TEXT, false);
int valueX = x + this.rowWidth() - 6 - font.width(valueText);
graphics.drawString(font, valueText, valueX, y + 7, ACCENT, false);
}
}

static final class ColorRow extends Row {
private final ColorRowSpec spec;

ColorRow(ConfigData data, ColorRowSpec spec) {
super(data);
this.spec = spec;
}

@Override
boolean handlePress(double mouseX, double mouseY, int button) {
if (!this.inBounds(mouseX, mouseY)) return false;
int length = COLOR_VALUES.length;
int dir = button == 1 ? length - 1 : 1;
int next = (indexOfColor(this.spec.getter().getAsInt()) + dir) % length;
this.spec.setter().accept(this.data, COLOR_VALUES[next]);
return true;
}

@Override
void draw(GuiGraphics graphics, boolean hovered) {
panel(graphics, hovered);
int current = this.spec.getter().getAsInt();
String name = this.spec.name() + " " + COLOR_NAMES[indexOfColor(current)];
graphics.drawString(Minecraft.getInstance().font, name, this.rowX() + 6, this.rowY() + 7, TEXT, false);
int swatch = this.rowX() + this.rowWidth() - 30;
graphics.fill(swatch, this.rowY() + 6, swatch + 10, this.rowY() + 16, SWATCH_BG | (current & 0xFFFFFF));
Outline.draw(graphics, swatch - 1, this.rowY() + 5, 12, 12, OUTLINE);
}
}

static final class ActionRow extends Row {
private final ActionRowSpec spec;

ActionRow(ActionRowSpec spec) {
super(null);
this.spec = spec;
}

@Override
boolean handlePress(double mouseX, double mouseY, int button) {
if (!this.inBounds(mouseX, mouseY)) return false;
if (button != 0) return false;
this.spec.action().run();
return true;
}

@Override
void draw(GuiGraphics graphics, boolean hovered) {
panel(graphics, hovered);
graphics.drawString(Minecraft.getInstance().font, this.spec.label(), this.rowX() + 6, this.rowY() + 7, TEXT, false);
graphics.drawString(Minecraft.getInstance().font, ">", this.rowX() + this.rowWidth() - 15, this.rowY() + 7, OFF, false);
}
}

static final class SectionHeaderRow extends Row {
private final String name;
private final boolean open;
private final Runnable onToggle;

SectionHeaderRow(String name, boolean open, Runnable onToggle) {
super(null);
this.name = name;
this.open = open;
this.onToggle = onToggle;
}

@Override
boolean handlePress(double mouseX, double mouseY, int button) {
if (!this.inBounds(mouseX, mouseY)) return false;
this.onToggle.run();
return true;
}

@Override
void draw(GuiGraphics graphics, boolean hovered) {
int x = this.rowX();
int y = this.rowY();
int width = this.rowWidth();
int height = ROW_HEIGHT;
graphics.fill(x, y, x + width, y + height, 0xF0161616);
Outline.draw(graphics, x, y, width, height, OUTLINE_HOVER);
graphics.drawString(Minecraft.getInstance().font, this.name, x + 6, y + 7, TEXT, false);
graphics.drawString(Minecraft.getInstance().font, this.open ? "^" : "v", x + width - 14, y + 7, ACCENT, false);
}
}
}
}
