package com.charrey.game.model.simulatable.subgrid;

import com.charrey.game.model.Direction;
import com.charrey.game.model.Grid;
import com.charrey.game.model.simulatable.EdgeType;
import com.charrey.game.model.simulatable.Simulatable;
import com.charrey.game.texture.Drawable;
import com.charrey.game.util.GridItem;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.nio.file.Path;
import java.util.*;

import static com.charrey.game.model.Direction.*;

/**
 * A simulatable that emulates an entire other grid whilst being smaller than the grid it simulates. Each edge of the contained
 * grid marked as padded or exported corresponds to an edge of this rectangular simulatable, and these edges are physically linked.
 */
public class SubGrid extends Simulatable {

    private final Grid subgrid;

    private final Map<Direction, List<? extends Link>> inwardLinks; //from down to up and from left to right
    private final Map<Direction, List<GridItem>> outwardLinks; //from down to up and from left to right
    private final Path location;


    /**
     * Creates a new Subgrid
     *
     * @param gridItem location
     * @param subgrid  subgrid that this simulatable contains
     * @param path     file path to the file describing the subgrid (so that this simulatable can be properly serialized)
     */
    public SubGrid(GridItem gridItem, Grid subgrid, Path path) {
        super(gridItem, 100, subgrid.getExport(UP).size() + subgrid.getPad(UP).size(), subgrid.getExport(LEFT).size() + subgrid.getPad(LEFT).size());
        this.location = path;
        this.subgrid = subgrid;
        this.subgrid.setParent(this);
        this.inwardLinks = new EnumMap<>(Direction.class);
        this.outwardLinks = new EnumMap<>(Direction.class);
        for (Direction direction : Direction.values()) {
            List<ImmutablePair<? extends Link, Integer>> edges = new ArrayList<>(subgrid.getExport(direction).stream().map(index -> switch (direction) {
                case UP -> new ImmutablePair<>(new ExportedLink(new GridItem(index, subgrid.getHeight() - 1)), index);
                case DOWN -> new ImmutablePair<>(new ExportedLink(new GridItem(index, 0)), index);
                case LEFT -> new ImmutablePair<>(new ExportedLink(new GridItem(0, index)), index);
                case RIGHT -> new ImmutablePair<>(new ExportedLink(new GridItem(subgrid.getWidth() - 1, index)), index);
            }).toList());
            edges.addAll(subgrid.getPad(direction).stream().map(index -> new ImmutablePair<>(new PaddedLink(), index)).toList());
            edges.sort(Comparator.comparingInt(p -> p.right));
            inwardLinks.put(direction, edges.stream().map(pair -> pair.left).toList());
        }
        Arrays.stream(Direction.values()).forEach(direction -> outwardLinks.put(direction, new ArrayList<>()));
        int compactOffset = 0;
        for (int x = 0; x < subgrid.getWidth(); x++) {
            outwardLinks.get(UP).add(subgrid.getExport(UP).contains(x) ? new GridItem(getLocation().x() + compactOffset, getLocation().y() + getHeight()) : null);
            if (subgrid.getExport(UP).contains(x) || subgrid.getPad(UP).contains(x)) {
                compactOffset++;
            }
        }
        compactOffset = 0;
        for (int x = 0; x < subgrid.getWidth(); x++) {
            outwardLinks.get(DOWN).add(subgrid.getExport(DOWN).contains(x) ? new GridItem(getLocation().x() + compactOffset, getLocation().y() - 1) : null);
            if (subgrid.getExport(UP).contains(x) || subgrid.getPad(UP).contains(x)) {
                compactOffset++;
            }
        }
        compactOffset = 0;
        for (int y = 0; y < subgrid.getHeight(); y++) {
            outwardLinks.get(LEFT).add(subgrid.getExport(LEFT).contains(y) ? new GridItem(getLocation().x() - 1, getLocation().y() + compactOffset) : null);
            if (subgrid.getExport(LEFT).contains(y) || subgrid.getPad(LEFT).contains(y)) {
                compactOffset++;
            }
        }
        compactOffset = 0;
        for (int y = 0; y < subgrid.getHeight(); y++) {
            outwardLinks.get(RIGHT).add(subgrid.getExport(RIGHT).contains(y) ? new GridItem(getLocation().x() + getWidth(), getLocation().y() + compactOffset) : null);
            if (subgrid.getExport(RIGHT).contains(y) || subgrid.getPad(RIGHT).contains(y)) {
                compactOffset++;
            }
        }
    }

    /**
     * Returns whether a specific edge on the SubGrid (as simulatable) corresponds to an exported edge of the grid that
     * it contains. If it corresponds to an exported edge, this returns true. If it corresponds to a padded edge, this
     * returns false. Note that these are the only two possibilities since any other edge is abstracted away.
     *
     * @param direction Direction of the edge of this simulatable
     * @param index     Index of the edge. For the upper- and lower edge this is a horizontal index from left to right.
     *                  For the left and right edge this is a vertical index from bottom to top.
     * @return whether the edge corresponds to an exported edge.
     */
    public boolean isInwardLinked(Direction direction, int index) {
        return inwardLinks.get(direction).get(index) instanceof ExportedLink;
    }

