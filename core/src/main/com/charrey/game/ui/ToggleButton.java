package com.charrey.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import java.util.List;

import static com.charrey.game.ui.ToggleButton.State.ONE;

/**
 * Button that toggles between three distinct states when clicked on by the user.
 */
public class ToggleButton extends Table {

    private final ToggleButtonStyle style;
    private State state = ONE;
    private boolean needsRender = false;


    /**
     * Creates a new ToggleButton
     *
     * @param skin skin of the button
     */
    public ToggleButton(Skin skin) {
        super(skin);
        setTouchable(Touchable.enabled);
        style = getSkin().get(ToggleButtonStyle.class);
        addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                toggle();
            }
        });
    }

    /**
     * Provides the state of this button
     *
     * @return current state
     */
    public State getState() {
        return state;
    }

    /**
     * Sets the state of the button
     *
     * @param newState new state
     */
    public void setState(State newState) {
        state = newState;
        needsRender = true;
        fire(new ChangeListener.ChangeEvent());
    }

    private void toggle() {
        setState(state.next());
    }

    public float getPrefWidth() {
        float width = super.getPrefWidth();
        if (style.state1 != null) width = Math.max(width, style.state1.getMinWidth());
        if (style.state2 != null) width = Math.max(width, style.state2.getMinWidth());
        if (style.state3 != null) width = Math.max(width, style.state3.getMinWidth());
        return width;
    }

    public float getPrefHeight() {
        float height = super.getPrefHeight();
        if (style.state1 != null) height = Math.max(height, style.state1.getMinHeight());
        if (style.state2 != null) height = Math.max(height, style.state2.getMinHeight());
        if (style.state3 != null) height = Math.max(height, style.state3.getMinHeight());
        return height;
    }

    public float getMinWidth() {
        return getPrefWidth();
    }

    public float getMinHeight() {
        return getPrefHeight();
    }

    public void draw(Batch batch, float parentAlpha) {
        validate();
        setBackground(List.of(style.state1, style.state2, style.state3).get(state.ordinal()));
        super.draw(batch, parentAlpha);
        Stage stage = getStage();
        if (stage != null && stage.getActionsRequestRendering() && needsRender) {
            Gdx.graphics.requestRendering();
            needsRender = false;
        }
    }

    /**
     * Enum enumerating all three states this button can have
     */
    public enum State {
        /**
         * First button state
         */
        ONE,
        /**
         * Second button state
         */
        TWO,
        /**
         * Third button state
         */
        THREE;

        /**
         * Providdes the next state of a button
         *
         * @return next state
         */
        public State next() {
            return switch (this) {
                case ONE -> TWO;
                case TWO -> THREE;
                case THREE -> ONE;
            };
        }
    }

    /**
     * Button styles as specified by a Skin
     */
    @SuppressWarnings("unused")
    public static class ToggleButtonStyle {

        Drawable state1;
        Drawable state2;
        Drawable state3;

    }
}
