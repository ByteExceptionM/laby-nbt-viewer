package io.masel.nbtaddon.v1_19_2;

import io.masel.nbtaddon.api.INBTApi;
import net.labymod.api.models.Implements;
import net.labymod.api.nbt.tags.*;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.*;

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