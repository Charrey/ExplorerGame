package com.charrey.game.stage.game.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.charrey.game.util.file.ExploreSaveFileFilter;
import com.charrey.game.util.file.FileUtils;
import com.charrey.game.util.file.filechooser.FileChooser;
import com.charrey.game.util.file.filechooser.FileChooserConfiguration;
import com.charrey.game.util.file.filechooser.LoadCallback;

import java.util.function.Consumer;

public class LoadButton extends TextButton {
    public LoadButton(Skin skin, Consumer<String> saveLoader) {
        super("Load", skin);
        addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                FileChooserConfiguration conf = new FileChooserConfiguration(FileUtils.getLastSaveFile(), "Choose savegame to load", new ExploreSaveFileFilter(), "Load");
                FileChooser.chooseExistingFile(conf, new LoadCallback(saveLoader));
                return true;
            }
        });
    }
}
