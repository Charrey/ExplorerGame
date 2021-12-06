package com.charrey.game.ui.context;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * Contextmenu item that performs some task when clicked and has no subitems
 */
public class LeafContextMenuItem extends ContextMenuItem {

    /**
     * Creates a new LeafContextMenuItem.
     * @param text text on the contextmenu item
     * @param onClick task performed when the contextmenu item is clicked
     */
    public LeafContextMenuItem(String text, @NotNull Runnable onClick) {
        super(text);
        addCaptureListener(new ClickListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (button == 0) {
                    onClick.run();
                    Arrays.stream(getStage().getRoot().getChildren().toArray()).filter(ContextMenu.class::isInstance).forEach(Actor::remove);
                }
                return true;
            }
        });
    }
}
