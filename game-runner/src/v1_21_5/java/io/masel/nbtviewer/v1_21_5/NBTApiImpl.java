package io.masel.nbtviewer.v1_21_5;

import com.google.gson.*;
import io.masel.nbtviewer.api.INBTApi;
import net.labymod.api.component.data.DataComponentContainer;
import net.labymod.api.component.data.DataComponentKey;
import net.labymod.api.models.Implements;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.StringTag;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Implements(INBTApi.class)
public class NBTApiImpl implements INBTApi {

    private final Gson gson = new GsonBuilder()
            .serializeNulls()
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .create();

    @Override
    public boolean hasAdvancedToolsTips() {
        return Minecraft.getInstance().options.advancedItemTooltips;
    }

    @Override
    public String prettyPrint(DataComponentContainer components) {
        JsonObject jsonObject = new JsonObject();

        List<DataComponentKey> dataComponentKeys = new ArrayList<>(components.keySet());
        dataComponentKeys.sort(Comparator.comparing(DataComponentKey::name));

        for (DataComponentKey dataComponentKey : dataComponentKeys) {
            Object value = components.get(dataComponentKey);

            if (value == null)
                continue;

            jsonObject.add(dataComponentKey.name(), this.parseValue(value));
        }

        return this.gson.toJson(jsonObject);
    }

    private JsonElement parseValue(@NotNull Object content) {
        try {
            return switch (content) {
                case NumericTag value -> new JsonPrimitive(value.toString());
                case StringTag value -> {
                    try {
                        yield JsonParser.parseString(value.toString());
                    } catch (Throwable ignored) {
                        yield new JsonPrimitive(value.toString());
                    }
                }
                case CompoundTag value -> this.gson.fromJson(value.toString(), JsonObject.class);
                case ListTag value -> {
                    JsonArray jsonArray = new JsonArray();

                    for (Object element : value.toArray()) {
                        jsonArray.add(this.parseValue(element));
                    }

                    yield jsonArray;
                }
                default -> new JsonPrimitive(content.toString());
            };
        } catch (Throwable cause) {
            cause.printStackTrace();
        }

        return JsonNull.INSTANCE;
    }

}