package com.charrey.game.stage.actor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import com.charrey.game.stage.Hideable;

public class RainDrop extends Actor implements Pool.Poolable, Hideable {

    private static final Texture image = new Texture(Gdx.files.internal("droplet.png"));
    private static final Sound dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));

    public RainDrop() {
        setZIndex(0);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        batch.draw(image, getX(), getY());
    }

    @Override
    public void act(float delta) {
        setY(getY() - delta * 200);
        super.act(delta);
    }

    @Override
    public void reset() {
        setX(0);
        setY(0);
        setZIndex(0);
    }

    public Rectangle getRectangle() {
        Rectangle rectangle = Pools.get(Rectangle.class).obtain();
        rectangle.x = getX();
        rectangle.y = getY();
        rectangle.width = getWidth();
        rectangle.height = getHeight();
        return rectangle;
    }

    public void playSound() {
        dropSound.play();
    }

    @Override
    public void show() {
        //Nothing needs to be changed when a raindrop is unhidden
    }

    @Override
    public void hide() {
        dropSound.stop();
    }
}
