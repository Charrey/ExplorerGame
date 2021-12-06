package com.charrey.game.stage;

import com.badlogic.gdx.scenes.scene2d.Stage;

/**
 * This abstract class is extended by Stage implementations in this game and provides a way for the stage to 'know' when it is shown on the screen.
 */
public abstract class HideableStage extends Stage {

    /**
     * Called when the stage is shown in the window
     */
    public void show() {}


    /**
     * Called when the stage is replaced by a different stage
     */
    public void hide() {}
}
