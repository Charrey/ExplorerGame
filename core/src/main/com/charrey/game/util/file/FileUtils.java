package com.charrey.game.util.file;

import com.badlogic.gdx.files.FileHandle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

/**
 * Provides utility methods involving files.
 */
public class FileUtils {

    private FileUtils() {}


    /**
     * Gets the last used location for saving or loading savegames. Used to set a default directory for file choosers
     * and to save and overwrite.
     * @return the last used save file.
     */
    @Nullable
    public static File getLastSaveFile() {
        String lastSave = Cache.get("lastSaveLocation");
        File directory = null;
        if (lastSave != null) {
            directory = new FileHandle(lastSave).parent().file();
        }
        return directory;
    }

    /**
     * Sets the last used location for saving or loading savegames. Used to set a default directory for file choosers
     * and to save and overwrite.
     * @param file the last used location
     */
    public static void setLastSaveFile(@NotNull FileHandle file) {
        Cache.set("lastSaveLocation", file.path());
    }
}
