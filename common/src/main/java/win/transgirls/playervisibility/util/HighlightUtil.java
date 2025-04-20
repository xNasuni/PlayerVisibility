package win.transgirls.playervisibility.util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Map;

public class HighlightUtil {
    private static final String STRING_COLOR = "§e"; // yellow
    private static final String BOOLEAN_TRUE_COLOR = "§a"; // green
    private static final String BOOLEAN_FALSE_COLOR = "§c"; // red
    private static final String NUMBER_COLOR = "§9"; // blue
    private static final String NULL_COLOR = "§7"; // dark gray
    private static final String RESET_COLOR = "§f"; // white
    private static final Gson gson = new Gson();

    private static String repeat(char c, int len) {
        return new String(new char[len]).replace('\0', c);
    }
    private static String repeat(String c, int len) {
        return new String(new char[len]).replaceAll("\0", c);
    }

    public static String highlightJson(String jsonString) {
        JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
        return highlightObject(jsonObject, 0);
    }
    private static String highlightObject(JsonObject object, int indentLevel) {
        StringBuilder formattedOutput = new StringBuilder(RESET_COLOR + "{\n");
        int count = 0;

        String indent = repeat("  ", indentLevel + 1);


        for (Map.Entry<String, JsonElement> kv : object.entrySet()) {
            formattedOutput.append(indent)
                    .append(highlightKey(kv.getKey()))
                    .append(highlightValue(kv.getValue(), indentLevel + 1));

            if (count < object.size() - 1) {
                formattedOutput.append(RESET_COLOR + ",");
            }
            formattedOutput.append("\n");
            count++;
        }

        formattedOutput.append(repeat("  ", indentLevel)).append(RESET_COLOR).append("}");
        return formattedOutput.toString();
    }
    private static String highlightKey(String key) {
        return STRING_COLOR + "\"" + key + "\"" + RESET_COLOR + ": ";
    }
    private static String highlightValue(JsonElement value, int indentLevel) {
        if (value.isJsonPrimitive()) {
            if (value.getAsJsonPrimitive().isString()) {
                return STRING_COLOR + "\"" + value.getAsString() + "\"" + RESET_COLOR;
            } else if (value.getAsJsonPrimitive().isBoolean()) {
                return (value.getAsBoolean() ? BOOLEAN_TRUE_COLOR : BOOLEAN_FALSE_COLOR) + value.getAsBoolean() + RESET_COLOR;
            } else if (value.getAsJsonPrimitive().isNumber()) {
                return NUMBER_COLOR + value.getAsNumber() + RESET_COLOR;
            }
        } else if (value.isJsonNull()) {
            return NULL_COLOR + "null" + RESET_COLOR;
        } else if (value.isJsonArray()) {
            return highlightArray(value.getAsJsonArray(), indentLevel);
        } else if (value.isJsonObject()) {
            return highlightObject(value.getAsJsonObject(), indentLevel);
        }

        return RESET_COLOR + value.toString();
    }
    private static String highlightArray(JsonArray array, int indentLevel) {
        StringBuilder arrayString = new StringBuilder(RESET_COLOR + "[");
        for (int i = 0; i < array.size(); i++) {
            arrayString.append(highlightValue(array.get(i), indentLevel));
            if (i < array.size() - 1) {
                arrayString.append(RESET_COLOR + ", ");
            }
        }
        arrayString.append(RESET_COLOR + "]");
        return arrayString.toString();
    }
}