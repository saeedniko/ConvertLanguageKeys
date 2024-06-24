package com.language.converter;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map.Entry;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class JsonKeyConverter {

	private static Path OPENEMS_UI_PATH = Paths.get("../OpenEMS/ui/src/assets/i18n", "de.json");

	public static void main(String[] args) {

		if (args.length > 0) {
			OPENEMS_UI_PATH = Paths.get(args[0]);
		}
		// TODO run this if you need new json
		//Path outputFilePath = Paths.get("../OpenEMS/ui/src/assets/i18n", "de.json");

		try (FileReader reader = new FileReader(OPENEMS_UI_PATH.toFile())) {

			var jsonElement = JsonParser.parseReader(reader);
			var convertedJson = convertKeys(jsonElement);

			var gson = new GsonBuilder().setPrettyPrinting().create();
			var convertedJsonString = gson.toJson(convertedJson);

			System.out.println("Converting JSON:");
			// System.out.println(convertedJsonString);

			printKeys(convertedJson.getAsJsonObject(), "");

			// TODO run this if you need new json
			try (FileWriter writer = new FileWriter(OPENEMS_UI_PATH.toFile())) {
				writer.write(convertedJsonString);
				System.out.println("Converted JSON written to " + OPENEMS_UI_PATH);
			} catch (IOException e) {
				System.err.println("Error writing to output file: " + e.getMessage());
			}

		} catch (IOException e) {
			System.err.println("Error reading input file: " + e.getMessage());
		}
		
		// --- replace
		
//		try (Stream<Path> stream = Files.walk(OPENEMS_UI_PATH)) {
//		    stream.filter(Files::isRegularFile)
//		    .filter(html + ts)
//		          .forEach(f -> {
//		        	System.out.println(f);
//		        	
//		            try (Stream<String> stream = Files.lines(f);
//		                    FileOutputStream fop = new FileOutputStream(new File(outputFile))) {
//		                stream.map(line -> line += " manipulate line as required\n").forEach(line -> {
//		                    try {
//		                        fop.write(line.getBytes());
//		                    } catch (IOException e) {
//		                        e.printStackTrace();
//		                    }
//		                });
//		            }
//		          });
//		}
	}

	private static void printKeys(JsonObject jsonObject, String parentKey) {
		for (Entry<String, JsonElement> entry : jsonObject.entrySet()) {
			String key = parentKey.isEmpty() ? //
					entry.getKey() //
					: parentKey + "." + entry.getKey();
			if (entry.getValue().isJsonObject()) {
				printKeys(entry.getValue().getAsJsonObject(), key);
			} else {
				System.out.println(key);
			}
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
		jsonObject.entrySet()//
				.forEach(entry -> {
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