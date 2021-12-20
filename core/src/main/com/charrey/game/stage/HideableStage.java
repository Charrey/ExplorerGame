package com.charrey.game.stage;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.charrey.game.util.mouse.MouseHistory;
import com.charrey.game.util.mouse.MouseHistoryRecord;

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


    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        final Vector2 stageCoordinates = new Vector2();
        screenToStageCoordinates(stageCoordinates.set(screenX, screenY));
        Actor target = hit(stageCoordinates.x, stageCoordinates.y, true);
        MouseHistory.touchDown(new MouseHistoryRecord(screenX, screenY, stageCoordinates.x, stageCoordinates.y, pointer, button, target));
        return super.touchDown(screenX, screenY, pointer, button);
    }
}
