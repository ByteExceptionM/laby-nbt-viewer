package io.masel.nbtviewer.core.util;

import com.google.gson.*;

import java.util.Map;

public class JsonStringExpander {

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .serializeNulls()
            .create();

    public static String expand(String json) {
        try {
            JsonElement element = JsonParser.parseString(json);
            JsonElement expanded = expandElement(element);
            return GSON.toJson(expanded);
        } catch (Throwable ignored) {
            return json;
        }
    }

    private static JsonElement expandElement(JsonElement element) {
        if (element.isJsonObject()) {
            JsonObject result = new JsonObject();
            for (Map.Entry<String, JsonElement> entry : element.getAsJsonObject().entrySet()) {
                result.add(entry.getKey(), expandElement(entry.getValue()));
            }
            return result;
        }

        if (element.isJsonArray()) {
            JsonArray result = new JsonArray();
            for (JsonElement child : element.getAsJsonArray()) {
                result.add(expandElement(child));
            }
            return result;
        }

        if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
            String value = element.getAsString();

            // Try parsing the string as JSON directly
            try {
                JsonElement parsed = JsonParser.parseString(value);
                if (!parsed.isJsonPrimitive()) {
                    return expandElement(parsed);
                }
            } catch (Throwable ignored) {
            }

            // Try stripping SNBT quotes (single or double) and re-parsing
            String stripped = stripSnbtQuotes(value);
            if (!stripped.equals(value)) {
                try {
                    JsonElement parsed = JsonParser.parseString(stripped);
                    if (!parsed.isJsonPrimitive()) {
                        return expandElement(parsed);
                    }
                } catch (Throwable ignored) {
                }
            }
        }

        return element;
    }

    private static String stripSnbtQuotes(String snbt) {
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
