package io.masel.nbtviewer.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.labymod.api.component.data.DataComponentContainer;
import net.labymod.api.reference.annotation.Referenceable;

@Referenceable
public abstract class NBTApi {

    protected final Gson gson = new GsonBuilder()
            .serializeNulls()
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .create();

    /**
     * Check if the player has enabled advanced tooltips
     *
     * @return If the player has enabled advanced tooltips
     */
    public abstract boolean hasAdvancedToolsTips();

    /**
     * Pretty print the data components of an ItemStack
     *
     * @param components The data components of the ItemStack
     * @return The pretty printed data components of the ItemStack
     */
    public abstract String prettyPrint(DataComponentContainer components);

}