package com.charrey.game.model;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.charrey.game.model.simulatable.Simulatable;
import com.charrey.game.texture.CachedTexture;
import com.charrey.game.util.GridItem;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Class that houses the entire model that can be specified or simulated. This consists of a 2D grid with simulatable
 * elements in it that are located somewhere in the grid.
 */
public class Grid {

    private final Set<Simulatable> simulatables;

    private final Map<GridItem, Set<Simulatable>> map = new ConcurrentHashMap<>(); //only updated in stateChange
    private int width;
    private int height;
    private static final CachedTexture emptyGridItem;

    static {
        emptyGridItem = new CachedTexture() {
            @Override
            protected void computeTexture(Pixmap pixels) {
                pixels.setColor(new Color(0.3f, 0.3f, 0.3f, 1));
                pixels.fill();
                pixels.setColor(0, 0, 0, 1);
                pixels.drawRectangle(0, 0, pixels.getWidth(), pixels.getHeight());
            }
        };
    }

    /**
     * Creates a new model with specified grid dimensions
     * @param width width of the grid
     * @param height height of the grid
     */
    public Grid(int width, int height) {
        this.width = width;
        this.height = height;
        simulatables = Collections.synchronizedSet(new HashSet<>());
        for (int columnIndex = 0; columnIndex < width; columnIndex++) {
            for (int rowIndex = 0; rowIndex < height; rowIndex++) {
                map.put(new GridItem(columnIndex, rowIndex), new CopyOnWriteArraySet<>());
            }
        }
    }

    /**
     * Become a semantic copy of a different model
     * @param other model to copy
     */
    public void copy(Grid other) {
        this.width = other.width;
        this.height = other.height;
        this.simulatables.clear();
        other.simulatables.forEach(simulatable -> simulatables.add(simulatable.copy()));
        Grid thisGrid = this;
        this.simulatables.forEach(simulatable -> simulatable.setContainingGrid(thisGrid));
        map.clear();
        updateMapAndDeduplicate();
    }

    /**
     * Adds a simulatable to this model
     * @param simulatable simulatable to add
     */
    public void add(Simulatable simulatable) {
        outOfBoundsCheck("X", 0, width - 1, simulatable.getLocation().x());
        outOfBoundsCheck("Y", 0, height - 1, simulatable.getLocation().y());
        outOfBoundsCheck("width", 1, width, simulatable.getWidth());
        outOfBoundsCheck("height", 1, height, simulatable.getHeight());
        simulatables.add(simulatable);
        simulatable.setContainingGrid(this);
        updateMapAndDeduplicate();
    }

    private void outOfBoundsCheck(String propertyName, int lowerBound, int upperBound, int actualValue) {
        if (actualValue < lowerBound || actualValue > upperBound) {
            throw new IndexOutOfBoundsException(propertyName + " value out of range. Expected: {" + lowerBound + ".." + upperBound + "}. Actual: " + actualValue);
        }
    }

    /**
     * Removes all simulatables at a specific location of the grid.
     * @param location location of the grid
     */
    public void remove(GridItem location) {
        outOfBoundsCheck("X", 0, width - 1, location.x());
        outOfBoundsCheck("Y", 0, height - 1, location.y());
        if (!map.containsKey(location)) {
            return;
        }
        Set<Simulatable> simulatablesAtCoordinates = map.get(location);
        simulatablesAtCoordinates.forEach(simulatables::remove);
        updateMapAndDeduplicate();
    }

    /**
     * Removes a simulatable from the set of simulatables
     * @param simulatable simulatable to remove
     */
    public void remove(Simulatable simulatable) {
        simulatables.remove(simulatable);
    }

    /**
     * Returns a view of all simulatables at specific coordinates
     * @param location coordinates
     * @return view of all simulatables at those coordinates
     */
    public Set<Simulatable> getAtStrictGridLocation(GridItem location) {
        return Collections.unmodifiableSet(Objects.requireNonNullElse(map.get(location), Collections.emptySet()));
    }

