package fr.teddz.benchburner.b4.ui;

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
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
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
int listTop = 56;
int listHeight = Math.max(40, doneY - 4 - listTop);
this.settingsList = new SettingsList(this.minecraft, this.width - 20, listHeight, listTop, ROW_HEIGHT);
this.settingsList.setX(10);
this.addRenderableWidget(this.settingsList);
this.settingsList.replaceEntries(buildRows());

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
this.settingsList.replaceEntries(buildRows());
}

private static int indexOfColor(int color) {
for (int i = 0; i < COLOR_VALUES.length; i++) {
if (COLOR_VALUES[i] == color) return i;
}
return 0;
}

@Override
public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
super.extractRenderState(graphics, mouseX, mouseY, delta);
graphics.centeredText(this.font, "BenchBurner overlay settings", this.width / 2, 12, 0xFFFFFFFF);
this.presetMenu.render(graphics, this.font, mouseX, mouseY);

String version = FabricLoader.getInstance().getModContainer("benchburner")
.map(container -> "benchburner " + container.getMetadata().getVersion().getFriendlyString())
.orElse("benchburner");
graphics.text(this.font, version, this.width - this.font.width(version) - 6, this.height - 14, 0xFFAAAAAA, false);
String footer = this.notice;
graphics.text(this.font, footer, 6, this.height - 14, this.notice.isEmpty() ? DIM : ACCENT, false);
}

@Override
public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
if (this.presetMenu.handleClick(event.x(), event.y())) {
playClick();
this.applyPresetChoice(this.presetMenu.selected());
return true;
}
int listTop = this.settingsList.getY();
int listBottom = this.settingsList.getBottom();
for (SettingsList.Row row : this.settingsList.children()) {
int rowTop = row.getY();
if (rowTop + ROW_HEIGHT <= listTop || rowTop >= listBottom) continue;
boolean columnHit = event.x() >= row.getX() && event.x() < row.getX() + row.getWidth();
boolean rowHit = event.y() >= rowTop && event.y() < rowTop + ROW_HEIGHT;
boolean visibleHit = event.y() >= listTop && event.y() < listBottom;
if (!columnHit || !rowHit || !visibleHit) continue;
if (row.handlePress(event)) {
playClick();
return true;
}
}
return super.mouseClicked(event, doubleClick);
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
public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
if (this.presetMenu.mouseScrolled(mouseX, mouseY, scrollY)) {
return true;
}
return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
}

@Override
public boolean keyPressed(KeyEvent event) {
if (event.key() == GLFW.GLFW_KEY_ESCAPE && this.presetMenu.isOpen()) {
this.presetMenu.close();
return true;
}
return super.keyPressed(event);
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
SettingsList(Minecraft client, int width, int height, int y, int itemHeight) {
super(client, width, height, y, itemHeight);
}

@Override
public int getRowWidth() {
return this.getWidth() - 14;
}

public abstract static class Row extends ObjectSelectionList.Entry<Row> {
final ConfigData data;

Row(ConfigData data) {
this.data = data;
}

@Override
public Component getNarration() {
return Component.literal("overlay setting");
}

abstract boolean handlePress(MouseButtonEvent event);

boolean inBounds(double mouseX, double mouseY) {
int x = this.getX();
int y = this.getY();
return mouseX >= x && mouseX < x + this.getWidth() && mouseY >= y && mouseY < y + this.getHeight();
}

void panel(GuiGraphicsExtractor graphics, boolean hovered) {
int x = this.getX();
int y = this.getY();
int width = this.getWidth();
int height = this.getHeight();
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
boolean handlePress(MouseButtonEvent event) {
if (!this.inBounds(event.x(), event.y())) return false;
this.spec.setter().accept(this.data, event.button() != 1);
return true;
}

@Override
public void extractContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float tickDelta) {
panel(graphics, hovered);
String state = this.spec.getter().getAsBoolean() ? "on" : "off";
boolean on = this.spec.getter().getAsBoolean();
var font = Minecraft.getInstance().font;
graphics.text(font, this.spec.name(), this.getX() + 6, this.getY() + 7, TEXT, false);
int stateX = this.getX() + this.getWidth() - 6 - font.width(state);
int hintX = stateX - 10 - font.width("L on  R off");
graphics.text(font, "L on  R off", hintX, this.getY() + 7, DIM, false);
graphics.text(font, state, stateX, this.getY() + 7, on ? ACCENT : OFF, false);
}
}

static final class StepperRow extends Row {
private final StepperRowSpec spec;

StepperRow(ConfigData data, StepperRowSpec spec) {
super(data);
this.spec = spec;
}

@Override
boolean handlePress(MouseButtonEvent event) {
if (!this.inBounds(event.x(), event.y())) return false;
writeQuiet(event.button() == 1 ? this.spec.step() : -this.spec.step());
return true;
}

private void writeQuiet(double delta) {
double stepped = this.spec.getter().getAsDouble() + delta;
if (this.spec.step() >= 1) stepped = Math.round(stepped);
else stepped = Math.round(stepped / this.spec.step()) * this.spec.step();
this.spec.setter().accept(Math.clamp(stepped, this.spec.min(), this.spec.max()));
}

@Override
public void extractContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float tickDelta) {
panel(graphics, hovered);
String valueText = String.format(Locale.ROOT, this.spec.format(), this.spec.getter().getAsDouble());
var font = Minecraft.getInstance().font;
int x = this.getX();
int y = this.getY();
graphics.text(font, "L-", x + 6, y + 7, OFF, false);
graphics.text(font, "R+", x + 28, y + 7, OFF, false);
graphics.text(font, this.spec.name(), x + 52, y + 7, TEXT, false);
int valueX = x + this.getWidth() - 6 - font.width(valueText);
graphics.text(font, valueText, valueX, y + 7, ACCENT, false);
}
}

