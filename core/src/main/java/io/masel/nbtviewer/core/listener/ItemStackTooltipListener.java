package io.masel.nbtviewer.core.listener;

import io.masel.nbtviewer.api.NBTApi;
import io.masel.nbtviewer.core.NBTAddon;
import net.labymod.api.Laby;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.format.TextColor;
import net.labymod.api.client.gui.screen.key.Key;
import net.labymod.api.client.gui.window.Window;
import net.labymod.api.client.world.item.ItemStack;
import net.labymod.api.component.data.DataComponentContainer;
import net.labymod.api.component.data.DataComponentKey;
import net.labymod.api.component.data.NbtDataComponentContainer;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.input.MouseScrollEvent;
import net.labymod.api.event.client.world.ItemStackTooltipEvent;
import net.labymod.api.nbt.tags.NBTTagCompound;
import net.labymod.api.util.Color;

import javax.inject.Singleton;
import java.util.List;

@Singleton
public class ItemStackTooltipListener {

    private final Component barOpeningBracket = Component.text("[").color(TextColor.color(Color.DARK_GRAY.get()));
    private final Component barClosingBracket = Component.text("]").color(TextColor.color(Color.DARK_GRAY.get()));
    private final Component selectedPageIndicator = Component.text("▮").color(TextColor.color(Color.WHITE.get()));
    private final Component pageSymbol = Component.text("·").color(TextColor.color(Color.LIGHT_GRAY.get()));

    private final NBTAddon nbtAddon;
    private final NBTApi nbtApi;

    private int tooltipPage = 0;
    private String lastTooltipId = "";

    public ItemStackTooltipListener(NBTAddon nbtAddon, NBTApi nbtApi) {
        this.nbtAddon = nbtAddon;
        this.nbtApi = nbtApi;
    }

    @Subscribe
    public void onItemStackTooltip(ItemStackTooltipEvent event) {
        if (!this.nbtApi.hasAdvancedToolsTips()) {
            return;
        }

        if (!Laby.labyAPI().minecraft().isKeyPressed(Key.L_SHIFT)) {
            return;
        }

        ItemStack itemStack = event.itemStack();

        if (!itemStack.hasDataComponentContainer()) {
            return;
        }

        Window window = Laby.labyAPI().minecraft().minecraftWindow();

        float guiScaleFloat = window.getScale();
        int guiScale = Math.max(1, Math.round(guiScaleFloat));

        int linesPerPage = Math.max(3, 45 / guiScale);

        DataComponentContainer components = itemStack.getDataComponentContainer();

        if (this.nbtAddon.configuration().isOnlyShowCustomData().getOrDefault(false)) {
            DataComponentKey customDataKey = DataComponentKey.fromId("minecraft", "custom_data");

            if (!components.has(customDataKey)) {
                return;
            }

            components = new NbtDataComponentContainer((NBTTagCompound) components.get(customDataKey));
        }

        String id = itemStack.getAsItem().getIdentifier().getNamespace() + components.hashCode();

        if (!id.equals(this.lastTooltipId)) {
            this.tooltipPage = 0;
            this.lastTooltipId = id;
        }

        String pretty = this.nbtApi.prettyPrint(components);

        List<String> lines = List.of(pretty.split("\n"));
        int totalPages = Math.max(1, (int) Math.ceil((double) lines.size() / linesPerPage));

        this.tooltipPage = Math.min(this.tooltipPage, totalPages - 1);

        List<Component> tooltipLines = event.getTooltipLines();
        tooltipLines.add(Component.empty());

        for (int i = this.tooltipPage * linesPerPage; i < Math.min(lines.size(), (this.tooltipPage + 1) * linesPerPage); i++) {
            tooltipLines.add(Component.text(lines.get(i)));
        }

        if (totalPages > 1) {
            tooltipLines.add(Component.empty());
            tooltipLines.add(this.getPageBar(totalPages));
            tooltipLines.add(Component.empty());

            tooltipLines.add(
                    Component.text("Page " + (this.tooltipPage + 1) + "/" + totalPages)
                            .color(TextColor.color(Color.LIGHT_GRAY.get()))
            );
        }

        if (this.nbtAddon.configuration().isCopy().getOrDefault(false)) {
            Laby.labyAPI().minecraft().setClipboard(pretty);
        }
    }

    @Subscribe
    public void onMouseScroll(MouseScrollEvent event) {
        if (!Laby.labyAPI().minecraft().isKeyPressed(Key.L_SHIFT) ||
                !Laby.labyAPI().minecraft().minecraftWindow().isScreenOpened()) {
            return;
        }

        event.setCancelled(true);

        double scrollDelta = event.delta();

        if (scrollDelta < 0) {
            this.tooltipPage++;
            return;
        }

        if (scrollDelta <= 0 || this.tooltipPage <= 0) {
            return;
        }

        this.tooltipPage--;
    }

    private Component getPageBar(int totalPages) {
        Component bar = this.barOpeningBracket.copy();

        for (int i = 0; i < totalPages; i++) {
            if (i == this.tooltipPage) {
                bar.append(this.selectedPageIndicator);
                continue;
            }

            bar.append(this.pageSymbol);
        }

        return bar.append(this.barClosingBracket);
    }

}