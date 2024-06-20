package io.masel.nbtviewer.core.config;

import net.labymod.api.addon.AddonConfig;
import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.configuration.loader.annotation.ConfigName;
import net.labymod.api.configuration.loader.property.ConfigProperty;

@ConfigName("settings")
public class NBTAddonConfiguration extends AddonConfig {

    @SwitchSetting
    private final ConfigProperty<Boolean> enabled = new ConfigProperty<>(true);

    @SwitchSetting
    private final ConfigProperty<Boolean> copy = new ConfigProperty<>(false);

    @SwitchSetting
    private final ConfigProperty<Boolean> onlyShowCustomData = new ConfigProperty<>(false);

    @Override
    public ConfigProperty<Boolean> enabled() {
        return this.enabled;
    }

    public ConfigProperty<Boolean> isCopy() {
        return this.copy;
    }

    public ConfigProperty<Boolean> isOnlyShowCustomData() {
        return this.onlyShowCustomData;
    }

}