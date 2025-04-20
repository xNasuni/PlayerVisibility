package win.transgirls.playervisibility.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.*;

public class ArrayListUtil {
    public static String getCase(List<String> stringArrayList, String stringValue) {
        String normalCase = stringValue;
        for (String normalCaseStringValue : stringArrayList) {
            if (normalCaseStringValue.equalsIgnoreCase(stringValue)) {
                normalCase = normalCaseStringValue;
                break;
            }
        }
        return normalCase;
    }
    public static boolean containsLowercase(List<String> stringArrayList, String stringValue) {
        boolean containsLowercase = false;
        for (String normalCaseStringValue : stringArrayList) {
            if (normalCaseStringValue.equalsIgnoreCase(stringValue)) {
                containsLowercase = true;
                break;
            }
        }
        return containsLowercase;
    }
    public static String joinSeperator(List<?> stringArrayList, String seperator) {
        StringBuilder string = new StringBuilder();
        Iterator<?> iterator = stringArrayList.iterator();
        while (iterator.hasNext()) {
            String item = iterator.next().toString();
            string.append(item);
            if (!iterator.hasNext()) {
                string.append(seperator);
            }
        }
        return string.toString();
    }
    public static String joinSeperator(List<?> stringArrayList) {
        return joinSeperator(stringArrayList, ", ");
    }
    public static ArrayList<String> getAsStringArray(JsonArray elementArrayList) {
        ArrayList<String> list = new ArrayList<>();
        for (JsonElement element : elementArrayList) {
            if (!element.isJsonPrimitive()) {
                continue;
            }
            if (element.getAsJsonPrimitive().isString()) {
                list.add(element.getAsJsonPrimitive().getAsString());
            }
        }
        return list;
    }
    public static Map<String, ArrayList<String>> getAsObjectStringMap(JsonObject object) {
        Map<String, ArrayList<String>> map = new HashMap<>();
        for (Map.Entry<String, JsonElement> set : object.entrySet()) {
            if (set.getValue().isJsonArray()) {
                map.put(set.getKey(), getAsStringArray(set.getValue().getAsJsonArray()));
            }
        }
        return map;
    }
    public static <T> ArrayList<T> cloneref(ArrayList<T> list) {
        ArrayList<T> cloned = new ArrayList<>();
        for (T value : list) {
            cloned.add(value);
        }
        return cloned;
    }
}