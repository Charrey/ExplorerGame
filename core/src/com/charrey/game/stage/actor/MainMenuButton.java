package com.charrey.game.stage.actor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

public class MainMenuButton extends Actor {

    private final TextButton textButton;
    private final Texture red = new Texture(Gdx.files.internal("red.jpg"));



    public MainMenuButton(String text, BitmapFont font) {
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = font;
        textButton = new TextButton(text, buttonStyle);
        setWidth(200);
        setHeight(40);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        batch.draw(red, getX(), getY(), getWidth(), getHeight());
        textButton.draw(batch, parentAlpha);
    }

    @Override
    public void setX(float x) {
        super.setX(x);
        textButton.setX(x);
    }

    @Override
    public void setWidth(float width) {
        super.setWidth(width);
        textButton.setWidth(width);
    }

    @Override
    public void setHeight(float height) {
        super.setHeight(height);
        textButton.setHeight(height);
    }

    @Override
    public void setY(float y) {
        super.setY(y);
        textButton.setY(y);
    }

}
