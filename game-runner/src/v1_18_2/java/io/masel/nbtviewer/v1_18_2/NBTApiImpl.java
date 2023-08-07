package io.masel.nbtviewer.v1_18_2;

import io.masel.nbtviewer.api.INBTApi;
import net.labymod.api.models.Implements;
import net.labymod.api.nbt.tags.NBTTagCompound;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;

@Implements(INBTApi.class)
public class NBTApiImpl implements INBTApi {

    @Override
    public boolean hasAdvancedToolsTips() {
        return Minecraft.getInstance().options.advancedItemTooltips;
    }

    @Override
    public String prettyPrint(NBTTagCompound nbtTagCompound) {
        CompoundTag compound = (CompoundTag) nbtTagCompound;

        return NbtUtils.prettyPrint(compound);
    }

}