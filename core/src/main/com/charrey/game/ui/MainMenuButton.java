package com.charrey.game.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.charrey.game.util.SkinUtils;
import org.jetbrains.annotations.NotNull;


/**
 * Button that redirects the user to the main menu when clicked.
 */
public class MainMenuButton extends TextButton {
    /**
     * Creates a new MainMenuButton
     * @param onClick what should happen when the button is clicked
     */
    public MainMenuButton(@NotNull Runnable onClick) {
        super("Menu", SkinUtils.getSkin());
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
