package com.charrey.game.model.simulatable;

import com.badlogic.gdx.graphics.Color;
import com.charrey.game.model.Direction;
import com.charrey.game.settings.NewBlockFactory;
import com.charrey.game.texture.CachedGameFieldBlockTexture;
import com.charrey.game.texture.Drawable;
import com.charrey.game.util.GridItem;

import java.util.*;

/**
 * 1x1 simulatable that moves one step in some direction each simulation step. Upon encountering a barricade in its facing direction,
 * it instead changes direction to a direction to its left or right where no barricade is present. If no such direction exists,
 * this is deleted.
 */
public class RandomExplorer extends DirectionalSimulatable {

    private static final Map<Direction, CachedGameFieldBlockTexture> textures = new EnumMap<>(Direction.class);

    static {
        Direction.forEachConcrete(direction -> textures.put(direction, new CachedGameFieldBlockTexture(new Color(1f, 0.5f, 0.5f, 1), direction)));
    }

    /**
     * Creates a new RandomExplorer
     *
     * @param direction initial direction the explorer is facing
     * @param location  location of the explorer
     */
    private RandomExplorer(Direction direction, GridItem location) {
        super(location, direction, 1, 1, 1);
    }

    /**
     * Returns a factory that provides dimensions imformation of a RandomExplorer and can create them.
     *
     * @param direction direction of the RandomExplorer
     * @return the factory
     */
    public static NewBlockFactory<RandomExplorer> factory(Direction direction) {
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
            public RandomExplorer makeSimulatable(GridItem location) {
                return new RandomExplorer(direction, location);
            }
        };
    }

    @Override
    public void simulateStep() {
        Set<Simulatable> inFrontOfMe = getInDirection(getDirection());
        if (inFrontOfMe.stream().anyMatch(obj -> obj instanceof Barrier barrier && barrier.isBlocking())) {
            List<Direction> toRotateTo = new LinkedList<>();
            if (getInDirection(getDirection().rotateLeft()).stream().noneMatch(Barrier.class::isInstance)) {
                toRotateTo.add(getDirection().rotateLeft());
            }
            if (getInDirection(getDirection().rotateRight()).stream().noneMatch(Barrier.class::isInstance)) {
                toRotateTo.add(getDirection().rotateRight());
            }
            if (toRotateTo.isEmpty()) {
                removeFromMasterInNextStep();
            } else {
                Collections.shuffle(toRotateTo);
                setDirection(toRotateTo.get(0));
                advance();
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
    public Simulatable copy() {
        return new RandomExplorer(getDirection(), getLocation());
    }

    @Override
    public String shortName() {
        return "R" + switch (getDirection()) {
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
        RandomExplorer that = (RandomExplorer) o;
        return getDirection() == that.getDirection() && getLocation().equals(that.getLocation()) /*&& getPhase() == that.getPhase()*/;
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
