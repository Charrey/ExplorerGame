package com.charrey.game.ui.context;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class LeafContextMenuItem extends ContextMenuItem {

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
