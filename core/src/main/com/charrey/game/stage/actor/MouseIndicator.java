package com.charrey.game.stage.actor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

public class MouseIndicator extends Actor {

    private final @NotNull Texture texture;
    private static final boolean ENABLED = false;

    public MouseIndicator() {
        Pixmap map = new Pixmap(20, 20, Pixmap.Format.RGBA8888);
        map.setColor(1, 1, 0, 0.5f);
        map.fillCircle(10, 10, 10);
        texture = new Texture(map);
    }

    long lastPrint = 0;

    @Override
    public void act(float delta) {
        super.act(delta);
        setX(Gdx.input.getX());
        setY(getStage().getHeight() - Gdx.input.getY());
        if (System.currentTimeMillis() > lastPrint + 5000) {
            lastPrint = System.currentTimeMillis();
            if (ENABLED) {
                Logger.getLogger(getClass().getName()).finest(() -> "MOUSE x=" + getX() + " y=" + getY());
            }
        }
    }


    @Override
    public void draw(@NotNull Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        batch.draw(texture, Gdx.input.getX() - 10f, getStage().getHeight() - Gdx.input.getY() - 10);
    }
}
