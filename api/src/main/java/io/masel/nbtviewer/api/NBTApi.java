package io.masel.nbtviewer.api;

import com.google.gson.*;
import net.labymod.api.component.data.DataComponentContainer;
import net.labymod.api.reference.annotation.Referenceable;

import java.util.Map;

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

    public String expandedPrettyPrint(DataComponentContainer components) {
        return this.expandJsonStrings(this.prettyPrint(components));
    }

    private String expandJsonStrings(String json) {
        try {
            JsonElement element = JsonParser.parseString(json);
            JsonElement expanded = this.expandElement(element);
            return this.gson.toJson(expanded);
        } catch (Throwable ignored) {
            return json;
        }
    }

    private JsonElement expandElement(JsonElement element) {
        if (element.isJsonObject()) {
            JsonObject result = new JsonObject();
            for (Map.Entry<String, JsonElement> entry : element.getAsJsonObject().entrySet()) {
                result.add(entry.getKey(), this.expandElement(entry.getValue()));
            }
            return result;
        }

        if (element.isJsonArray()) {
            JsonArray result = new JsonArray();
            for (JsonElement child : element.getAsJsonArray()) {
                result.add(this.expandElement(child));
            }
            return result;
        }

        if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
            String value = element.getAsString();

            try {
                JsonElement parsed = JsonParser.parseString(value);
                if (!parsed.isJsonPrimitive()) {
                    return this.expandElement(parsed);
                }
            } catch (Throwable ignored) {
            }

            String stripped = this.stripSnbtQuotes(value);
            if (!stripped.equals(value)) {
                try {
                    JsonElement parsed = JsonParser.parseString(stripped);
                    if (!parsed.isJsonPrimitive()) {
                        return this.expandElement(parsed);
                    }
                } catch (Throwable ignored) {
                }
            }
        }

        return element;
    }

    private String stripSnbtQuotes(String snbt) {
        if (snbt.length() < 2) {
            return snbt;
        }

        char quote = snbt.charAt(0);
        if ((quote != '"' && quote != '\'') || snbt.charAt(snbt.length() - 1) != quote) {
            return snbt;
        }

        StringBuilder result = new StringBuilder();
        for (int i = 1; i < snbt.length() - 1; i++) {
            char c = snbt.charAt(i);
            if (c == '\\' && i + 1 < snbt.length() - 1) {
                char next = snbt.charAt(i + 1);
                if (next == quote || next == '\\') {
                    result.append(next);
                    i++;
                    continue;
                }
            }
            result.append(c);
        }

        return result.toString();
    }

}