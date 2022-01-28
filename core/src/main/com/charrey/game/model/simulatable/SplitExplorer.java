package com.charrey.game.model.simulatable;

import com.badlogic.gdx.graphics.Color;
import com.charrey.game.model.Direction;
import com.charrey.game.settings.NewBlockFactory;
import com.charrey.game.texture.CachedGameFieldBlockTexture;
import com.charrey.game.texture.Drawable;
import com.charrey.game.util.GridItem;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * 1x1 simulatable that moves one step in some direction each simulation step. Upon encountering a barricade in its facing direction,
 * it instead disappears and ejects a copy of itself from its left- and right side (if there are no barricades to its left
 * or right side, respectively).
 */
public class SplitExplorer extends DirectionalSimulatable {


    private static final Map<Direction, CachedGameFieldBlockTexture> textures = new EnumMap<>(Direction.class);

    static {
        Direction.forEachConcrete(direction -> textures.put(direction, new CachedGameFieldBlockTexture(new Color(1f, 1f, 0.5f, 1), direction)));
    }

    /**
     * Creates a new SplitExplorer
     *
     * @param direction direction the explorer is facing
     * @param location  location of the explorer
     */
    private SplitExplorer(Direction direction, GridItem location) {
        super(location, direction, 0, 1, 1);
    }

    /**
     * Returns a factory that provides dimensions imformation of a SplitExplorer and can create them.
     *
     * @param direction direction of the SplitExplorer
     * @return the factory
     */
    public static NewBlockFactory<SplitExplorer> factory(Direction direction) {
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
            public SplitExplorer makeSimulatable(GridItem location) {
                return new SplitExplorer(direction, location);
            }
        };
    }

    @Override
    public Simulatable copy() {
        return new SplitExplorer(getDirection(), getLocation());
    }

    @Override
    public void simulateStep() {
        if (blockedInDirection(getDirection())) {
            List<Direction> toSpawnIn = new ArrayList<>(2);
            if (!blockedInDirection(getDirection().rotateLeft())) {
                toSpawnIn.add(getDirection().rotateLeft());
            }
            if (!blockedInDirection(getDirection().rotateRight())) {
                toSpawnIn.add(getDirection().rotateRight());
            }
            if (toSpawnIn.isEmpty()) {
                removeFromMasterInNextStep();
            } else {
                setDirection(toSpawnIn.get(0));
                advance(toSpawnIn.get(0));
                if (toSpawnIn.size() > 1) {
                    SplitExplorer splitOff = new SplitExplorer(toSpawnIn.get(1), getLocation());
                    splitOff.setContainingGrid(getContainerGrid());
                    splitOff.advanceNow();
                    addInNextStep(splitOff);
                }
            }
        } else {
            advance();
        }
    }

    @Override
    public Drawable getTexture(int xOffset, int yOffset, int textureWidth, int textureHeight) {
        return textures.get(getDirection());
    }

    @Override
    public String shortName() {
        return "S" + switch (getDirection()) {
            case DOWN -> "d";
            case UP -> "^";
            case RIGHT -> ">";
            case LEFT -> "<";
        };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SplitExplorer that = (SplitExplorer) o;
        return getDirection() == that.getDirection() && getLocation().equals(that.getLocation()) /*&& getPhase() == that.getPhase()*/;
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
