package com.charrey.game.stage.game.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.charrey.game.util.SkinUtils;

public class ClearButton extends TextButton {
    public ClearButton(Runnable behaviour) {
        super("Clear", SkinUtils.getSkin());
        addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                getClickListener().cancel();
                behaviour.run();
                return true;
            }
        });
    }
}
