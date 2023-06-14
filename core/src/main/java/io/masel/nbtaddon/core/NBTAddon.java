package io.masel.nbtaddon.core;

import io.masel.nbtaddon.api.INBTApi;
import io.masel.nbtaddon.core.config.NBTAddonConfiguration;
import io.masel.nbtaddon.core.generated.DefaultReferenceStorage;
import io.masel.nbtaddon.core.listener.ItemStackTooltipListener;
import net.labymod.api.addon.LabyAddon;
import net.labymod.api.models.addon.annotation.AddonMain;

@AddonMain
public class NBTAddon extends LabyAddon<NBTAddonConfiguration> {

    private INBTApi nbtApi;

    @Override
    protected void load() {
        this.nbtApi = ((DefaultReferenceStorage) this.referenceStorageAccessor()).inbtApi();
    }

    @Override
    protected void enable() {
        this.registerSettingCategory();

        this.registerListener(new ItemStackTooltipListener(this, this.nbtApi));
    }

    @Override
    protected Class<NBTAddonConfiguration> configurationClass() {
        return NBTAddonConfiguration.class;
    }

}