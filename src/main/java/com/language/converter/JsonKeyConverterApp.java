package com.language.converter;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.LoggerFactory;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class JsonKeyConverterApp {

	private static org.slf4j.Logger logger = LoggerFactory.getLogger(JsonKeyConverterApp.class);
	private static Path OPENEMS_UI_PATH = Paths.get("../OpenEMS/ui/src/assets/i18n", "de.json");
	private static Path UPDATE_DIR = Paths.get("../OpenEMS/ui/src");

	public static void main(String[] args) {
		if (args.length > 0) {
			OPENEMS_UI_PATH = Paths.get(args[0]);
		}

		List<String> translationKeys = new ArrayList<>();

		try {
			JsonElement jsonElement = JsonParser.parseReader(FileOperation.getFileReader(OPENEMS_UI_PATH));
			JsonElement convertedJson = ConvertKeys.convertKeys(jsonElement);
			GsonBuilder gsonBuilder = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting();
			String convertedJsonString = gsonBuilder.create().toJson(convertedJson);

			translationKeys = ExtractKey.extractKeys(jsonElement.getAsJsonObject(), "");
			translationKeys.forEach(System.out::println);

			FileOperation.writeFile(OPENEMS_UI_PATH, convertedJsonString);
			logger.info("Converted JSON written to: " + OPENEMS_UI_PATH);

			SearchKeysInFiles.searchKeysInFiles(translationKeys, UPDATE_DIR);
		} catch (IOException e) {
			logger.error("Error processing file: " + e.getMessage(), e);
		}
	}
}
