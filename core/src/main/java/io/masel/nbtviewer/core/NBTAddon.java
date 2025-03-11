package io.masel.nbtviewer.core;

import io.masel.nbtviewer.api.INBTApi;
import io.masel.nbtviewer.core.api.generated.ReferenceStorage;
import io.masel.nbtviewer.core.config.NBTAddonConfiguration;
import io.masel.nbtviewer.core.listener.ItemStackTooltipListener;
import net.labymod.api.addon.LabyAddon;
import net.labymod.api.models.addon.annotation.AddonMain;

@AddonMain
public class NBTAddon extends LabyAddon<NBTAddonConfiguration> {

    private INBTApi nbtApi;

    @Override
    protected void load() {
        this.nbtApi = ((ReferenceStorage) this.referenceStorageAccessor()).inbtApi();
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