package com.charrey.game.ui.context;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import com.charrey.game.util.SkinUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class ContextMenuItem extends TextButton {

    protected ContextMenu parentMenu;

    protected ContextMenuItem(String text) {
        super(text, SkinUtils.getSkin());
        setDebug(true);
    }


    @Override
    public @Nullable Actor hit(float x, float y, boolean touchable) {
        if (x < 0 || x >= getWidth() || y < 0 || y >= getHeight()) {
            return null;
        } else {
            return this;
        }
    }

    public void setContextMenuParent(ContextMenu parentMenu) {
        this.parentMenu = parentMenu;
    }

    public @NotNull Rectangle getStageRectangle() {
        Vector2 location = this.localToStageCoordinates(new Vector2(getX(Align.left), getY(Align.bottom)));
        return new Rectangle(location.x, location.y, getWidth(), getHeight());
    }
}
