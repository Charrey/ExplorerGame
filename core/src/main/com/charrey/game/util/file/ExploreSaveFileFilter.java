package com.charrey.game.util.file;

import javax.swing.filechooser.FileFilter;
import java.io.File;

public class ExploreSaveFileFilter extends FileFilter {
    @Override
    public boolean accept(File f) {
        return f.isDirectory() || f.getName().endsWith(".explore");
    }

    @Override
    public String getDescription() {
        return "Explorer Savegames";
    }
}
