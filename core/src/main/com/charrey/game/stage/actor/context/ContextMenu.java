package com.charrey.game.stage.actor.context;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Group;

import java.util.LinkedList;
import java.util.List;

import static com.badlogic.gdx.graphics.Pixmap.Format.RGB888;

public class ContextMenu extends Group {

    List<ContextMenuItem> menuItems = new LinkedList<>();

    public void add(ContextMenuItem test) {
        menuItems.add(test);
    }


    @Override
    public void draw(Batch batch, float parentAlpha) {
        Pixmap pixels = new Pixmap((int)getWidth(), (int)getHeight(), RGB888);
        pixels.setColor(new Color(0.5f, 1f, 0.5f, 1));
        pixels.fill();
        batch.draw(new Texture(pixels), getX(), getY());
        super.draw(batch, parentAlpha);
    }
}
