package com.charrey.game.model.simulatable;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.charrey.game.model.Direction;
import com.charrey.game.texture.CachedGameFieldBlockTexture;
import com.charrey.game.util.GridItem;

import java.util.*;

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
      * @param direction direction the explorer is facing
     * @param location location of the explorer
     */
    public SplitExplorer(Direction direction, GridItem location) {
        super(location, direction, 0, 1, 1);
    }

    @Override
    public Simulatable copy() {
        return new SplitExplorer(getDirection(), getLocation());
    }

    @Override
    public void simulateStep() {
        Set<Simulatable> inFrontOfMe = getInDirection(getDirection());
        if (inFrontOfMe.stream().anyMatch(obj -> obj instanceof Barrier barrier && barrier.isBlocking())) {
            Set<Direction> toSpawnIn = EnumSet.noneOf(Direction.class);
            if (getInDirection(getDirection().rotateLeft()).stream().noneMatch(Barrier.class::isInstance)) {
                toSpawnIn.add(getDirection().rotateLeft());
            }
            if (getInDirection(getDirection().rotateRight()).stream().noneMatch(Barrier.class::isInstance)) {
                toSpawnIn.add(getDirection().rotateRight());
            }
            if (toSpawnIn.isEmpty()) {
                removeInNextStep();
            } else {
                Iterator<Direction> iterator = toSpawnIn.iterator();
                this.setDirection(iterator.next());
                advance(getNextDirection());
                if (iterator.hasNext()) {
                    SplitExplorer splitOff = new SplitExplorer(getNextDirection().opposite(), getLocation());
                    splitOff.setContainingGrid(getGrid());
                    splitOff.advanceNow(splitOff.getDirection());
                    addInNextStep(splitOff);
                }
            }
        } else {
            advance(getDirection());
        }
    }





    @Override
    public Texture getTexture(int xOffset, int yOffset, int textureWidth, int textureHeight) {
        return textures.get(getDirection()).get(textureWidth, textureHeight);
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
}
