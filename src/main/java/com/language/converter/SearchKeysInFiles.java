package com.language.converter;

import static com.language.converter.ConvertKeys.convertKey;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SearchKeysInFiles {

	private static final Logger logger = LoggerFactory.getLogger(SearchKeysInFiles.class);

	public static void searchKeysInFiles(List<String> keys, Path updateDir) {
		try (Stream<Path> stream = Files.walk(updateDir)) {
			stream.filter(Files::isRegularFile)
					.filter(f -> f.toString().endsWith(".html") || f.toString().endsWith(".ts")).forEach(f -> {
						try {
							String content = FileOperation.readFile(f);
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
}