static final class ColorRow extends Row {
private final ColorRowSpec spec;

ColorRow(ConfigData data, ColorRowSpec spec) {
super(data);
this.spec = spec;
}

@Override
boolean handlePress(MouseButtonEvent event) {
if (!this.inBounds(event.x(), event.y())) return false;
int length = COLOR_VALUES.length;
int dir = event.button() == 1 ? length - 1 : 1;
int next = (indexOfColor(this.spec.getter().getAsInt()) + dir) % length;
this.spec.setter().accept(this.data, COLOR_VALUES[next]);
return true;
}

@Override
public void extractContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float tickDelta) {
panel(graphics, hovered);
int current = this.spec.getter().getAsInt();
String name = this.spec.name() + " " + COLOR_NAMES[indexOfColor(current)];
graphics.text(Minecraft.getInstance().font, name, this.getX() + 6, this.getY() + 7, TEXT, false);
int swatch = this.getX() + this.getWidth() - 30;
graphics.fill(swatch, this.getY() + 6, swatch + 10, this.getY() + 16, SWATCH_BG | (current & 0xFFFFFF));
Outline.draw(graphics, swatch - 1, this.getY() + 5, 12, 12, OUTLINE);
}
}

static final class ActionRow extends Row {
private final ActionRowSpec spec;

ActionRow(ActionRowSpec spec) {
super(null);
this.spec = spec;
}

@Override
boolean handlePress(MouseButtonEvent event) {
if (!this.inBounds(event.x(), event.y())) return false;
if (event.button() != 0) return false;
this.spec.action().run();
return true;
}

@Override
public void extractContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float tickDelta) {
panel(graphics, hovered);
graphics.text(Minecraft.getInstance().font, this.spec.label(), this.getX() + 6, this.getY() + 7, TEXT, false);
graphics.text(Minecraft.getInstance().font, ">", this.getX() + this.getWidth() - 15, this.getY() + 7, OFF, false);
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
boolean handlePress(MouseButtonEvent event) {
if (!this.inBounds(event.x(), event.y())) return false;
this.onToggle.run();
return true;
}

@Override
public void extractContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float tickDelta) {
int x = this.getX();
int y = this.getY();
int width = this.getWidth();
int height = this.getHeight();
graphics.fill(x, y, x + width, y + height, 0xF0161616);
Outline.draw(graphics, x, y, width, height, OUTLINE_HOVER);
graphics.text(Minecraft.getInstance().font, this.name, x + 6, y + 7, TEXT, false);
graphics.text(Minecraft.getInstance().font, this.open ? "^" : "v", x + this.getWidth() - 14, y + 7, ACCENT, false);
}
}
}
}
