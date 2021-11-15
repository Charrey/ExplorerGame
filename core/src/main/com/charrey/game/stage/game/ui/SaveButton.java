package com.charrey.game.stage.game.ui;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.charrey.game.util.file.ExploreSaveFileFilter;
import com.charrey.game.util.file.FileUtils;
import com.charrey.game.util.file.filechooser.FileChooser;
import com.charrey.game.util.file.filechooser.FileChooserConfiguration;
import com.charrey.game.util.file.filechooser.SaveCallback;


import java.io.File;
import java.util.function.Supplier;

public class SaveButton extends TextButton {
    public SaveButton(Skin skin, Supplier<String> saveState) {
        super("Save", skin);
        addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                File lastSaveDirectory = FileUtils.getLastSaveFile();
                if (lastSaveDirectory == null) {
                    FileChooserConfiguration conf = new FileChooserConfiguration(null, "Choose save location", new ExploreSaveFileFilter(), "Save");
                    FileChooser.chooseAnyFile(conf, new SaveCallback(saveState));
                } else {
                    new SaveCallback(saveState).onFileChosen(new FileHandle(lastSaveDirectory));
                }
                return true;
            }
        });
    }
}
