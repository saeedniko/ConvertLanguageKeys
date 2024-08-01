package com.language.converter;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class ConvertKeys {

    public static JsonElement convertKeys(JsonElement jsonElement) {
        return convertJsonElement(jsonElement);
    }

    private static JsonElement convertJsonElement(JsonElement jsonElement) {
        if (jsonElement.isJsonObject()) {
            return convertJsonObject(jsonElement.getAsJsonObject());
        } else if (jsonElement.isJsonArray()) {
            return convertJsonArray(jsonElement.getAsJsonArray());
        } else {
            return jsonElement;
        }
    }

    private static JsonArray convertJsonArray(JsonArray jsonArray) {
        JsonArray convertedArray = new JsonArray();
        jsonArray.forEach(element -> convertedArray.add(convertJsonElement(element)));
        return convertedArray;
    }

    private static JsonObject convertJsonObject(JsonObject jsonObject) {
        JsonObject convertedObject = new JsonObject();
        jsonObject.entrySet().forEach(entry -> {
            String convertedKey = convertKey(entry.getKey());
            JsonElement convertedValue = convertJsonElement(entry.getValue());
            convertedObject.add(convertedKey, convertedValue);
        });
        return convertedObject;
    }

    public static String convertKey(String key) {
        var transformedKey = key;
        transformedKey = transformedKey.replaceAll("[^A-Za-z0-9]", "_");
        transformedKey = transformedKey.replaceAll("([a-z])([A-Z])", "$1_$2");
        transformedKey = transformedKey.toUpperCase();
        return transformedKey;
    }
}
