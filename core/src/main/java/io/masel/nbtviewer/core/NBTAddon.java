package io.masel.nbtviewer.core;

import io.masel.nbtviewer.api.NBTApi;
import io.masel.nbtviewer.api.generated.ReferenceStorage;
import io.masel.nbtviewer.core.config.NBTAddonConfiguration;
import io.masel.nbtviewer.core.listener.ItemStackTooltipListener;
import net.labymod.api.addon.LabyAddon;
import net.labymod.api.models.addon.annotation.AddonMain;

@AddonMain
public class NBTAddon extends LabyAddon<NBTAddonConfiguration> {

    private NBTApi nbtApi;

    @Override
    protected void load() {
        this.nbtApi = ((ReferenceStorage) this.referenceStorageAccessor()).nbtApi();
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