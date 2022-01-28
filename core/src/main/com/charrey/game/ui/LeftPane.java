package com.charrey.game.ui;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.charrey.game.model.Checker;
import com.charrey.game.model.Grid;
import com.charrey.game.model.serialize.SaveFormatException;
import com.charrey.game.model.serialize.XMLLoader;
import com.charrey.game.model.simulatable.*;
import com.charrey.game.model.simulatable.subgrid.SubGrid;
import com.charrey.game.settings.NewBlockFactory;
import com.charrey.game.settings.Settings;
import com.charrey.game.util.GridItem;
import com.charrey.game.util.SkinUtils;
import com.charrey.game.util.file.ExploreSaveFileFilter;
import com.charrey.game.util.file.FileUtils;
import com.charrey.game.util.file.filechooser.FileChooser;
import com.charrey.game.util.file.filechooser.FileChooserCallback;
import com.charrey.game.util.file.filechooser.FileChooserConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;
import java.util.logging.Logger;

import static com.charrey.game.model.Direction.LEFT;
import static com.charrey.game.model.Direction.UP;
import static com.charrey.game.util.ErrorUtils.showErrorMessage;


/**
 * Pane on the left hand side of the user interface in which the user selects which block (pointing which direction) is used
 * when interacting with the game field.
 */
public final class LeftPane extends Table {

    private TextButton selected;


    /**
     * Creates a new LeftPane with a specific height.
     *
     * @param height height of the pane
     */
    public LeftPane(float height) {
        addLeftButton("empty", null);
        addLeftButton("barrier", DefaultBarrier.factory());
        addLeftButton("conditional", ConditionalBarrier.factory());
        addLeftButton("split", SplitExplorer.factory(Settings.newBlockDirection));
        addLeftButton("random", RandomExplorer.factory(Settings.newBlockDirection));
        addLeftButton("weak", WeakExplorer.factory());

        TextButton subgridButton = new TextButton("subgrid", SkinUtils.getSkin());
        subgridButton.addCaptureListener(new ClickListener() {
            @Override
            public boolean touchDown(@NotNull InputEvent event, float x, float y, int pointer, int ignore) {
                event.cancel();
                FileChooserConfiguration conf = new FileChooserConfiguration(FileUtils.getLastSaveFile(), "Choose subgrid to load", new ExploreSaveFileFilter(), "Load");
                FileChooser.chooseExistingFile(conf, new SubgridLoadCallback(event, subgridButton));
                return true;
            }

            @Override
            public void touchUp(@NotNull InputEvent event, float x, float y, int pointer, int button) {
                event.cancel();
            }
        });
        add(subgridButton).row();

        float buttonHeight = height / getChildren().toArray().length;

        Optional<Float> optionalButtonWidth = Arrays.stream(getChildren().toArray()).map(actor -> ((TextButton) actor).getPrefWidth()).max(Comparator.comparingDouble(x -> x));
        if (optionalButtonWidth.isEmpty()) {
            Logger.getLogger(getClass().getName()).severe("No button on left bar present after adding them!");
        } else {
            Arrays.stream(getChildren().toArray()).forEachOrdered(actor -> getCell(actor).width(optionalButtonWidth.get()).height(buttonHeight));
        }
    }

    private void addLeftButton(String value, NewBlockFactory<? extends Simulatable> type) {
        TextButton button = new TextButton(value, SkinUtils.getSkin());
        if (selected == null) {
            selected = button;
        }
        button.addCaptureListener(new ClickListener() {
            @Override
            public boolean touchDown(@NotNull InputEvent event, float x, float y, int pointer, int ignore) {
                event.cancel();
                selected.getClickListener().touchUp(event, 0f, 0f, 0, 0);
                selected = button;
                Settings.newBlockFactory = type;
                selected.getClickListener().touchDown(event, 0f, 0f, 0, 0);
                return true;
            }

            @Override
            public void touchUp(@NotNull InputEvent event, float x, float y, int pointer, int button) {
                event.cancel();
            }
        });
        add(button).row();
    }

    /**
     * Ran when this Actor's stage is swapped out of context. Unselects the current block.
     */
    public void hide() {
        InputEvent event = new InputEvent();
        event.setListenerActor(selected);
        selected.getClickListener().touchUp(event, 0, 0, 0, 0);
    }

    private class SubgridLoadCallback implements FileChooserCallback {
        private final InputEvent event;
        private final TextButton subgridButton;

        public SubgridLoadCallback(InputEvent event, TextButton subgridButton) {
            this.event = event;
            this.subgridButton = subgridButton;
        }

        @Override
        public void onFileChosen(FileHandle file) {
            Grid subgrid;
            try {
                subgrid = new XMLLoader().load(file);
                Checker checker = new Checker();
                checker.addListener(gridCheckerError -> showErrorMessage(gridCheckerError.getMessage(), getStage()));
                if (checker.check(subgrid)) {
                    Settings.newBlockFactory = new NewBlockFactory<SubGrid>() {
                        @Override
                        public int getWidth() {
                            return subgrid.getExport(UP).size() + subgrid.getPad(UP).size();
                        }

                        @Override
                        public int getHeight() {
                            return subgrid.getExport(LEFT).size() + subgrid.getPad(LEFT).size();
                        }

                        @Override
                        public SubGrid makeSimulatable(GridItem location) {
                            return new SubGrid(location, subgrid, file.file().toPath());
                        }
                    };

                    selected.getClickListener().touchUp(event, 0f, 0f, 0, 0);
                    selected = subgridButton;
                    selected.getClickListener().touchDown(event, 0f, 0f, 0, 0);
                }
            } catch (SaveFormatException e) {
                showErrorMessage(e.getMessage(), getStage());
            }
        }

        @Override
        public void onCancellation() {
        }
    }
}
