package com.charrey.game.model;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.charrey.game.model.simulatable.Barrier;
import com.charrey.game.model.simulatable.EdgeType;
import com.charrey.game.model.simulatable.Simulatable;
import com.charrey.game.model.simulatable.subgrid.SubGrid;
import com.charrey.game.texture.CachedTexture;
import com.charrey.game.texture.Drawable;
import com.charrey.game.util.CollectionUtils;
import com.charrey.game.util.GridItem;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Class that houses the entire model that can be specified or simulated. This consists of a 2D grid with simulatable
 * elements in it that are located somewhere in the grid.
 */
public class Grid {

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

    private final Map<Direction, Set<Integer>> exports;
    private final Map<Direction, Set<Integer>> pads;
    private final Set<Simulatable> simulatables;
    private final Map<GridItem, Set<Simulatable>> map = new ConcurrentHashMap<>(); //only updated in stateChange
    private SubGrid parent = null;
    private int width;
    private int height;

    /**
     * Creates a new model with specified grid dimensions
     *
     * @param width  width of the grid
     * @param height height of the grid
     */
    public Grid(int width, int height) {
        this.width = width;
        this.height = height;
        this.simulatables = Collections.synchronizedSet(new HashSet<>());
        for (int columnIndex = 0; columnIndex < width; columnIndex++) {
            for (int rowIndex = 0; rowIndex < height; rowIndex++) {
                GridItem gridItem = new GridItem(columnIndex, rowIndex);
                map.put(gridItem, new CopyOnWriteArraySet<>());
            }
        }
        this.exports = new EnumMap<>(Direction.class);
        this.pads = new EnumMap<>(Direction.class);
        for (Direction direction : Direction.values()) {
            exports.put(direction, new HashSet<>());
            pads.put(direction, new HashSet<>());
        }
    }

    /**
     * Marks a specific edge of this grid as exported or not exported. For horizontal directions, the index
     * is vertical from bottom to top. For vertical directions, the index is horizontal from left to right
     *
     * @param index     index
     * @param direction direction of the edge
     * @param value     true if marked as exported, false if unmarked as exported
     */
    public void setExport(int index, Direction direction, boolean value) {
        if (value) {
            exports.get(direction).add(index);
        } else {
            exports.get(direction).remove(index);
        }
        assert CollectionUtils.separate(pads.get(direction), exports.get(direction));
    }

    /**
     * Marks a specific edge of this grid as padded or not padded. For horizontal directions, the index
     * is vertical from bottom to top. For vertical directions, the index is horizontal from left to right
     *
     * @param index     index
     * @param direction direction of the edge
     * @param value     true if marked as padded, false if unmarked as padded
     */
    public void setPad(int index, Direction direction, boolean value) {
        if (value) {
            pads.get(direction).add(index);
        } else {
            pads.get(direction).remove(index);
        }
        assert CollectionUtils.separate(pads.get(direction), exports.get(direction));
    }

    /**
     * Returns all indices of edges marked as Exported in a specific direction. For horizontal directions, the indices
     * are vertical from bottom to top. For vertical directions, the indices are horizontal from left to right
     *
     * @param direction direction of the edge
     * @return indices of edges on that direction that are marked exported
     */
    public Set<Integer> getExport(Direction direction) {
        return Collections.unmodifiableSet(exports.get(direction));
    }

    /**
     * Returns all indices of edges marked as Padded in a specific direction. For horizontal directions, the indices
     * are vertical from bottom to top. For vertical directions, the indices are horizontal from left to right
     *
     * @param direction direction of the edge
     * @return indices of edges on that direction that are marked padded
     */
    public Set<Integer> getPad(Direction direction) {
        assert CollectionUtils.separate(pads.get(direction), exports.get(direction));
        return Collections.unmodifiableSet(pads.get(direction));
    }

    /**
     * Returns the SubGrid that contains this Grid or null if this is the root grid
     *
     * @return the containing SubGrid simulatable
     */
    public SubGrid getParent() {
        return parent;
    }

