package com.charrey.game.util.file.filechooser;

import com.badlogic.gdx.files.FileHandle;
import com.charrey.game.util.file.FileUtils;

import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;

public record SaveCallback(Supplier<String> saveState) implements FileChooserCallback {

    @Override
    public void onFileChosen(FileHandle file) {
        file = file.sibling(file.nameWithoutExtension() + ".explore");
        FileUtils.setLastSaveFile(file);
        file.writeBytes(saveState.get().getBytes(StandardCharsets.UTF_8), false);
    }

    @Override
    public void onCancellation() {
        //You don't want to save? Sure, then I don't save.
    }
}
