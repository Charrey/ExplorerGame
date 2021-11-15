package com.charrey.game.stage.game.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;


public class MainMenuButton extends TextButton {
    public MainMenuButton(Skin skin, Runnable onClick) {
        super("Menu", skin);
        addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                getClickListener().cancel();
                onClick.run();
                return true;
            }
        });
    }
}
