package com.charrey.game.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.charrey.game.settings.Settings;
import com.charrey.game.util.SkinUtils;
import org.jetbrains.annotations.NotNull;

/**
 * Button at the bottom of the user interface in the game stage that resets the game field when clicked.
 */
public class ClearButton extends TextButton {
    /**
     * Creates a new clear button.
     * @param behaviour behaviour when the button is pressed.
     */
    public ClearButton(@NotNull Runnable behaviour) {
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


    @Override
    public void act(float delta) {
        super.act(delta);
        setDisabled(Settings.currentlySimulating);
    }
}
