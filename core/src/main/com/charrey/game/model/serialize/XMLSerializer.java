package com.charrey.game.model.serialize;

import com.charrey.game.model.Direction;
import com.charrey.game.model.Grid;
import com.charrey.game.model.condition.Condition;
import com.charrey.game.model.condition.ConditionWithReference;
import com.charrey.game.model.simulatable.ConditionalBarrier;
import com.charrey.game.model.simulatable.DirectionalSimulatable;
import com.charrey.game.model.simulatable.Simulatable;
import com.charrey.game.model.simulatable.subgrid.SubGrid;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Serializes a model to an XML format.
 */
public class XMLSerializer implements GridSerializer {

    private static final XMLSerializer instance = new XMLSerializer();

    /**
     * Returns an XML serializer
     *
     * @return serializer
     */
    public static XMLSerializer get() {
        return instance;
    }

    /**
     * Serializes griddata to an XML document
     *
     * @param grid grid to serialize
     * @param uri  file location where the file will be saved to (used to generate relative path for inter-grid references)
     * @return XML representation
     */
    public Document serializeToDocument(Grid grid, URI uri) {
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement("Grid");
        root.addAttribute("width", String.valueOf(grid.getWidth()));
        root.addAttribute("height", String.valueOf(grid.getHeight()));

        Element simulatablesElement = root.addElement("simulatables");

        List<Simulatable> simulatables = new ArrayList<>(grid.getSimulatables());
        simulatables.sort(Comparator.comparing(s -> ((Simulatable) s).getLocation()).thenComparingInt(s -> ((Simulatable) s).getRenderPriority()));
        simulatables.forEach(simulatable -> {
            Element representation = serialize(simulatable, uri);
            simulatablesElement.add(representation);
        });

        Element exportsElement = root.addElement("exports");
        Element padsElement = root.addElement("pads");
        for (Direction direction : Direction.values()) {
            exportsElement.addElement(direction.name()).addText(String.join(" ", grid.getExport(direction).stream().sorted().map(String::valueOf).toList()));
            padsElement.addElement(direction.name()).addText(String.join(" ", grid.getPad(direction).stream().sorted().map(String::valueOf).toList()));
        }
        return document;
    }

    @Override
    public String serializeToString(Grid grid, URI targetLocation) {
        Document serializedDocument = serializeToDocument(grid, targetLocation);
        OutputFormat format = OutputFormat.createPrettyPrint();
        StringWriter res = new StringWriter();
        try {
            XMLWriter writer = new XMLWriter(res, format);
            writer.write(serializedDocument);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res.toString();
    }

    private Element serialize(Element res, DirectionalSimulatable explorer) {
        Element direction = DocumentHelper.createElement("direction");
        direction.add(DocumentHelper.createText(String.valueOf(explorer.getDirection())));
        res.add(direction);
        return res;
    }

    private Element serialize(Element res, ConditionalBarrier barrier) {
        Condition condition = barrier.getCondition();
        Element conditionElement = DocumentHelper.createElement("condition");
        conditionElement.addAttribute("type", condition.getClass().getSimpleName());
        if (condition instanceof ConditionWithReference conditionWithReference) {
            conditionElement.addAttribute("x", String.valueOf(conditionWithReference.getLocation().x()));
            conditionElement.addAttribute("y", String.valueOf(conditionWithReference.getLocation().y()));
        }
        res.add(conditionElement);
        return res;
    }

    private Element serialize(Element res, SubGrid subGrid, URI uri) {
        Path targetFolder = Paths.get(uri).getParent();
        Path subgridLocation = subGrid.getPath();
        Element uriElement = DocumentHelper.createElement("uri");
        uriElement.addText(targetFolder.relativize(subgridLocation).toString());
        res.add(uriElement);
        return res;
    }

    private Element serialize(Simulatable simulatable, URI uri) {
        Element res = DocumentHelper.createElement(simulatable.getClass().getSimpleName());
        res.addAttribute("x", String.valueOf(simulatable.getLocation().x()));
        res.addAttribute("y", String.valueOf(simulatable.getLocation().y()));
        return switch (simulatable.getClass().getSimpleName()) {
            case "SplitExplorer", "RandomExplorer" -> serialize(res, (DirectionalSimulatable) simulatable);
            case "ConditionalBarrier" -> serialize(res, (ConditionalBarrier) simulatable);
            case "SubGrid" -> serialize(res, (SubGrid) simulatable, uri);
            case "DefaultBarrier" -> res;
            default -> throw new UnsupportedOperationException(simulatable.getClass().getSimpleName());
        };
    }
}
