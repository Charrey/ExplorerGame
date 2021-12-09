package com.charrey.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.charrey.game.stage.*;
import com.charrey.game.util.file.Cache;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import static org.lwjgl.opengl.Display.getHeight;
import static org.lwjgl.opengl.Display.getWidth;

/**
 * Game that provides a way for the player to specify different types of blocks in a grid. Then, in a simulation these
 * blocks interact in specific ways.
 */
public class Explore extends Game implements StageSwitcher {

    private ExploreMainMenuStage mainMenuStage;
    private ExploreGameStage gameStage;
    private ExploreSettingsStage settingsStage;

    private HideableStage currentStage;


    @Override
    public void create() {
        mainMenuStage = new ExploreMainMenuStage(this);
        gameStage = new ExploreGameStage(this);
        settingsStage = new ExploreSettingsStage(this);
        currentStage = mainMenuStage;
        Gdx.input.setInputProcessor(currentStage);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        mainMenuStage.getViewport().update(width, height, true);
        gameStage.getViewport().update(width, height, true);
        settingsStage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        mainMenuStage.dispose();
        gameStage.dispose();
        settingsStage.dispose();
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

    /**
     * Sets which stage is shown in the window.
     * @param newStage stage to be shown
     */
    public void changeToStage(HideableStage newStage) {
        currentStage.hide();
        currentStage = newStage;
        currentStage.show();
        Gdx.input.setInputProcessor(currentStage);
        //Perform an update with a slightly different size.
        //Unfortunately, this is necessary to circumvent a LibGDX bug that prevents drawing straight lines.
        gameStage.getViewport().update(getWidth(), getHeight() + 1, true);
    }

    @Override
    public void changeToStage(@NotNull ExploreStage stage) {
        switch (stage) {
            case GAME -> changeToStage(gameStage);
            case MENU -> changeToStage(mainMenuStage);
            case SETTINGS -> changeToStage(settingsStage);
        }
    }
}
