package com.charrey.game.stage.game;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.charrey.game.StageSwitcher;
import com.charrey.game.stage.HideableStage;
import com.charrey.game.stage.actor.GameField;
import com.charrey.game.stage.game.ui.BottomPane;
import com.charrey.game.stage.game.ui.LeftPane;


public class ExploreGameStage extends HideableStage {

    private final LeftPane leftPane;

    private GameField gameField;

    public ExploreGameStage(StageSwitcher stageSwitcher, Skin skin) {
        Table layout = new Table(skin);

        BottomPane bottomPane = new BottomPane(skin, getWidth(), stageSwitcher);
        leftPane = new LeftPane(skin, getHeight() - bottomPane.getPrefHeight());
        gameField = new GameField(1000, 1000, leftPane::getBlockSelected, leftPane::getBlockDirection);

        bottomPane.setResetButtonBehaviour(gameField::reset);
        bottomPane.setSetGameStateString(gameField::serialize);
        bottomPane.setSaveLoader(gameField::load);
        bottomPane.setStartSimulation(() -> gameField.startSimulation());
        bottomPane.setStopSimulation(() -> gameField.stopSimulation());

        layout.add(leftPane).left();
        layout.add(gameField).left().row();
        layout.add(bottomPane).colspan(2);
        layout.setX(0 + layout.getPrefWidth() / 2);
        layout.setY(0 + layout.getPrefHeight() / 2);
        addActor(layout);
        show();
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
