package com.charrey.game.stage.griddisplay;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.charrey.game.ui.ToggleButton;
import com.charrey.game.util.SkinUtils;

/**
 * Button at the edge of a GridField that allows the user to mark specific grid edges as exported or padded
 */
public class EdgeExportPadButton extends ToggleButton {

    private final boolean vertical;
    private final RotatedLabel label;

    /**
     * Creates a new EdgeExportPadButton
     *
     * @param vertical true iff the button is vertical (this rotates the text to fit a slim vertical button)
     */
    public EdgeExportPadButton(boolean vertical) {
        super(SkinUtils.getSkin());
        this.vertical = vertical;
        label = new RotatedLabel("", vertical);
        addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                label.setText(switch (getState()) {
                    case ONE -> "";
                    case TWO -> "export";
                    case THREE -> "pad";
                });
            }
        });
        add(label);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (vertical) {
            super.draw(batch, parentAlpha);
        } else {
            super.draw(batch, parentAlpha);
        }
    }

    private static class RotatedLabel extends Group {

        private final Label content;

        public RotatedLabel(String text, boolean rotated) {
            content = new Label(text, SkinUtils.getSkin());
            addActor(content);
            content.setX(0, Align.center);
            content.setY(0, Align.center);
            if (rotated) {
                addAction(Actions.rotateBy(90));
            }
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            super.draw(batch, parentAlpha);
        }

        public void setText(String s) {
            content.setText(s);
            content.setX(-content.getPrefWidth() / 2);
        }
    }
}
