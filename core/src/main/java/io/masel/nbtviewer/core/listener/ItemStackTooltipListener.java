package io.masel.nbtviewer.core.listener;

import io.masel.nbtviewer.api.INBTApi;
import io.masel.nbtviewer.core.NBTAddon;
import net.labymod.api.Laby;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.gui.screen.key.Key;
import net.labymod.api.client.world.item.ItemStack;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.world.ItemStackTooltipEvent;
import net.labymod.api.nbt.tags.NBTTagCompound;

import javax.inject.Singleton;
import java.util.List;

@Singleton
public class ItemStackTooltipListener {

    private final NBTAddon nbtAddon;
    private final INBTApi nbtApi;

    public ItemStackTooltipListener(NBTAddon nbtAddon, INBTApi nbtApi) {
        this.nbtAddon = nbtAddon;
        this.nbtApi = nbtApi;
    }

    @Subscribe
    public void onItemStackTooltip(ItemStackTooltipEvent event) {
        if (!this.nbtApi.hasAdvancedToolsTips())
            return;

        if (!Laby.labyAPI().minecraft().isKeyPressed(Key.L_SHIFT))
            return;

        List<Component> tooltipLines = event.getTooltipLines();

        ItemStack itemStack = event.itemStack();

        if (!itemStack.hasNBTTag())
            return;

        NBTTagCompound nbtTag = itemStack.getNBTTag();

        tooltipLines.add(Component.empty());

        String text = this.nbtApi.prettyPrint(nbtTag);

        for (String s : text.split("\n")) {
            tooltipLines.add(Component.text(s));
        }

        if (this.nbtAddon.configuration().isCopy().getOrDefault(false))
            Laby.labyAPI().minecraft().setClipboard(text);
    }

}