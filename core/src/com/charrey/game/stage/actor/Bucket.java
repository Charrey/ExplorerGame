package com.charrey.game.stage.actor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Pools;

public class Bucket extends Actor {


    private static final Texture image = new Texture(Gdx.files.internal("bucket.png"));

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        batch.draw(image, getX(), getY());
    }

    @Override
    public void act(float delta) {
        if (Gdx.input.isTouched()) {
            setX(getStage().screenToStageCoordinates(new Vector2(Gdx.input.getX(), Gdx.input.getY())).x - getWidth()/2);
        } else {
            boolean rightPressed = Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D);
            boolean leftPressed = Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A);
            if (rightPressed && !leftPressed) {
                setX(getX() + 200 * delta);
            } else if (!rightPressed && leftPressed) {
                setX(getX() - 200 * delta);
            }
        }
        super.act(delta);
    }

    public Rectangle getRectangle() {
        Rectangle res = Pools.get(Rectangle.class).obtain();
        res.x = getX();
        res.y = getY();
        res.width = getWidth();
        res.height = getHeight();
        return res;
    }

    @Override
    public void setX(float x) {
        if (getStage() != null) {
            x = Math.min(getStage().getWidth() - getWidth(), x);
            x = Math.max(0, x);
        }
        super.setX(x);
    }
}