    /**
     * Returns a view of all simulatables at specific coordinates, correcting for coordinates outside the model
     * @param location coordinates
     * @return view of all simulatables at those coordinates
     */
    public Set<Simulatable> getAtWrappedGridLocation(GridItem location) {
        return getAtStrictGridLocation(new GridItem(Math.floorMod(location.x(), width), Math.floorMod(location.y(), height)));
    }

    /**
     * Returns a texture to render a specific location of the grid.
     * @param location location to get the texture of
     * @param textureWidth desired width of texture in pixels
     * @param textureHeight desired height of texture in pixels
     * @return the texture
     */
    public Texture getTextureAtLocation(GridItem location, int textureWidth, int textureHeight) {
        List<Simulatable> simulatablesAtLocation = new ArrayList<>(getAtStrictGridLocation(location));
        if (simulatablesAtLocation.isEmpty()) {
            return emptyGridItem.get(textureWidth, textureHeight);
        } else {
            Simulatable visible = simulatablesAtLocation.stream().reduce(null, (simulatable, simulatable2) -> simulatable != null && simulatable.getRenderPriority() > simulatable2.getRenderPriority() ? simulatable : simulatable2);
            Objects.requireNonNull(visible);
            int xOffset = (location.x() - visible.getLocation().x()) % width;
            int yOffset = (location.y() - visible.getLocation().y()) % height;
            return Objects.requireNonNull(visible.getTexture(xOffset, yOffset, textureWidth, textureHeight));
        }
    }

    /**
     * Returns a view of the current simulatables in this model
     * @return view of all simulatables
     */
    public Set<Simulatable> getSimulatables() {
        return Collections.unmodifiableSet(simulatables);
    }

    /**
     * Removes all simulatables from this model (essentially, go to the state immediately after calling the constructor)
     */
    public void clear() {
        simulatables.clear();
        map.clear();
    }

    /**
     * Recalculates the map based on the containing simulatables and removes any duplicate simulatables at the same location.
     */
    public void updateMapAndDeduplicate() {
        //remove map entries that are no longer valid
        for (Map.Entry<GridItem, Set<Simulatable>> entry : map.entrySet()) {
            entry.getValue().removeIf(simulatable -> !simulatables.contains(simulatable) || !simulatable.getLocation().equals(entry.getKey()));
        }
        map.entrySet().removeIf(e -> e.getValue().isEmpty());
        //add map entries
        List<Simulatable> simulatablesCopy = new ArrayList<>(simulatables);
        for (Simulatable simulatable : simulatablesCopy) {
            map.computeIfAbsent(simulatable.getLocation(), coords -> Collections.synchronizedSet(new HashSet<>()));
            Set<Simulatable> simulatablesAtLocation = map.get(simulatable.getLocation());
            if (simulatablesAtLocation.stream().anyMatch(x -> x != simulatable && x.equals(simulatable))) {
                simulatables.remove(simulatable);
            } else {
                simulatablesAtLocation.add(simulatable);
            }
        }
    }

    /**
     * Returns the width of the grid of this model, i.e. how many 1-width simulatables fit next to each other.
     * @return the width
     */
    public int getWidth() {
        return width;
    }
    /**
     * Returns the height of the grid of this model, i.e. how many 1-height simulatables fit on top of each other.
     * @return the width
     */
    public int getHeight() {
        return height;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Grid with ").append(simulatables.size()).append(" simulatables").append("\n");
        for (int row = height - 1; row >=0; row--) {
            for (int column = 0; column < width; column++) {
                Set<Simulatable> atLocation = map.get(new GridItem(column, row));
                if (atLocation != null) {
                    sb.append(StringUtils.rightPad(map.get(new GridItem(column, row)).stream().map(Simulatable::shortName).toList().toString(), 10, " "));
                } else {
                    sb.append("[]        ");
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
