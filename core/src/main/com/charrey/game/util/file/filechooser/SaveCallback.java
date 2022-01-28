package com.charrey.game.util.file.filechooser;

import com.badlogic.gdx.files.FileHandle;
import com.charrey.game.model.Grid;
import com.charrey.game.model.serialize.XMLSerializer;
import com.charrey.game.util.file.FileUtils;

import java.nio.charset.StandardCharsets;

/**
 * Class that is called to save the string representation to a file.
 */
public record SaveCallback(Grid grid) implements FileChooserCallback {

    @Override
    public void onFileChosen(FileHandle file) {
        file = file.sibling(file.nameWithoutExtension() + ".explore");
        FileUtils.setLastSaveFile(file);
        String serialized = XMLSerializer.get().serializeToString(grid, file.file().toURI());
        file.writeBytes(serialized.getBytes(StandardCharsets.UTF_8), false);
    }

    @Override
    public void onCancellation() {
        //You don't want to save? Sure, then I don't save.
    }
}
