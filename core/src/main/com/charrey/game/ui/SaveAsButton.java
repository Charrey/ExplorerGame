package com.charrey.game.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.charrey.game.model.Checker;
import com.charrey.game.model.Grid;
import com.charrey.game.settings.Settings;
import com.charrey.game.util.SkinUtils;
import com.charrey.game.util.file.ExploreSaveFileFilter;
import com.charrey.game.util.file.FileUtils;
import com.charrey.game.util.file.filechooser.FileChooser;
import com.charrey.game.util.file.filechooser.FileChooserConfiguration;
import com.charrey.game.util.file.filechooser.SaveCallback;

import static com.charrey.game.util.ErrorUtils.showErrorMessage;

/**
 * Button in the bottom of the user interface that saves the current specification to a file. This button will always
 * prompt the user to select a file location, contrary to the SaveButton class that uses the last used save location
 * by default.
 */
public class SaveAsButton extends TextButton {
    /**
     * Creates a new SaveAsbutton
     *
     * @param grid grid to check and save
     */
    public SaveAsButton(Grid grid) {
        super("Save as", SkinUtils.getSkin());
        addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Checker checker = new Checker();
                checker.addListener(gridCheckerError -> showErrorMessage(gridCheckerError.getMessage(), getStage()));
                if (checker.check(grid)) {
                    FileChooserConfiguration conf = new FileChooserConfiguration(FileUtils.getLastSaveFile(), "Choose save location", new ExploreSaveFileFilter(), "Save");
                    FileChooser.chooseAnyFile(conf, new SaveCallback(grid));
                }
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
