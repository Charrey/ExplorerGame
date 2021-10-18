package com.charrey.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.charrey.game.stage.GameStage;
import com.charrey.game.stage.HideableStage;
import com.charrey.game.stage.MainMenuStage;

public class Drop extends Game implements StageSwitcher {


    private HideableStage mainMenuStage;
    private HideableStage gameStage;
    private HideableStage currentStage;


    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        mainMenuStage.getViewport().update(width, height, true);
        gameStage.getViewport().update(width, height, true);
    }


    @Override
    public void create() {
        BitmapFont font = new BitmapFont();
        mainMenuStage = new MainMenuStage(this, font);
        gameStage = new GameStage(font);
        currentStage = mainMenuStage;
        Gdx.input.setInputProcessor(currentStage);
    }

    @Override
    public void render() {
        super.render();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        currentStage.act();
        currentStage.draw();
    }

    @Override
    public void dispose() {
        mainMenuStage.dispose();
        gameStage.dispose();
    }

    @Override
    public void changeToMainMenu() {
        changeTo(mainMenuStage);
    }

    @Override
    public void changeToGame() {
        changeTo(gameStage);
    }

    @Override
    public void changeTo(HideableStage newStage) {
        currentStage.hide();
        currentStage = newStage;
        currentStage.show();
        Gdx.input.setInputProcessor(currentStage);

    }
}
