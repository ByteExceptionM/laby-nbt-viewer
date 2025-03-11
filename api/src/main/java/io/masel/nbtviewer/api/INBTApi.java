package io.masel.nbtviewer.api;

import net.labymod.api.component.data.DataComponentContainer;
import net.labymod.api.reference.annotation.Referenceable;

@Referenceable
public interface INBTApi {

    /**
     * Check if the player has enabled advanced tooltips
     *
     * @return If the player has enabled advanced tooltips
     */
    boolean hasAdvancedToolsTips();

    /**
     * Pretty print the data components of an ItemStack
     *
     * @param components The data components of the ItemStack
     * @return The pretty printed data components of the ItemStack
     */
    String prettyPrint(DataComponentContainer components);

}