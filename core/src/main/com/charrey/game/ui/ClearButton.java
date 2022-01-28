package com.charrey.game.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.charrey.game.settings.Settings;
import com.charrey.game.util.SkinUtils;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;

/**
 * Button at the bottom of the user interface in the game stage that resets the game field when clicked.
 */
public class ClearButton extends Table {


    private final TextButton textButton;
    private final TextField width;
    private final TextField height;


    /**
     * Creates a new clear button.
     *
     * @param behaviour  behaviour when the button is pressed.
     * @param pixelWidth with of the button
     */
    public ClearButton(@NotNull BiConsumer<Integer, Integer> behaviour, float pixelWidth) {
        this.width = new TextField("8", SkinUtils.getSkin());
        this.height = new TextField("8", SkinUtils.getSkin());

        add(this.width).width(pixelWidth / 3).prefHeight(40).left();
        add(this.height).width(pixelWidth / 3).prefHeight(40).right();
        row();
        textButton = new TextButton("Clear", SkinUtils.getSkin());
        add(textButton).colspan(2).bottom().width(pixelWidth);
        textButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                textButton.getClickListener().cancel();
                behaviour.accept(Integer.parseInt(width.getText()), Integer.parseInt(height.getText()));
                return true;
            }
        });
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        textButton.setDisabled(Settings.currentlySimulating);
    }
}
