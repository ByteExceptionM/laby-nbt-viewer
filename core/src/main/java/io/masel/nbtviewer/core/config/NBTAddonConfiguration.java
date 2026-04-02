package io.masel.nbtviewer.core.config;

import net.labymod.api.addon.AddonConfig;
import net.labymod.api.client.gui.screen.widget.widgets.input.SliderWidget;
import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.dropdown.DropdownWidget;
import net.labymod.api.configuration.loader.annotation.ConfigName;
import net.labymod.api.configuration.loader.property.ConfigProperty;
import net.labymod.api.configuration.settings.annotation.SettingSection;

@ConfigName("settings")
public class NBTAddonConfiguration extends AddonConfig {

    @SwitchSetting
    private final ConfigProperty<Boolean> enabled = new ConfigProperty<>(true);

    @SwitchSetting
    private final ConfigProperty<Boolean> onlyShowCustomData = new ConfigProperty<>(false);

    @SwitchSetting
    private final ConfigProperty<Boolean> syntaxHighlighting = new ConfigProperty<>(true);

    @SettingSection("pagination")
    @DropdownWidget.DropdownSetting
    @DropdownWidget.DropdownEntryTranslationPrefix("nbt-viewer.settings.paginationMode")
    private final ConfigProperty<PaginationMode> paginationMode = new ConfigProperty<>(PaginationMode.AUTO);

    @SliderWidget.SliderSetting(min = 5, max = 25)
    private final ConfigProperty<Integer> linesPerPage = new ConfigProperty<>(15)
            .visibilitySupplier(() -> this.paginationMode.get() == PaginationMode.FIXED);

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

    public enum PaginationMode {
        AUTO,
        FIXED;
    }

}