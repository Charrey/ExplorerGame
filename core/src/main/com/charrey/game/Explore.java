package com.charrey.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.charrey.game.stage.ExploreMainMenuStage;
import com.charrey.game.stage.ExploreStage;
import com.charrey.game.stage.HideableStage;
import com.charrey.game.stage.ExploreGameStage;
import com.charrey.game.util.file.Cache;

import java.io.IOException;

public class Explore extends Game implements StageSwitcher {

    private ExploreMainMenuStage mainMenuStage;
    private ExploreGameStage gameStage;

    private HideableStage currentStage;


    @Override
    public void create() {
        mainMenuStage = new ExploreMainMenuStage(this);
        gameStage = new ExploreGameStage(this);
        currentStage = mainMenuStage;
        Gdx.input.setInputProcessor(currentStage);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        mainMenuStage.getViewport().update(width, height, true);
        gameStage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        mainMenuStage.dispose();
        gameStage.dispose();
        try {
            Cache.save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void render() {
        super.render();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        currentStage.act();
        currentStage.draw();
    }

    public void changeToStage(HideableStage newStage) {
        currentStage.hide();
        currentStage = newStage;
        currentStage.show();
        Gdx.input.setInputProcessor(currentStage);
    }

    @Override
    public void changeToStage(ExploreStage stage) {
        if (stage == ExploreStage.GAME) {
            changeToStage(gameStage);
        } else if (stage == ExploreStage.MENU) {
            changeToStage(mainMenuStage);
        }
    }
}
