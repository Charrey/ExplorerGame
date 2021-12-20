package com.charrey.game.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.charrey.game.settings.Settings;
import com.charrey.game.util.SkinUtils;
import com.charrey.game.util.file.ExploreSaveFileFilter;
import com.charrey.game.util.file.FileUtils;
import com.charrey.game.util.file.filechooser.FileChooser;
import com.charrey.game.util.file.filechooser.FileChooserConfiguration;
import com.charrey.game.util.file.filechooser.LoadCallback;

import java.util.function.Consumer;

/**
 * Button in the bottom of the user interface meant for loading a save file.
 */
public class LoadButton extends TextButton {
    /**
     * Creates a button that prompts the user to select a save file, after which the save file is loaded.
     * @param saveLoader what should happen with the content of the save file
     */
    public LoadButton(Consumer<String> saveLoader) {
        super("Load", SkinUtils.getSkin());
        addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                FileChooserConfiguration conf = new FileChooserConfiguration(FileUtils.getLastSaveFile(), "Choose savegame to load", new ExploreSaveFileFilter(), "Load");
                FileChooser.chooseExistingFile(conf, new LoadCallback(saveLoader));
                return true;
            }
        });
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        setDisabled(Settings.currentlySimulating);
    }
}