    /**
     * Returns the location in the subgrid that a moving simulatable would end up when entering the subgrid from a
     * specific direction into a specific edge of this simulatable
     *
     * @param direction       Direction of the edge of this simulatable the subgrid is approached from
     * @param horizontalIndex Horizontal index of the edge being entered (from left to right). Only applicable for vertical directions.
     * @param verticalIndex   Vertical index of the edge being entered (from bottom to top). Only applicable for horizontal directions.
     * @return Location within the subgrid that the simulatable would end up.
     * @throws NoLinkException Thrown when the edge has no link, i.e. it corresponds to an inner padded edge instead of
     *                         an exported one.
     */
    public GridItem getInwardLink(Direction direction, int horizontalIndex, int verticalIndex) throws NoLinkException {
        if (!direction.isHorizontal()) {
            return getInwardLink(direction, horizontalIndex);
        } else {
            return getInwardLink(direction, verticalIndex);
        }
    }

    /**
     * Returns the location in the subgrid that a moving simulatable would end up when entering the subgrid from a
     * specific direction into a specific edge of this simulatable
     *
     * @param direction Direction of the edge of this simulatable the subgrid is approached from
     * @param index     Index of the edge. For the upper- and lower edge this is a horizontal index from left to right.
     *                  For the left and right edge this is a vertical index from bottom to top.
     * @return Location within the subgrid that the simulatable would end up.
     * @throws NoLinkException Thrown when the edge has no link, i.e. it corresponds to an inner padded edge instead of
     *                         an exported one.
     */
    public GridItem getInwardLink(Direction direction, int index) throws NoLinkException {
        if (!(inwardLinks.get(direction).get(index) instanceof ExportedLink)) {
            throw new NoLinkException();
        }
        return ((ExportedLink) inwardLinks.get(direction).get(index)).getLocation();
    }

    /**
     * Returns the location in the container grid that a moving simulatable would end up when exiting the subgrid from a
     * specific direction into a specific edge of the subgrid
     *
     * @param direction Direction of the edge of the subgrid that is approached
     * @param index     Index of the edge from the subgrid's perspective. For the upper- and lower edge this is a horizontal index from left to right.
     *                  For the left and right edge this is a vertical index from bottom to top.
     * @return Location in the container subgrid that the simulatable would end up.
     */
    public GridItem getOutwardLink(Direction direction, int index) {
        return outwardLinks.get(direction).get(index);
    }

    @Override
    public void simulateStep() {
        //nothing. This simulatable is static.
    }

    @Override
    public Drawable getTexture(int xOffset, int yOffset, int textureWidth, int textureHeight) {
        EdgeType topType;
        if (yOffset < getHeight() - 1) {
            topType = EdgeType.EMPTY;
        } else {
            topType = inwardLinks.get(UP).get(xOffset) instanceof ExportedLink ? EdgeType.EXPORT : EdgeType.PAD;
        }
        EdgeType rightType;
        if (xOffset < getWidth() - 1) {
            rightType = EdgeType.EMPTY;
        } else {
            rightType = inwardLinks.get(RIGHT).get(yOffset) instanceof ExportedLink ? EdgeType.EXPORT : EdgeType.PAD;
        }
        EdgeType bottomType;
        if (yOffset > 0) {
            bottomType = EdgeType.EMPTY;
        } else {
            bottomType = inwardLinks.get(DOWN).get(xOffset) instanceof ExportedLink ? EdgeType.EXPORT : EdgeType.PAD;
        }
        EdgeType leftType;
        if (xOffset > 0) {
            leftType = EdgeType.EMPTY;
        } else {
            leftType = inwardLinks.get(LEFT).get(yOffset) instanceof ExportedLink ? EdgeType.EXPORT : EdgeType.PAD;
        }
        return SubGridTexture.getTexture(topType, rightType, bottomType, leftType);
    }

    @Override
    public Simulatable copy() {
        Grid gridCopy = new Grid(subgrid.getWidth(), subgrid.getHeight());
        gridCopy.copy(subgrid);
        return new SubGrid(getLocation(), gridCopy, getPath());
    }

    @Override
    public String shortName() {
        return "SG";
    }

    /**
     * Returns the grid that this subgrid contains
     *
     * @return the grid
     */
    public Grid getSubgrid() {
        return subgrid;
    }

    /**
     * Returns the system file path of the file that describes this simulatable. This is stored so that any grid
     * that contains this subgrid can be saved and loaded again without storing all subgrid information.
     *
     * @return The file system path of the save file of the subgrid
     */
    public Path getPath() {
        return location;
    }

    private static abstract class Link {
    }

    private static class PaddedLink extends Link {
    }

    private static class ExportedLink extends Link {
        private final GridItem location;

        public ExportedLink(GridItem location) {
            this.location = location;
        }

        public GridItem getLocation() {
            return location;
        }
    }

    /**
     * Thrown when a link is queried while no such link exists.
     */
    public static class NoLinkException extends Throwable {
    }
}
