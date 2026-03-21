package io.masel.nbtviewer.core.util;

import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.format.TextColor;

public class JsonSyntaxHighlighter {

    private static final TextColor KEY_COLOR = TextColor.color(0x55FFFF);
    private static final TextColor STRING_COLOR = TextColor.color(0x55FF55);
    private static final TextColor NUMBER_COLOR = TextColor.color(0xFFAA00);
    private static final TextColor BOOL_NULL_COLOR = TextColor.color(0xFF5555);
    private static final TextColor BRACKET_COLOR = TextColor.color(0xFFFFFF);
    private static final TextColor PUNCTUATION_COLOR = TextColor.color(0x555555);

    public static Component highlightLine(String line) {
        Component result = Component.empty();

        String trimmed = line.stripLeading();
        String indent = line.substring(0, line.length() - trimmed.length());

        if (!indent.isEmpty()) {
            result.append(Component.text(indent));
        }

        if (trimmed.isEmpty()) {
            return result;
        }

        if (isStructuralLine(trimmed)) {
            result.append(Component.text(trimmed).color(BRACKET_COLOR));
            return result;
        }

        int colonIndex = findKeyValueColon(trimmed);

        if (colonIndex != -1) {
            String key = trimmed.substring(0, colonIndex);
            result.append(Component.text(key).color(KEY_COLOR));
            result.append(Component.text(": ").color(PUNCTUATION_COLOR));

            String valuePart = trimmed.substring(colonIndex + 2);
            appendValue(result, valuePart);
        } else {
            appendValue(result, trimmed);
        }

        return result;
    }

    private static boolean isStructuralLine(String trimmed) {
        return trimmed.equals("{") || trimmed.equals("}")
                || trimmed.equals("[") || trimmed.equals("]")
                || trimmed.equals("{,") || trimmed.equals("},")
                || trimmed.equals("[,") || trimmed.equals("],")
                || trimmed.equals("},{") || trimmed.equals("],[");
    }

    private static int findKeyValueColon(String trimmed) {
        if (!trimmed.startsWith("\"")) {
            return -1;
        }

        int i = 1;
        while (i < trimmed.length()) {
            if (trimmed.charAt(i) == '\\') {
                i += 2;
                continue;
            }
            if (trimmed.charAt(i) == '"') {
                break;
            }
            i++;
        }

        int afterQuote = i + 1;
        if (afterQuote + 1 < trimmed.length()
                && trimmed.charAt(afterQuote) == ':'
                && trimmed.charAt(afterQuote + 1) == ' ') {
            return afterQuote;
        }

        return -1;
    }

    private static void appendValue(Component result, String rawValue) {
        boolean hasTrailingComma = rawValue.endsWith(",");
        String value = hasTrailingComma ? rawValue.substring(0, rawValue.length() - 1) : rawValue;

        TextColor color;
        if (value.startsWith("\"")) {
            color = STRING_COLOR;
        } else if (value.equals("true") || value.equals("false") || value.equals("null")) {
            color = BOOL_NULL_COLOR;
        } else if (value.equals("{") || value.equals("}") || value.equals("[") || value.equals("]")
                || value.equals("{}") || value.equals("[]")) {
            color = BRACKET_COLOR;
        } else if (isNumeric(value)) {
            color = NUMBER_COLOR;
        } else {
            color = STRING_COLOR;
        }

        result.append(Component.text(value).color(color));

        if (hasTrailingComma) {
            result.append(Component.text(",").color(PUNCTUATION_COLOR));
        }
    }

    private static boolean isNumeric(String value) {
        try {
            Double.parseDouble(value);
            return true;
        } catch (NumberFormatException ignored) {
            return false;
        }
    }

}
