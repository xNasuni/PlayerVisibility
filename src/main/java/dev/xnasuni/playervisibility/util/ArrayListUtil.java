package dev.xnasuni.playervisibility.util;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class ArrayListUtil {

    public static String[] ToLowerCaseArray(String[] StringArrayList) {
        ArrayList<String> LowercaseArrayList = new ArrayList<>();
        for (String NormalCaseStringValue : StringArrayList) {
            LowercaseArrayList.add(NormalCaseStringValue.toLowerCase());
        }
        return (String[]) LowercaseArrayList.toArray();
    }

    public static String GetCase(String[] StringArrayList, String StringValue) {
        AtomicReference<String> NormalCase = new AtomicReference<>(StringValue);
        for (String NormalCaseStringValue : StringArrayList) {
            if (NormalCaseStringValue.equalsIgnoreCase(StringValue)) {
                NormalCase.set(NormalCaseStringValue);
            }
        }
        return NormalCase.get();
    }

    public static boolean ContainsLowercase(String[] StringArrayList, String StringValue) {
        boolean ContainsLowercase = false;
        for (String NormalCaseStringValue : StringArrayList) {
            if (NormalCaseStringValue.equalsIgnoreCase(StringValue)) {
                ContainsLowercase = true;
                break;
            }
        }
        return ContainsLowercase;
    }

    public static String[] AddStringToArray(String[] StringArrayList, String StringValue) {
        List<String> CloneArrayList = new ArrayList<>(Arrays.stream(StringArrayList).toList());
        CloneArrayList.add(StringValue);
        return CloneArrayList.toArray(new String[0]);
    }

    public static String[] RemoveStringToArray(String[] StringArrayList, String StringValue) {
        List<String> CloneArrayList = new ArrayList<>(Arrays.stream(StringArrayList).toList());
        CloneArrayList.remove(StringValue);
        return CloneArrayList.toArray(new String[0]);
    }

}
