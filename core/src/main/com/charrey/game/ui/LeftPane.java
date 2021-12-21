package com.charrey.game.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.charrey.game.settings.Settings;
import com.charrey.game.model.BlockType;
import com.charrey.game.model.Direction;
import com.charrey.game.util.SkinUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;
import java.util.logging.Logger;

import static com.charrey.game.model.BlockType.*;
import static com.charrey.game.model.Direction.*;


/**
 * Pane on the left hand side of the user interface in which the user selects which block (pointing which direction) is used
 * when interacting with the game field.
 */
public final class LeftPane extends Table {

    private TextButton selected;


    /**
     * Creates a new LeftPane with a specific height.
     * @param height height of the pane
     */
    public LeftPane(float height) {
        addLeftButton("empty", null, null);
        addLeftButton("barrier", BARRIER, null);
        addLeftButton("conditional", CONDITIONAL_BARRIER, null);
        addLeftButton("split", SPLIT_EXPLORER, UP);
        addLeftButton("random", RANDOM_EXPLORER, UP);

        float buttonHeight = height / getChildren().toArray().length;

        Optional<Float> optionalButtonWidth = Arrays.stream(getChildren().toArray()).map(actor -> ((TextButton)actor).getPrefWidth()).max(Comparator.comparingDouble(x -> x));
        if (optionalButtonWidth.isEmpty()) {
            Logger.getLogger(getClass().getName()).severe("No button on left bar present after adding them!");
        } else {
            Arrays.stream(getChildren().toArray()).forEachOrdered(actor -> getCell(actor).width(optionalButtonWidth.get()).height(buttonHeight));
        }
    }

    private void addLeftButton(String value, BlockType type, Direction direction) {
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
                Settings.newBlockDirection = direction;
                Settings.newBlockType = type;
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
}
