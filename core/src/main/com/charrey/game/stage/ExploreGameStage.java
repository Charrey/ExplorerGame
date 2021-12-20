package com.charrey.game.stage;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Collections;
import com.charrey.game.StageSwitcher;
import com.charrey.game.model.Direction;
import com.charrey.game.model.serialize.GridLoader;
import com.charrey.game.stage.actor.GameField;
import com.charrey.game.ui.BottomPane;
import com.charrey.game.ui.LeftPane;
import com.charrey.game.ui.context.ContextMenu;
import com.charrey.game.ui.context.ContextMenuItem;
import com.charrey.game.util.SkinUtils;
import com.charrey.game.util.mouse.MouseHistory;
import com.charrey.game.util.mouse.MouseHistoryRecord;
import com.charrey.game.util.mouse.MouseIndicator;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.StreamSupport;

/**
 * Stage describing the actual game part of the game.
 */
public class ExploreGameStage extends HideableStage {

    private final @NotNull LeftPane leftPane;

    private final @NotNull GameField gameField;

    static {
        Collections.allocateIterators = true;
    }

    /**
     * Creates a new GameStage.
     * @param stageSwitcher used to switch to other stages if the user desires
     */
    public ExploreGameStage(@NotNull StageSwitcher stageSwitcher) {
        Table layout = new Table(SkinUtils.getSkin());
        gameField = new GameField(900, 900);
        BottomPane bottomPane = new BottomPane(
                getWidth(),
                stageSwitcher,
                gameField::reset,
                gameField::serialize,
                serialized -> {
                    try {
                        gameField.load(serialized);
                    } catch (GridLoader.SaveFormatException e) {
                        e.printStackTrace();
                    }
                },
                gameField::toggleSimulation);
        leftPane = new LeftPane(getHeight() - bottomPane.getPrefHeight());
        layout.add(leftPane).left();
        layout.add(gameField).left().row();
        layout.add(bottomPane).colspan(2);
        layout.setX(0 + layout.getPrefWidth() / 2);
        layout.setY(0 + layout.getPrefHeight() / 2);
        addActor(layout);
        addActor(new MouseIndicator());
        show();
    }


    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        MouseHistoryRecord lastTouchDown = MouseHistory.lastTouchDown();
        if (lastTouchDown.target() == gameField) {
            Direction direction = Direction.relativeDirection(new Vector2(lastTouchDown.screenX(), lastTouchDown.screenY()), new Vector2(screenX, screenY));
            if (direction != null) {
                gameField.setDirectionLastAdded(direction);
            }
        }
        return super.touchDragged(screenX, screenY, pointer);
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button == 0) {
            List<Actor> contextMenus = StreamSupport.stream(getRoot().getChildren().select(ContextMenu.class::isInstance).spliterator(), false).toList();
            if (!contextMenus.isEmpty()) {
                Vector2 stageCoordinates = screenToStageCoordinates(new Vector2(screenX, screenY));
                Actor target = hit(stageCoordinates.x, stageCoordinates.y, true);
                if (!(target instanceof ContextMenu || target instanceof ContextMenuItem)) {
                    contextMenus.forEach(Actor::remove);
                    return true;
                }
            }
        } else if (button == 1) {
            ContextMenu.removeAll(this);
        }
        return super.touchDown(screenX, screenY, pointer, button);
    }



    @Override
    public void show() {
        ((TextButton)leftPane.getChild(0)).getClickListener().touchDown(new InputEvent(), 0, 0, 0, 0);
    }

    @Override
    public void hide() {
        leftPane.hide();
    }



}
