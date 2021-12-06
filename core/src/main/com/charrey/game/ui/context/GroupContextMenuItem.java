package com.charrey.game.ui.context;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.charrey.game.texture.CachedTexture;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

/**
 * Contextmenu item that houses a submenu and performs no operation when clicked. The submenu appears when hovering over
 * this menuitem.
 */
public class GroupContextMenuItem extends ContextMenuItem {

    @NotNull
    private static final CachedTexture childrenArrow = new CachedArrowTexture();
    private @Nullable ContextMenu subMenu;

    @Override
    public float getPrefWidth() {
        return super.getPrefWidth() + 30;
    }

    /**
     * Creates a new GroupContextMenuItem
     * @param text text to be shown on the item
     * @param children Methods that may be used to create the items in the submenu.
     */
    public GroupContextMenuItem(String text, @NotNull List<Supplier<ContextMenuItem>> children) {
        super(text);
        GroupContextMenuItem groupContextMenuItem = this;
        addCaptureListener(new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                super.enter(event, x, y, pointer, fromActor);
                if (subMenu == null || subMenu.getStage() == null) {
                    subMenu = new ContextMenu(((ContextMenu )getParent()).getDepth() + 1);
                    subMenu.setName("child-of-" + text);
                    getStage().getRoot().addActor(subMenu);
                    children.forEach(contextMenuItemSupplier -> subMenu.add(contextMenuItemSupplier.get()));
                    Vector2 coordinates = localToStageCoordinates(new Vector2(getX() + getWidth(), getY() - (getParent().getHeight() - getHeight())));
                    subMenu.setX(coordinates.x);
                    subMenu.setY(coordinates.y);
                }
            }

            @Override
            public boolean touchDown(@NotNull InputEvent event, float x, float y, int pointer, int button) {
                event.cancel();
                event.setTouchFocus(false);
                return true;
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                super.exit(event, x, y, pointer, toActor);
                if (groupContextMenuItem != toActor && toActor instanceof ContextMenuItem item && ((ContextMenu)item.getParent()).getDepth() <= ((ContextMenu)getParent()).getDepth()) {
                    Arrays.stream(getStage().getRoot().getChildren().toArray()).filter(actor -> actor instanceof ContextMenu menu && menu.getDepth() > ((ContextMenu)getParent()).getDepth()).forEach(Actor::remove);
                }
            }
        });
    }

    @Override
    public void draw(@NotNull Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        batch.draw(childrenArrow.get(15, (int) getHeight()), getX() + getWidth() - 15, getY(), 15, getHeight());
    }



    private static class CachedArrowTexture extends CachedTexture {
        @Override
        protected void computeTexture(@NotNull Pixmap pixels) {
            Vector2 topLeft = new Vector2(0, 5);
            Vector2 right = new Vector2(10, pixels.getHeight() / 2f);
            Vector2 bottomLeft = new Vector2(0, pixels.getHeight() - 5f);
            pixels.setColor(Color.WHITE);
            pixels.drawLine((int) topLeft.x, (int) topLeft.y, (int) right.x, (int) right.y);
            pixels.drawLine((int) bottomLeft.x, (int) bottomLeft.y, (int) right.x, (int) right.y);
        }
    }
}
