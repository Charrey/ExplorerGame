package com.charrey.game.util.file;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class Cache {

    private Cache () {}

    public static final String SETTINGS_FILENAME = "settings.json";
    private static Map<String, String> cachedProperties;

    static String get(String key) {
        loadCacheFromFile();
        return cachedProperties.get(key);
    }


    public static void save() throws IOException {
        if (cachedProperties != null) {
            Files.writeString(Paths.get(".").resolve(SETTINGS_FILENAME), new JSONObject(cachedProperties).toString());
        }
    }

    static void set(String key, String value) {
        loadCacheFromFile();
        cachedProperties.put(key, value);
    }


    private static void loadCacheFromFile() {
        if (cachedProperties == null) {
            cachedProperties = new HashMap<>();
            try {
                if (Files.notExists(Paths.get(".").resolve(SETTINGS_FILENAME))) {
                    Files.createFile(Paths.get(".").resolve(SETTINGS_FILENAME));
                    Files.writeString(Paths.get(".").resolve(SETTINGS_FILENAME), new JSONObject(cachedProperties).toString());
                } else {
                    String settings = new String(Files.readAllBytes(Paths.get(".").resolve(SETTINGS_FILENAME)));
                    JSONObject obj = new JSONObject(settings);
                    obj.keys().forEachRemaining(s -> cachedProperties.put(s, obj.getString(s)));
                }
            } catch (@NotNull IOException | JSONException e) {
                e.printStackTrace();
            }
        }
    }


}
