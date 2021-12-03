package com.charrey.game.util.file;

import com.badlogic.gdx.files.FileHandle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class FileUtils {

    private FileUtils() {}


    @Nullable
    public static File getLastSaveFile() {
        String lastSave = Cache.get("lastSaveLocation");
        File directory = null;
        if (lastSave != null) {
            directory = new FileHandle(lastSave).parent().file();
        }
        return directory;
    }

    public static void setLastSaveFile(@NotNull FileHandle file) {
        Cache.set("lastSaveLocation", file.path());
    }
}