    /**
     * Signifies that this grid is a subgrid of a SubGrid simulatable and specifies it.
     *
     * @param parent the SubGrid that contains this grid
     */
    public void setParent(SubGrid parent) {
        this.parent = parent;
    }

    /**
     * Become a semantic copy of a different model
     *
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
        for (Direction direction : Direction.values()) {
            exports.get(direction).clear();
            for (Integer index : other.getExport(direction)) {
                setExport(index, direction, true);
            }
            pads.get(direction).clear();
            for (Integer index : other.getPad(direction)) {
                setPad(index, direction, true);
            }
        }
    }

    /**
     * Adds a simulatable to this model
     *
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
     *
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
     *
     * @param simulatable simulatable to remove
     */
    public void remove(Simulatable simulatable) {
        simulatables.remove(simulatable);
    }

    /**
     * Returns a view of all simulatables at specific coordinates
     *
     * @param location coordinates
     * @return view of all simulatables at those coordinates
     */
    public Set<Simulatable> getAtStrictGridLocation(GridItem location) {
        return Collections.unmodifiableSet(Objects.requireNonNullElse(map.get(location), Collections.emptySet()));
    }

    /**
     * Returns a view of all simulatables at specific coordinates, correcting for coordinates outside the model
     *
     * @param location coordinates
     * @return view of all simulatables at those coordinates
     */
    public Set<Simulatable> getAtWrappedGridLocation(GridItem location) {
        return getAtStrictGridLocation(new GridItem(Math.floorMod(location.x(), width), Math.floorMod(location.y(), height)));
    }

    /**
     * Returns a Drawable (a method for drawing) for a gridItem not occupied by Simulatables
     *
     * @return the drawable
     */
    public Drawable getEmptyGridDrawable() {
        return emptyGridItem;
    }

    /**
     * Returns a view of the current simulatables in this model
     *
     * @return view of all simulatables
     */
    public Set<Simulatable> getSimulatables() {
        return Collections.unmodifiableSet(simulatables);
    }

