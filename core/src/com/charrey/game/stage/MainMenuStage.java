package com.charrey.game.stage;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.charrey.game.StageSwitcher;
import com.charrey.game.stage.actor.MainMenuButton;

public class MainMenuStage extends HideableStage {

    private final StageSwitcher stageSwitcher;
    private MainMenuButton playButton;

    public MainMenuStage(StageSwitcher stageSwitcher, BitmapFont font) {
        this.stageSwitcher = stageSwitcher;
        playButton = new MainMenuButton("Play!", font);
        playButton.setX(100);
        playButton.setY(200);
        addActor(playButton);
    }


    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Vector2 stageCoordinates = screenToStageCoordinates(new Vector2(screenX, screenY));
        Actor hitted = hit(stageCoordinates.x, stageCoordinates.y, false);
        if (hitted == playButton) {
            stageSwitcher.changeToGame();
        }
        return true;
    }

    @Override
    public float getHeight() {
        return playButton.getHeight();
    }

    @Override
    public float getWidth() {
        return playButton.getWidth();
    }

    @Override
    public void show() {
        //The Main Menu has no state, so nothing needs to be reset when hidden or shown.
    }

    @Override
    public void hide() {
        //The Main Menu has no state, so nothing needs to be reset when hidden or shown.
    }
}
