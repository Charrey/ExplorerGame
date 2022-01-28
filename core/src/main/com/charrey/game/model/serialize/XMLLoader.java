package com.charrey.game.model.serialize;

import com.badlogic.gdx.files.FileHandle;
import com.charrey.game.model.Direction;
import com.charrey.game.model.Grid;
import com.charrey.game.model.condition.*;
import com.charrey.game.model.simulatable.*;
import com.charrey.game.model.simulatable.subgrid.SubGrid;
import com.charrey.game.util.GridItem;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.nio.file.Path;

/**
 * Loads a model from an XML format.
 */
public class XMLLoader extends GridLoader {


    @Override
    public Grid load(FileHandle fileBeingLoaded) throws SaveFormatException {
        SAXReader reader = new SAXReader();
        try {
            Document document = reader.read(new ByteArrayInputStream(fileBeingLoaded.readBytes()));
            Element root = document.getRootElement();
            Grid grid = new Grid(getRequiredIntegerAttribute(root, "width"), getRequiredIntegerAttribute(root, "height"));
            Element simulatablesContainer = getRequiredChild(root, "simulatables");
            for (Element element : simulatablesContainer.elements()) {
                Simulatable simulatable = loadSimulatable(fileBeingLoaded.file().toPath(), grid, element);
                grid.add(simulatable);
            }
            Element exportContainer = getRequiredChild(root, "exports");
            Element padsContainer = getRequiredChild(root, "pads");
            for (Direction direction : Direction.values()) {
                Element directionElement = getRequiredChild(exportContainer, direction.name());
                String text = directionElement.getText();
                if (text.length() > 0) {
                    String[] splitted = text.split(" ");
                    for (String exportIndexString : splitted) {
                        try {
                            grid.setExport(Integer.parseInt(exportIndexString), direction, true);
                        } catch (NumberFormatException e) {
                            throw new SaveFormatException("Export should consist of only numbers, but direction " + direction + " has a non-number \"" + exportIndexString + "\" in it.", e);
                        }
                    }
                }
                directionElement = getRequiredChild(padsContainer, direction.name());
                text = directionElement.getText();
                if (text.length() > 0) {
                    String[] splitted = text.split(" ");
                    for (String exportIndexString : splitted) {
                        try {
                            grid.setPad(Integer.parseInt(exportIndexString), direction, true);
                        } catch (NumberFormatException e) {
                            throw new SaveFormatException("Pads should consist of only numbers, but direction " + direction + " has a non-number \"" + exportIndexString + "\" in it.", e);
                        }
                    }
                }
            }
            return grid;
        } catch (DocumentException e) {
            throw new SaveFormatException(e.getMessage(), e);
        }
    }

    private Condition getConditionOf(Grid grid, Element element) throws SaveFormatException {
        Element conditionElement = getRequiredChild(element, "condition");
        String type = getRequiredAttribute(conditionElement, "type");
        switch (type) {
            case "True":
                return new True();
            case "False":
                return new False();
        }
        GridItem conditionLocation = new GridItem(getRequiredIntegerAttribute(element, "x"), getRequiredIntegerAttribute(element, "y"));
        switch (type) {
            case "BlockExists":
                return new BlockExists(grid, conditionLocation);
            case "NotBlockExists":
                return new NotBlockExists(grid, conditionLocation);
        }
        throw new UnsupportedOperationException(type);
    }

    private Direction getDirectionOf(Element parent) throws SaveFormatException {
        try {
            return Direction.valueOf(getRequiredChild(parent, "direction").getText());
        } catch (IllegalArgumentException e) {
            throw new SaveFormatException("Unknown direction specified for split explorer.", e);
        }
    }

    private @NotNull Simulatable loadSimulatable(Path fileBeingLoaded, Grid grid, Element element) throws SaveFormatException {
        int x = getRequiredIntegerAttribute(element, "x");
        int y = getRequiredIntegerAttribute(element, "y");
        return switch (element.getName()) {
            case "DefaultBarrier" -> DefaultBarrier.factory().makeSimulatable(new GridItem(x, y));
            case "SplitExplorer" -> SplitExplorer.factory(getDirectionOf(element)).makeSimulatable(new GridItem(x, y));
            case "ConditionalBarrier" -> {
                ConditionalBarrier res = ConditionalBarrier.factory().makeSimulatable(new GridItem(x, y));
                res.setCondition(getConditionOf(grid, element));
                yield res;
            }
            case "RandomExplorer" -> RandomExplorer.factory(getDirectionOf(element)).makeSimulatable(new GridItem(x, y));
            case "SubGrid" -> loadSubGrid(fileBeingLoaded, element, new GridItem(x, y));
            default -> throw new UnsupportedOperationException(element.getName());
        };
    }

    private @NotNull SubGrid loadSubGrid(Path fileBeingLoaded, Element XMLElement, GridItem gridItem) throws SaveFormatException {
        FileHandle subGridFile = new FileHandle(fileBeingLoaded.getParent().resolve(getRequiredChild(XMLElement, "uri").getText()).toFile());
        Grid subgrid = load(subGridFile);
        return new SubGrid(gridItem, subgrid, subGridFile.file().toPath());
    }


    private String getRequiredAttribute(Element element, String attributeName) throws SaveFormatException {
        Attribute attribute = element.attribute(attributeName);
        if (attribute == null) {
            throw new SaveFormatException("Required attribute " + attributeName + " missing for element " + element.getName(), null);
        }
        return attribute.getValue();
    }

    private int getRequiredIntegerAttribute(Element element, String attributeName) throws SaveFormatException {
        Attribute attribute = element.attribute(attributeName);
        if (attribute == null) {
            throw new SaveFormatException("Required attribute " + attributeName + " missing for element " + element.getName(), null);
        }
        try {
            return Integer.parseInt(attribute.getValue());
        } catch (NumberFormatException e) {
            throw new SaveFormatException("Attribute " + attributeName + " must be an integer but could not be parsed as such.", e);
        }
    }

    private Element getRequiredChild(Element element, String childName) throws SaveFormatException {
        Element child = element.element(childName);
        if (child == null) {
            throw new SaveFormatException("Required child " + childName + " missing for element " + element.getName(), null);
        }
        return child;
    }
}
