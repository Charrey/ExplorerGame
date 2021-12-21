package com.charrey.game.model.serialize;

import com.charrey.game.model.Direction;
import com.charrey.game.model.Grid;
import com.charrey.game.model.condition.*;
import com.charrey.game.model.simulatable.*;
import com.charrey.game.util.GridItem;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;

/**
 * Loads a model from an XML format.
 */
public class XMLLoader implements GridLoader{

    private static final XMLLoader instance = new XMLLoader();

    /**
     * Returns an XML loader
     * @return loader
     */
    public static XMLLoader get() {
        return instance;
    }

    @Override
    public Grid load(String serialized) throws SaveFormatException {
        SAXReader reader = new SAXReader();
        try {
            Document document = reader.read(new ByteArrayInputStream(serialized.getBytes()));
            Element root = document.getRootElement();
            Grid grid = new Grid(getRequiredIntegerAttribute(root, "width"), getRequiredIntegerAttribute(root, "height"));
            for (Element element : root.elements()) {
                Simulatable simulatable = loadSimulatable(grid, element);
                grid.add(simulatable);
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

    private @NotNull Simulatable loadSimulatable(Grid grid, Element element) throws SaveFormatException {
        int x = getRequiredIntegerAttribute(element, "x");
        int y = getRequiredIntegerAttribute(element, "y");
        return switch (element.getName()) {
            case "DefaultBarrier" -> new DefaultBarrier(new GridItem(x, y));
            case "SplitExplorer" ->  new SplitExplorer(getDirectionOf(element), new GridItem(x, y));
            case "ConditionalBarrier" -> new ConditionalBarrier(new GridItem(x, y), getConditionOf(grid, element));
            case "RandomExplorer" -> new RandomExplorer(getDirectionOf(element), new GridItem(x, y));
            default -> throw new UnsupportedOperationException();
        };
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
            throw new SaveFormatException("Required attribute " + childName + " missing for element " + element.getName(), null);
        }
        return child;
    }
}
