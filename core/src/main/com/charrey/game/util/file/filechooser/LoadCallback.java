package com.charrey.game.util.file.filechooser;

import com.badlogic.gdx.files.FileHandle;
import com.charrey.game.util.file.FileUtils;

import java.util.function.Consumer;

/**
 * Class that is called to load a string from a file containing a savegame.
 */
public record LoadCallback(Consumer<FileHandle> stringConsumer) implements FileChooserCallback {

    @Override
    public void onFileChosen(FileHandle file) {
        file = file.sibling(file.nameWithoutExtension() + ".explore");
        FileUtils.setLastSaveFile(file);
        stringConsumer.accept(file);
    }

    @Override
    public void onCancellation() {
        //You don't want to load anything? Sure, then I don't do anything.
    }
}
