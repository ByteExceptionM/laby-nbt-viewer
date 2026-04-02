package io.masel.nbtviewer.api;

import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.format.TextColor;

public class JsonSyntaxHighlighter {

    public record SyntaxColors(
            TextColor key, TextColor string, TextColor number,
            TextColor boolNull, TextColor bracket, TextColor punctuation
    ) {
    }

    public static Component highlightLine(String line, SyntaxColors colors) {
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
            result.append(Component.text(trimmed).color(colors.bracket()));
            return result;
        }

        int colonIndex = findKeyValueColon(trimmed);

        if (colonIndex != -1) {
            String key = trimmed.substring(0, colonIndex);
            result.append(Component.text(key).color(colors.key()));
            result.append(Component.text(": ").color(colors.punctuation()));

            String valuePart = trimmed.substring(colonIndex + 2);
            appendValue(result, valuePart, colors);
        } else {
            appendValue(result, trimmed, colors);
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

    private static void appendValue(Component result, String rawValue, SyntaxColors colors) {
        boolean hasTrailingComma = rawValue.endsWith(",");
        String value = hasTrailingComma ? rawValue.substring(0, rawValue.length() - 1) : rawValue;

        TextColor color;
        if (value.startsWith("\"")) {
            color = colors.string();
        } else if (value.equals("true") || value.equals("false") || value.equals("null")) {
            color = colors.boolNull();
        } else if (value.equals("{") || value.equals("}") || value.equals("[") || value.equals("]")
                || value.equals("{}") || value.equals("[]")) {
            color = colors.bracket();
        } else if (isNumeric(value)) {
            color = colors.number();
        } else {
            color = colors.string();
        }

        result.append(Component.text(value).color(color));

        if (hasTrailingComma) {
            result.append(Component.text(",").color(colors.punctuation()));
        }
    }

    private static boolean isNumeric(String value) {
        if (value.isEmpty()) {
            return false;
        }

        int i = 0;
        if (value.charAt(0) == '-' || value.charAt(0) == '+') {
            i++;
        }

        boolean hasDigit = false;
        boolean hasDot = false;

        for (; i < value.length(); i++) {
            char c = value.charAt(i);
            if (c >= '0' && c <= '9') {
                hasDigit = true;
            } else if (c == '.' && !hasDot) {
                hasDot = true;
            } else if ((c == 'e' || c == 'E') && hasDigit) {
                hasDigit = false;
                if (i + 1 < value.length() && (value.charAt(i + 1) == '+' || value.charAt(i + 1) == '-')) {
                    i++;
                }
            } else {
                return false;
            }
        }

        return hasDigit;
    }

}