    /**
     * Removes all simulatables from this model (essentially, go to the state immediately after calling the constructor)
     * and resizes the grid to new dimensions
     *
     * @param width  new width
     * @param height new height
     */
    public void clear(int width, int height) {
        this.width = width;
        this.height = height;
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
            for (GridItem location : simulatable.getLocations()) {
                location = new GridItem(Math.floorMod(location.x(), getWidth()), Math.floorMod(location.y(), getHeight()));
                map.computeIfAbsent(location, coords -> Collections.synchronizedSet(new HashSet<>()));
                Set<Simulatable> simulatablesAtLocation = map.get(location);
                if (simulatablesAtLocation.stream().anyMatch(x -> x != simulatable && x.equals(simulatable))) {
                    simulatables.remove(simulatable);
                } else {
                    simulatablesAtLocation.add(simulatable);
                }
            }
        }
    }

    /**
     * Returns the width of the grid of this model, i.e. how many 1-width simulatables fit next to each other.
     *
     * @return the width
     */
    public int getWidth() {
        return width;
    }

    /**
     * Returns the height of the grid of this model, i.e. how many 1-height simulatables fit on top of each other.
     *
     * @return the width
     */
    public int getHeight() {
        return height;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Grid with ").append(simulatables.size()).append(" simulatables").append("\n");
        for (int row = height - 1; row >= 0; row--) {
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


    /**
     * This method provides information on whether a particular location in the grid is at an edge (on a specifica face
     * of the location) and whether that face is marked as exported or padded.
     *
     * @param location  location in the grid
     * @param direction direction from that location
     * @return type of edge present
     */
    public EdgeType gridEdgeInDirection(GridItem location, Direction direction) {
        EdgeType ifAtEdge = EdgeType.UNMARKED;
        if (getExport(direction).contains(direction.isHorizontal() ? location.y() : location.x())) {
            ifAtEdge = EdgeType.EXPORT;
        } else if (getPad(direction).contains(direction.isHorizontal() ? location.y() : location.x())) {
            ifAtEdge = EdgeType.PAD;
        }
        return switch (direction) {
            case UP -> location.y() == getHeight() - 1 ? ifAtEdge : EdgeType.EMPTY;
            case DOWN -> location.y() == 0 ? ifAtEdge : EdgeType.EMPTY;
            case LEFT -> location.x() == 0 ? ifAtEdge : EdgeType.EMPTY;
            case RIGHT -> location.x() == getWidth() - 1 ? ifAtEdge : EdgeType.EMPTY;
        };
    }

    /**
     * This method provides information on whether a movement from a specific location in the grid in a given direction
     * of a simulatable with specific width and height would be blocked by a barrier, subgrid with padded edge or otherwise
     * blocking simulatable.
     *
     * @param fromLocation location in the grid moved from
     * @param direction    direction from that location
     * @param width        width of the requesting simulatable
     * @param height       height of the requesting simulatable
     * @return true iff movement would be blocked
     */
    public boolean blockedInDirection(GridItem fromLocation, Direction direction, int width, int height) {
        return switch (gridEdgeInDirection(fromLocation, direction)) {
            case EMPTY, PAD, UNMARKED -> {
                boolean blockedByBarrier = getInDirection(fromLocation, direction, width, height).stream().anyMatch(obj -> obj instanceof Barrier barrier && barrier.isBlocking());
                if (blockedByBarrier) {
                    yield true;
                }
                yield getInDirection(fromLocation, direction, width, height).stream().anyMatch(simulatable -> {
                    if (simulatable instanceof SubGrid subGrid) {
                        if (direction.isHorizontal()) {
                            return !subGrid.isInwardLinked(direction.opposite(), fromLocation.y() - subGrid.getLocation().y());
                        } else {
                            return !subGrid.isInwardLinked(direction.opposite(), fromLocation.x() - subGrid.getLocation().x());
                        }
                    } else {
                        return false;
                    }
                });
            }
            case EXPORT -> {
                SubGrid parent = getParent();
                if (parent == null) {
                    yield false;
                }
                GridItem linkedTo = parent.getOutwardLink(direction, direction.isHorizontal() ? fromLocation.y() : fromLocation.x());
                linkedTo = linkedTo.copyInDirection(direction.opposite());
                yield blockedInDirection(linkedTo, direction, width, height);
            }
        };
    }


    /**
     * Returns the set of simulatables that reside in a specific direction of a location (additive across its width/height)
     *
     * @param location  location
     * @param direction direction of which to request simulatables
     * @param width     width of the requesting simulatable
     * @param height    height of the requesting simulatable
     * @return a view of simulatables in that direction
     */
    public @NotNull Set<Simulatable> getInDirection(GridItem location, Direction direction, int width, int height) {
        Set<Simulatable> toReturn = new HashSet<>();
        switch (direction) {
            case UP -> {
                for (int simulatableColumn = location.x(); simulatableColumn < location.x() + width; simulatableColumn++) {
                    toReturn.addAll(getAtWrappedGridLocation(new GridItem(simulatableColumn, location.y() + height)));
                }
            }
            case DOWN -> {
                for (int simulatableColumn = location.x(); simulatableColumn < location.x() + width; simulatableColumn++) {
                    toReturn.addAll(getAtWrappedGridLocation(new GridItem(simulatableColumn, location.y() - 1)));
                }
            }
            case LEFT -> {
                for (int simulatableRow = location.y(); simulatableRow < location.y() + height; simulatableRow++) {
                    toReturn.addAll(getAtWrappedGridLocation(new GridItem(location.x() - 1, simulatableRow)));
                }
            }
            case RIGHT -> {
                for (int simulatableRow = location.y(); simulatableRow < location.y() + height; simulatableRow++) {
                    toReturn.addAll(getAtWrappedGridLocation(new GridItem(location.x() + width, simulatableRow)));
                }
            }
        }
        return Collections.unmodifiableSet(toReturn);
    }
}
