package io.masel.nbtviewer.core.config;

import net.labymod.api.addon.AddonConfig;
import net.labymod.api.client.gui.screen.widget.widgets.input.SliderWidget;
import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.color.ColorPickerWidget.ColorPickerSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.dropdown.DropdownWidget;
import net.labymod.api.configuration.loader.annotation.ConfigName;
import net.labymod.api.configuration.loader.property.ConfigProperty;
import net.labymod.api.configuration.settings.annotation.SettingSection;
import net.labymod.api.util.Color;

@ConfigName("settings")
public class NBTAddonConfiguration extends AddonConfig {

    @SwitchSetting
    private final ConfigProperty<Boolean> enabled = new ConfigProperty<>(true);

    @SwitchSetting
    private final ConfigProperty<Boolean> onlyShowCustomData = new ConfigProperty<>(false);

    @SettingSection(value = "display", center = true)

    @SwitchSetting
    private final ConfigProperty<Boolean> hideEmptyValues = new ConfigProperty<>(true);

    @SwitchSetting
    private final ConfigProperty<Boolean> autoCollapseArrays = new ConfigProperty<>(true);

    @SettingSection(value = "pagination", center = true)

    @DropdownWidget.DropdownSetting
    @DropdownWidget.DropdownEntryTranslationPrefix("nbt-viewer.settings.paginationMode")
    private final ConfigProperty<PaginationMode> paginationMode = new ConfigProperty<>(PaginationMode.AUTO);

    @SliderWidget.SliderSetting(min = 5, max = 35)
    private final ConfigProperty<Integer> linesPerPage = new ConfigProperty<>(15)
            .visibilitySupplier(() -> this.paginationMode.get() == PaginationMode.FIXED);

    @SettingSection(value = "colors", center = true)

    @SwitchSetting
    private final ConfigProperty<Boolean> syntaxHighlighting = new ConfigProperty<>(true);

    @ColorPickerSetting
    private final ConfigProperty<Color> keyColor = new ConfigProperty<>(Color.of(0xFF55FFFF))
            .visibilitySupplier(this.syntaxHighlighting::get);

    @ColorPickerSetting
    private final ConfigProperty<Color> stringColor = new ConfigProperty<>(Color.of(0xFF55FF55))
            .visibilitySupplier(this.syntaxHighlighting::get);

    @ColorPickerSetting
    private final ConfigProperty<Color> numberColor = new ConfigProperty<>(Color.of(0xFFFFAA00))
            .visibilitySupplier(this.syntaxHighlighting::get);

    @ColorPickerSetting
    private final ConfigProperty<Color> boolNullColor = new ConfigProperty<>(Color.of(0xFFFF5555))
            .visibilitySupplier(this.syntaxHighlighting::get);

    @ColorPickerSetting
    private final ConfigProperty<Color> bracketColor = new ConfigProperty<>(Color.of(0xFFFFFFFF))
            .visibilitySupplier(this.syntaxHighlighting::get);

    @ColorPickerSetting
    private final ConfigProperty<Color> punctuationColor = new ConfigProperty<>(Color.of(0xFF555555))
            .visibilitySupplier(this.syntaxHighlighting::get);

    @Override
    public ConfigProperty<Boolean> enabled() {
        return this.enabled;
    }

    public ConfigProperty<Boolean> isOnlyShowCustomData() {
        return this.onlyShowCustomData;
    }

    public ConfigProperty<Boolean> isSyntaxHighlighting() {
        return this.syntaxHighlighting;
    }

    public ConfigProperty<PaginationMode> getPaginationMode() {
        return this.paginationMode;
    }

    public ConfigProperty<Integer> getLinesPerPage() {
        return this.linesPerPage;
    }

    public ConfigProperty<Boolean> isHideEmptyValues() {
        return this.hideEmptyValues;
    }

    public ConfigProperty<Boolean> isAutoCollapseArrays() {
        return this.autoCollapseArrays;
    }

    public ConfigProperty<Color> getKeyColor() {
        return this.keyColor;
    }

    public ConfigProperty<Color> getStringColor() {
        return this.stringColor;
    }

    public ConfigProperty<Color> getNumberColor() {
        return this.numberColor;
    }

    public ConfigProperty<Color> getBoolNullColor() {
        return this.boolNullColor;
    }

    public ConfigProperty<Color> getBracketColor() {
        return this.bracketColor;
    }

    public ConfigProperty<Color> getPunctuationColor() {
        return this.punctuationColor;
    }

    public enum PaginationMode {
        AUTO,
        FIXED;
    }

}