package com.charrey.game.model.simulatable;

import com.badlogic.gdx.graphics.Color;
import com.charrey.game.model.Direction;
import com.charrey.game.settings.NewBlockFactory;
import com.charrey.game.settings.Settings;
import com.charrey.game.texture.CachedGameFieldBlockTexture;
import com.charrey.game.texture.Drawable;
import com.charrey.game.util.GridItem;

import java.util.EnumMap;
import java.util.Map;

/**
 * Explorer that is destroyed when it comes into contact with a barrier
 */
public class WeakExplorer extends DirectionalSimulatable {

    private static final Map<Direction, CachedGameFieldBlockTexture> textures = new EnumMap<>(Direction.class);

    static {
        Direction.forEachConcrete(direction -> textures.put(direction, new CachedGameFieldBlockTexture(new Color(1f, 0f, 0.5f, 1), direction)));
    }

    private WeakExplorer(Direction direction, GridItem x) {
        super(x, direction, 0, 1, 1);
    }

    /**
     * Returns a factory that provides dimensions information of a WeakExplorer and can create them.
     *
     * @return the factory
     */
    public static NewBlockFactory<WeakExplorer> factory() {
        return new NewBlockFactory<>() {
            @Override
            public int getWidth() {
                return 1;
            }

            @Override
            public int getHeight() {
                return 1;
            }

            @Override
            public WeakExplorer makeSimulatable(GridItem location) {
                return new WeakExplorer(Settings.newBlockDirection, location);
            }
        };
    }

    @Override
    public void simulateStep() {
        if (blockedInDirection(getDirection())) {
            System.out.println("The weak explorer broke into a thousand pieces");
            removeFromMasterInNextStep();
        } else {
            advance();
        }
    }


    @Override
    public Drawable getTexture(int xOffset, int yOffset, int textureWidth, int textureHeight) {
        return textures.get(getDirection());
    }

    @Override
    public Simulatable copy() {
        return new WeakExplorer(getDirection(), getLocation());
    }

    @Override
    public String shortName() {
        return "Weak";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WeakExplorer that = (WeakExplorer) o;
        return getDirection() == that.getDirection() && getLocation().equals(that.getLocation());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
