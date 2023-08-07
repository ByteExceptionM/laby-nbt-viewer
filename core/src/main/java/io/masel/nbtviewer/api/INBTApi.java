package io.masel.nbtviewer.api;

import net.labymod.api.nbt.tags.NBTTagCompound;
import net.labymod.api.reference.annotation.Referenceable;

@Referenceable
public interface INBTApi {

    boolean hasAdvancedToolsTips();

    String prettyPrint(NBTTagCompound nbtTagCompound);

}