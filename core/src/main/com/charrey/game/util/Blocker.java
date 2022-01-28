package com.charrey.game.util;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;

/**
 * Class that blocks user interaction with any Actor present before blocking
 */
public class Blocker extends Actor {

    private static final Blocker blocker = new Blocker();
    private static int requestsPending = 0;

    /**
     * Stops blocking the UI.
     *
     * @throws IllegalStateException if the blocker was not blocking.
     */
    public static void unblock() {
        if (requestsPending == 0) {
            throw new IllegalStateException("No block was pending");
        } else {
            requestsPending--;
            if (requestsPending == 0) {
                blocker.remove();
            }
        }
    }

    /**
     * Blocks user interaction with active Actors in a Stage.
     *
     * @param stage Stage to block actors in
     */
    public static void block(Stage stage) {
        if (requestsPending == 0) {
            stage.getRoot().addActor(blocker);
        }
        requestsPending++;
    }

    /**
     * Indicates whether the blocker is currently blocking the UI.
     *
     * @return whether blocking
     */
    public static boolean isBlocking() {
        return requestsPending > 0;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        setWidth(getStage().getWidth());
        setHeight(getStage().getHeight());
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        Pixmap myPixMap = new Pixmap((int) getWidth(), (int) getHeight(), Pixmap.Format.RGBA8888);
        myPixMap.setColor(0, 0, 0, 0.5f);
        myPixMap.fill();
        Texture tex = new Texture(myPixMap);
        tex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        batch.draw(tex, getX(), getY());
    }
}



