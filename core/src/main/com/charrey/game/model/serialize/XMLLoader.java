package com.charrey.game.model.serialize;

import com.charrey.game.model.*;
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
            String width = getRequiredAttribute(root, "width");
            String height = getRequiredAttribute(root, "height");
            Grid grid;
            try {
                grid = new Grid(Integer.parseInt(width), Integer.parseInt(height));
            } catch (NumberFormatException e) {
                throw new SaveFormatException("Width or height of grid could not be parsed as an integer.", e);
            }
            for (Element element : root.elements()) {
                Simulatable simulatable = loadSimulatable(element);
                grid.add(simulatable);
            }
            return grid;
        } catch (DocumentException e) {
            throw new SaveFormatException(e.getMessage(), e);
        }
    }

    private @NotNull Simulatable loadSimulatable(Element element) throws SaveFormatException {
        int x = Integer.parseInt(getRequiredAttribute(element, "x"));
        int y = Integer.parseInt(getRequiredAttribute(element, "y"));
        return switch (element.getName()) {
            case "Barrier" -> new Barrier(new GridItem(x, y));
            case "SplitExplorer" -> {
                Direction direction;
                try {
                    direction = Direction.valueOf(getRequiredChild(element, "direction").getText());
                } catch (IllegalArgumentException e) {
                    throw new SaveFormatException("Unknown direction specified for split explorer.", e);
                }
                yield new SplitExplorer(direction, new GridItem(x, y));
            }
            default -> throw new UnsupportedOperationException(element.getName());
        };
    }

    private String getRequiredAttribute(Element element, String attributeName) throws SaveFormatException {
        Attribute attribute = element.attribute(attributeName);
        if (attribute == null) {
            throw new SaveFormatException("Required attribute " + attributeName + " missing for element " + element.getName(), null);
        }
        return attribute.getValue();
    }

    private Element getRequiredChild(Element element, String childName) throws SaveFormatException {
        Element child = element.element(childName);
        if (child == null) {
            throw new SaveFormatException("Required attribute " + childName + " missing for element " + element.getName(), null);
        }
        return child;
    }
}
