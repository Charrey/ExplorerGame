package com.charrey.game.model.serialize;

import com.charrey.game.model.Grid;
import com.charrey.game.model.condition.Condition;
import com.charrey.game.model.condition.ConditionWithReference;
import com.charrey.game.model.simulatable.ConditionalBarrier;
import com.charrey.game.model.simulatable.DirectionalSimulatable;
import com.charrey.game.model.simulatable.Simulatable;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.IOException;
import java.io.StringWriter;
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
     * @return serializer
     */
    public static XMLSerializer get() {
        return instance;
    }

    @Override
    public String serialize(Grid grid) {
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement("Grid");
        root.addAttribute("width", String.valueOf(grid.getWidth()));
        root.addAttribute("height", String.valueOf(grid.getHeight()));
        List<Simulatable> simulatables = new ArrayList<>(grid.getSimulatables());
        simulatables.sort(Comparator.comparing(s -> ((Simulatable) s).getLocation()).thenComparingInt(s -> ((Simulatable) s).getRenderPriority()));
        simulatables.forEach(simulatable -> {
            Element representation = serialize(simulatable);
            root.add(representation);
        });

        OutputFormat format = OutputFormat.createPrettyPrint();
        StringWriter res = new StringWriter();
        try {
            XMLWriter writer = new XMLWriter(res, format);
            writer.write(document);
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

    private Element serialize(Simulatable simulatable) {
        Element res = DocumentHelper.createElement(simulatable.getClass().getSimpleName());
        res.addAttribute("x", String.valueOf(simulatable.getLocation().x()));
        res.addAttribute("y", String.valueOf(simulatable.getLocation().y()));
        return switch (simulatable.getClass().getSimpleName()) {
            case "SplitExplorer", "RandomExplorer" -> serialize(res, (DirectionalSimulatable) simulatable);
            case "ConditionalBarrier" -> serialize(res, (ConditionalBarrier) simulatable);
            case "DefaultBarrier" -> res;
            default -> throw new UnsupportedOperationException(simulatable.getClass().getSimpleName());
        };
    }
}
