package com.language.converter;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Stream;
import org.slf4j.LoggerFactory;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class JsonKeyConverter {

	private static org.slf4j.Logger logger = LoggerFactory.getLogger(JsonKeyConverter.class);

	private static Path OPENEMS_UI_PATH = Paths.get("../OpenEMS/ui/src/assets/i18n", "de.json");
	private static Path UPDATE_DIR = Paths.get("../OpenEMS/ui/src");

	public static void main(String[] args) throws IOException {

		if (args.length > 0) {
			OPENEMS_UI_PATH = Paths.get(args[0]);
		}

		List<String> translationKeys = new ArrayList<>();

		try (FileReader reader = new FileReader(OPENEMS_UI_PATH.toFile())) {
			var jsonElement = JsonParser.parseReader(reader);
			var convertedJson = convertKeys(jsonElement);
			var gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
			var convertedJsonString = gson.toJson(convertedJson);
			translationKeys = extractKeys(jsonElement.getAsJsonObject(), "");

			translationKeys.forEach(System.out::println);

			try (FileWriter writer = new FileWriter(OPENEMS_UI_PATH.toFile())) {
				writer.write(convertedJsonString);
				logger.info("Converted JSON written to: " + OPENEMS_UI_PATH);
			} catch (IOException e) {
				logger.error("Error writing to output file: " + e.getMessage());
			}

		} catch (IOException e) {
			logger.error("Error reading input file: " + e.getMessage());
		}

		searchKeysInFiles(translationKeys, UPDATE_DIR);
	}

	private static List<String> extractKeys(JsonObject jsonObject, String parentKey) {
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

	private static void searchKeysInFiles(List<String> keys, Path updateDir) {
		try (Stream<Path> stream = Files.walk(updateDir)) {
			stream.filter(Files::isRegularFile)
					.filter(f -> f.toString().endsWith(".html") || f.toString().endsWith(".ts")).forEach(f -> {
						try {
							String content = Files.readString(f);
							boolean updated = false;
							for (String key : keys) {
								if (content.contains(key)) {
									logger.info("Found key \"" + key + "\" in file: " + f);
									String transformedKey;
									if (key.contains(".")) {
										String[] parts = key.split("\\.");
										StringBuilder sb = new StringBuilder();
										for (int i = 0; i < parts.length; i++) {
											if (i > 0) {
												sb.append(".");
											}
											sb.append(convertKey(parts[i]));
										}
										transformedKey = sb.toString();
									} else {
										transformedKey = convertKey(key);
									}
									logger.info("Key is converted to: " + transformedKey);

									content = content.replaceAll("\\b" + key + "\\b", transformedKey);
									updated = true;
								}
							}
							if (updated) {
								Files.writeString(f, content);
								logger.info("Updateed file: " + f + "\n");
							}
						} catch (IOException e) {
							logger.error("Failed to read/write file: " + f);
							e.printStackTrace();
						}
					});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

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
