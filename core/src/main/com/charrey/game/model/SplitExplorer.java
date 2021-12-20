package com.charrey.game.model;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.charrey.game.texture.CachedGameFieldBlockTexture;
import com.charrey.game.util.GridItem;

import java.util.*;

/**
 * 1x1 simulatable that moves one step in some direction each simulation step. Upon encountering a barricade in its facing direction,
 * it instead disappears and ejects a copy of itself from its left- and right side (if there are no barricades to its left
 * or right side, respectively).
 */
public class SplitExplorer extends Simulatable {


    private Direction direction;
    private Direction nextDirection;
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
        super(location, 0, 1, 1);
        this.direction = direction;
        this.nextDirection = direction;
    }

    @Override
    public Simulatable copy() {
        return new SplitExplorer(direction, getLocation());
    }

    @Override
    public void simulateStep() {
        Set<Simulatable> inFrontOfMe = getInDirection(direction);
        if (inFrontOfMe.stream().anyMatch(Barrier.class::isInstance)) {
            Set<Direction> toSpawnIn = EnumSet.noneOf(Direction.class);
            if (getInDirection(direction.rotateLeft()).stream().noneMatch(Barrier.class::isInstance)) {
                toSpawnIn.add(direction.rotateLeft());
            }
            if (getInDirection(direction.rotateRight()).stream().noneMatch(Barrier.class::isInstance)) {
                toSpawnIn.add(direction.rotateRight());
            }
            if (toSpawnIn.isEmpty()) {
                removeInNextStep();
            } else {
                Iterator<Direction> iterator = toSpawnIn.iterator();
                this.nextDirection = iterator.next();
                advance(nextDirection);
                if (iterator.hasNext()) {
                    SplitExplorer splitOff = new SplitExplorer(nextDirection.opposite(), getLocation());
                    splitOff.setContainingGrid(getGrid());
                    splitOff.advanceNow();
                    addInNextStep(splitOff);
                }
            }
        } else {
            advance(direction);
        }
//        super.simulateStep();
    }

    private void advance(Direction direction) {
        switch(direction) {
            case UP -> move(0, 1);
            case DOWN -> move(0, -1);
            case LEFT -> move(-1, 0);
            case RIGHT -> move(1, 0);
        }
    }

    private void advanceNow() {
        switch(direction) {
            case UP -> moveNow(0, 1);
            case DOWN -> moveNow(0, -1);
            case LEFT -> moveNow(-1, 0);
            case RIGHT -> moveNow(1, 0);
        }
    }

    @Override
    public void stateSwitchStep() {
        direction = nextDirection;
        super.stateSwitchStep();
    }

    @Override
    public Texture getTexture(int xOffset, int yOffset, int textureWidth, int textureHeight) {
        return textures.get(direction).get(textureWidth, textureHeight);
    }

    @Override
    public String shortName() {
        return "S" + switch (direction) {
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
        return direction == that.direction && getLocation().equals(that.getLocation()) /*&& getPhase() == that.getPhase()*/;
    }

    @Override
    public int hashCode() {
        return direction.hashCode();
    }

    /**
     * Returns the direction this explorer is facing.
     * @return the direction
     */
    public Direction getDirection() {
        return direction;
    }
}
