package com.charrey.game.util.file.filechooser;

import com.badlogic.gdx.files.FileHandle;
import com.charrey.game.util.file.FileUtils;

import java.util.function.Consumer;

public class LoadCallback implements FileChooserCallback {

    private final Consumer<String> stringConsumer;

    public LoadCallback(Consumer<String> stringConsumer) {
        this.stringConsumer = stringConsumer;
    }

    @Override
    public void onFileChosen(FileHandle file) {
        file = file.sibling(file.nameWithoutExtension() + ".explore");
        FileUtils.setLastSaveFile(file);
        stringConsumer.accept(file.readString("UTF-8"));
    }

    @Override
    public void onCancellation() {
        //You don't want to load anything? Sure, then I don't do anything.
    }
}
