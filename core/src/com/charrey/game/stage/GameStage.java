package com.charrey.game.stage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.TimeUtils;
import com.charrey.game.stage.actor.Bucket;
import com.charrey.game.stage.actor.RainDrop;

import java.util.Iterator;

public class GameStage extends HideableStage {


    private static final Music rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));
    Bucket bucket;
    Array<RainDrop> raindrops;
    long lastDropTime;

    int dropsGathered;
    private Label score;

    static {
        rainMusic.setLooping(true);
    }


    public GameStage(BitmapFont font) {
        bucket = new Bucket();
        Label.LabelStyle style = new Label.LabelStyle(font, Color.WHITE);
        score = new Label("", style);
        addActor(bucket);
        addActor(score);
        raindrops = new Array<>();
    }


    private void spawnRainDrop() {
        RainDrop rainDrop = Pools.get(RainDrop.class).obtain();
        rainDrop.setX(MathUtils.random(0, 800-64));
        rainDrop.setY(480);
        rainDrop.setWidth(64);
        rainDrop.setHeight(64);
        raindrops.add(rainDrop);
        addActor(rainDrop);
        rainDrop.setZIndex(0);
        lastDropTime = TimeUtils.millis();
    }


    @Override
    public void act() {

        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        if (TimeUtils.millis() - lastDropTime > 1000) {
            spawnRainDrop();
        }
        Iterator<RainDrop> raindropIterator = raindrops.iterator();
        RainDrop currentRaindrop;
        Rectangle bucketRectangle = bucket.getRectangle();
        while (raindropIterator.hasNext()) {
            currentRaindrop = raindropIterator.next();
            Rectangle raindropRectangle = currentRaindrop.getRectangle();
            if (bucketRectangle.overlaps(currentRaindrop.getRectangle())) {
                raindropIterator.remove();
                currentRaindrop.playSound();
                currentRaindrop.remove();
                Pools.get(RainDrop.class).free(currentRaindrop);
                dropsGathered++;
            }
            Pools.get(Rectangle.class).free(raindropRectangle);
        }
        Pools.get(Rectangle.class).free(bucketRectangle);
        score.setText("Score: " + dropsGathered);
        super.act();
    }

    @Override
    public void show() {
        dropsGathered = 0;
        bucket.setWidth(64);
        bucket.setHeight(64);
        bucket.setX(getWidth()/2f - bucket.getWidth() / 2f);
        bucket.setY(getHeight() / 24f);
        score.setHeight(20);
        score.setY(getHeight(), Align.top);
        rainMusic.play();
        spawnRainDrop();
        setKeyboardFocus(bucket);
    }

    @Override
    public void dispose() {
        rainMusic.dispose();
        super.dispose();
    }

    @Override
    public void hide() {
        rainMusic.stop();
        raindrops.forEach(x -> {
            x.hide();
            x.remove();
            Pools.get(RainDrop.class).free(x);
        });
    }
}
