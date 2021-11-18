package com.charrey.game.stage.actor.context;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.charrey.game.util.SkinUtils;

import java.util.Collection;
import java.util.List;

public class ContextMenuItem extends TextButton {

    public ContextMenuItem(String text, Runnable onClick) {
        super(text, SkinUtils.getSkin());
    }

    public ContextMenuItem(String text, List<ContextMenuItem> children) {
        super(text, SkinUtils.getSkin());
    }
}
