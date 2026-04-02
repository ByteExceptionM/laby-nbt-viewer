package io.masel.nbtviewer.core.listener;

import io.masel.nbtviewer.api.NBTApi;
import io.masel.nbtviewer.core.NBTAddon;
import io.masel.nbtviewer.core.config.NBTAddonConfiguration.PaginationMode;
import io.masel.nbtviewer.core.util.JsonSyntaxHighlighter;
import net.labymod.api.Laby;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.format.NamedTextColor;
import net.labymod.api.client.component.format.TextDecoration;
import net.labymod.api.client.gui.screen.key.Key;
import net.labymod.api.client.world.item.ItemStack;
import net.labymod.api.component.data.DataComponentContainer;
import net.labymod.api.component.data.DataComponentKey;
import net.labymod.api.component.data.NbtDataComponentContainer;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.input.MouseScrollEvent;
import net.labymod.api.event.client.world.ItemStackTooltipEvent;
import net.labymod.api.nbt.tags.NBTTagCompound;

import javax.inject.Singleton;
import java.util.List;

@Singleton
public class ItemStackTooltipListener {

    private final Component barOpeningBracket = Component.text("[", NamedTextColor.DARK_GRAY);
    private final Component barClosingBracket = Component.text("]", NamedTextColor.DARK_GRAY);
    private final Component selectedPageIndicator = Component.text("▮", NamedTextColor.WHITE);
    private final Component pageSymbol = Component.text("·", NamedTextColor.GRAY);

    private final NBTAddon nbtAddon;
    private final NBTApi nbtApi;

    private int tooltipPage = 0;
    private String lastTooltipId = "";
    private String cachedPretty = "";
    private List<String> cachedLines = List.of();
    private boolean wasCPressed = false;

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
            this.cachedPretty = this.nbtApi.expandedPrettyPrint(components);
            this.cachedLines = List.of(this.cachedPretty.split("\n"));
        }

        int linesPerPage;
        if (this.nbtAddon.configuration().getPaginationMode().getOrDefault(PaginationMode.AUTO) == PaginationMode.AUTO) {
            float guiScale = Laby.labyAPI().minecraft().minecraftWindow().getScale();
            linesPerPage = Math.max(3, 45 / Math.max(1, Math.round(guiScale)));
        } else {
            linesPerPage = this.nbtAddon.configuration().getLinesPerPage().get();
        }

        boolean syntaxHighlighting = this.nbtAddon.configuration().isSyntaxHighlighting().getOrDefault(true);

        List<String> lines = this.cachedLines;
        int totalPages = Math.max(1, (int) Math.ceil((double) lines.size() / linesPerPage));

        this.tooltipPage = Math.min(this.tooltipPage, totalPages - 1);

        List<Component> tooltipLines = event.getTooltipLines();
        tooltipLines.add(Component.empty());

        for (int i = this.tooltipPage * linesPerPage; i < Math.min(lines.size(), (this.tooltipPage + 1) * linesPerPage); i++) {
            if (syntaxHighlighting) {
                tooltipLines.add(JsonSyntaxHighlighter.highlightLine(lines.get(i)));
            } else {
                tooltipLines.add(Component.text(lines.get(i)));
            }
        }

        if (totalPages > 1) {
            tooltipLines.add(Component.empty());
            tooltipLines.add(this.getPageBar(totalPages));
            tooltipLines.add(Component.empty());

            tooltipLines.add(Component.translatable(
                    "nbt-viewer.page",
                    NamedTextColor.GRAY,
                    Component.text(this.tooltipPage + 1),
                    Component.text(totalPages)
            ).append(Component.text(" (")).append(Component.translatable("nbt-viewer.copy", NamedTextColor.GRAY, TextDecoration.ITALIC)).append(Component.text(")")));
        } else {
            tooltipLines.add(Component.empty());
            tooltipLines.add(Component.translatable("nbt-viewer.copy", NamedTextColor.GRAY, TextDecoration.ITALIC));
        }

        boolean cPressed = Laby.labyAPI().minecraft().isKeyPressed(Key.C);
        if (cPressed && !this.wasCPressed) {
            Laby.labyAPI().minecraft().setClipboard(this.cachedPretty);
        }
        this.wasCPressed = cPressed;
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