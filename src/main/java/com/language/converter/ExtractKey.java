package com.language.converter;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

public class ExtractKey {

    public static List<String> extractKeys(JsonObject jsonObject, String parentKey) {
        List<String> keys = new ArrayList<>();
        for (Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            String key = parentKey.isEmpty() ? entry.getKey() : parentKey + "." + entry.getKey();
            if (entry.getValue().isJsonObject()) {
                keys.addAll(extractKeys(entry.getValue().getAsJsonObject(), key));
            } else {
                keys.add(key);
            }
        }
        return keys;
    }
}