package com.charrey.game.util.file;

import org.jetbrains.annotations.NotNull;

import javax.swing.filechooser.FileFilter;
import java.io.File;

/**
 * Filter used in file choosers to select only directories and files with a save game extension.
 */
public class ExploreSaveFileFilter extends FileFilter {
    @Override
    public boolean accept(@NotNull File f) {
        return f.isDirectory() || f.getName().endsWith(".explore");
    }

    @Override
    public @NotNull String getDescription() {
        return "Explorer Savegames";
    }
}
