package com.charrey.game.stage.game.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.charrey.game.BlockType;
import com.charrey.game.Direction;
import com.charrey.game.util.SkinUtils;


import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;
import java.util.logging.Logger;

import static com.charrey.game.BlockType.*;
import static com.charrey.game.Direction.*;


public final class LeftPane extends Table {

    private TextButton selected;
    private BlockType blockSelected = null;
    private Direction blockDirection = NOT_APPLICCABLE;


    public LeftPane(float height) {
        addLeftButton("empty", null, NOT_APPLICCABLE);
        addLeftButton("barrier", BARRIER, NOT_APPLICCABLE);
        addLeftButton("up", SPLIT_EXPLORER, UP);
        addLeftButton("down", SPLIT_EXPLORER, DOWN);
        addLeftButton("left", SPLIT_EXPLORER, LEFT);
        addLeftButton("right", SPLIT_EXPLORER, RIGHT);

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
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int ignore) {
                event.cancel();
                selected.getClickListener().touchUp(event, 0f, 0f, 0, 0);
                selected = button;
                blockDirection = direction;
                blockSelected = type;
                selected.getClickListener().touchDown(event, 0f, 0f, 0, 0);
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                event.cancel();
            }
        });
        add(button).row();
    }


    public void hide() {
        InputEvent event = new InputEvent();
        event.setListenerActor(selected);
        selected.getClickListener().touchUp(event, 0, 0, 0, 0);
    }

    public BlockType getBlockSelected() {
        return blockSelected;
    }

    public Direction getBlockDirection() {
        return blockDirection;
    }
}
