package de.dwslab.risk.gui;

import java.awt.Color;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.swing.util.mxGraphTransferable;
import com.mxgraph.swing.util.mxSwingConstants;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxResources;
import com.mxgraph.view.mxGraph;

import de.dwslab.risk.gui.jgraphx.BasicGraphEditor;
import de.dwslab.risk.gui.jgraphx.EditorMenuBar;
import de.dwslab.risk.gui.jgraphx.EditorPalette;
import de.dwslab.risk.gui.jgraphx.model.BackgroundKnowledge;
import de.dwslab.risk.gui.jgraphx.model.Grounding;

public class RoCA extends BasicGraphEditor {

    private static final long serialVersionUID = -4601740824088314699L;
    private static final Logger logger = LogManager.getLogger();

    /**
     * Holds the shared number formatter.
     *
     * @see NumberFormat#getInstance()
     */
    public static final NumberFormat numberFormat = NumberFormat.getInstance();

    public RoCA() {
        super("RoCA", new CustomGraphComponent(new CustomGraph()));
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            JOptionPane.showMessageDialog(RoCA.this, e.getMessage());
            logger.error("Unhandeled exception", e);
        });

        final mxGraph graph = graphComponent.getGraph();

        // Creates the shapes palette
        EditorPalette shapesPalette = insertPalette(mxResources.get("shapes"));

        // Sets the edge template to be used for creating new edges if an edge
        // is clicked in the shape palette
        shapesPalette.addListener(mxEvent.SELECT, (sender, evt) -> {
            Object tmp = evt.getProperty("transferable");

            if (tmp instanceof mxGraphTransferable) {
                mxGraphTransferable t = (mxGraphTransferable) tmp;
                Object cell = t.getCells()[0];

                if (graph.getModel().isEdge(cell)) {
                    ((CustomGraph) graph).setEdgeTemplate(cell);
                }
            }
        });

        // Adds some template cells for dropping into the graph
        shapesPalette.addTemplate(
                "Container",
                new ImageIcon(RoCA.class
                        .getResource("/com/mxgraph/examples/swing/images/swimlane.png")),
                "swimlane", 280, 280, "Container");
        shapesPalette.addTemplate(
                "Icon",
                new ImageIcon(RoCA.class
                        .getResource("/com/mxgraph/examples/swing/images/rounded.png")),
                "icon;image=/com/mxgraph/examples/swing/images/wrench.png", 70, 70, "Icon");
        shapesPalette.addTemplate(
                "Label",
                new ImageIcon(RoCA.class
                        .getResource("/com/mxgraph/examples/swing/images/rounded.png")),
                "label;image=/com/mxgraph/examples/swing/images/gear.png", 130, 50, "Label");
        shapesPalette.addTemplate(
                "Rectangle",
                new ImageIcon(RoCA.class
                        .getResource("/com/mxgraph/examples/swing/images/rectangle.png")), null,
                160, 120, "");
        shapesPalette.addTemplate(
                "Rounded Rectangle",
                new ImageIcon(RoCA.class
                        .getResource("/com/mxgraph/examples/swing/images/rounded.png")),
                "rounded=1", 160, 120, "");
        shapesPalette.addTemplate(
                "Double Rectangle",
                new ImageIcon(RoCA.class
                        .getResource("/com/mxgraph/examples/swing/images/doublerectangle.png")),
                "rectangle;shape=doubleRectangle", 160, 120, "");
        shapesPalette.addTemplate(
                "Ellipse",
                new ImageIcon(RoCA.class
                        .getResource("/com/mxgraph/examples/swing/images/ellipse.png")), "ellipse",
                160, 160, "");
        shapesPalette.addTemplate(
                "Double Ellipse",
                new ImageIcon(RoCA.class
                        .getResource("/com/mxgraph/examples/swing/images/doubleellipse.png")),
                "ellipse;shape=doubleEllipse", 160, 160, "");
        shapesPalette.addTemplate(
                "Triangle",
                new ImageIcon(RoCA.class
                        .getResource("/com/mxgraph/examples/swing/images/triangle.png")),
                "triangle", 120, 160, "");
        shapesPalette.addTemplate(
                "Rhombus",
                new ImageIcon(RoCA.class
                        .getResource("/com/mxgraph/examples/swing/images/rhombus.png")), "rhombus",
                160, 160, "");
        shapesPalette.addTemplate(
                "Horizontal Line",
                new ImageIcon(RoCA.class
                        .getResource("/com/mxgraph/examples/swing/images/hline.png")), "line", 160,
                10, "");
        shapesPalette.addTemplate(
                "Hexagon",
                new ImageIcon(RoCA.class
                        .getResource("/com/mxgraph/examples/swing/images/hexagon.png")),
                "shape=hexagon", 160, 120, "");
        shapesPalette.addTemplate(
                "Cylinder",
                new ImageIcon(RoCA.class
                        .getResource("/com/mxgraph/examples/swing/images/cylinder.png")),
                "shape=cylinder", 120, 160, "");
        shapesPalette.addTemplate(
                "Actor",
                new ImageIcon(RoCA.class
                        .getResource("/com/mxgraph/examples/swing/images/actor.png")),
                "shape=actor", 120, 160, "");
        shapesPalette.addTemplate(
                "Cloud",
                new ImageIcon(RoCA.class
                        .getResource("/com/mxgraph/examples/swing/images/cloud.png")),
                "ellipse;shape=cloud", 160, 120, "");

        shapesPalette.addEdgeTemplate(
                "Straight",
                new ImageIcon(RoCA.class
                        .getResource("/com/mxgraph/examples/swing/images/straight.png")),
                "straight", 120, 120, "");
        shapesPalette.addEdgeTemplate(
                "Horizontal Connector",
                new ImageIcon(RoCA.class
                        .getResource("/com/mxgraph/examples/swing/images/connect.png")), null, 100,
                100, "");
        shapesPalette.addEdgeTemplate(
                "Vertical Connector",
                new ImageIcon(RoCA.class
                        .getResource("/com/mxgraph/examples/swing/images/vertical.png")),
                "vertical", 100, 100, "");
        shapesPalette.addEdgeTemplate(
                "Entity Relation",
                new ImageIcon(RoCA.class
                        .getResource("/com/mxgraph/examples/swing/images/entity.png")), "entity",
                100, 100, "");
        shapesPalette.addEdgeTemplate(
                "Arrow",
                new ImageIcon(RoCA.class
                        .getResource("/com/mxgraph/examples/swing/images/arrow.png")), "arrow",
                120, 120, "");
    }

    public void handleKnowledgeUpdate(BackgroundKnowledge knowledge) {
        logger.debug("Updating background knowledge");
        mxGraph graph = graphComponent.getGraph();

        try {
            graph.getModel().beginUpdate();

            // clear the current graph
            graph.selectAll();
            graph.removeCells();

            // add the new entities
            Map<String, mxCell> cellMap = new HashMap<>();
            Set<String> infras = knowledge.getEntities().get("infra");
            for (String infra : infras) {
                mxCell cell = insertEntity(infra, graph);
                cellMap.put(infra, cell);
            }

            // connect the entities with edges
            Set<Grounding> dependsOns = knowledge.getGroundings().get("dependsOn");
            for (Grounding literal : dependsOns) {
                String source = literal.getValues().get(0);
                String target = literal.getValues().get(1);
                insertDependsOn(cellMap.get(source), cellMap.get(target), graph);
            }

            // add the risks
            Set<String> risks = knowledge.getEntities().get("risk");
            for (String risk : risks) {
                mxCell cell = insertRisk(risk, graph);
                cellMap.put(risk, cell);
            }

            Set<Grounding> hasRisks = knowledge.getGroundings().get("hasRiskDegree");
            for (Grounding literal : hasRisks) {
                String source = literal.getValues().get(0);
                String target = literal.getValues().get(1);
                String weightStr = literal.getValues().get(2);
                double weight = Double.parseDouble(weightStr);
                insertHasRisk(cellMap.get(source), cellMap.get(target), weight, graph);
            }

            Set<Grounding> offlines = knowledge.getGroundings().get("offline");
            for (Grounding literal : offlines) {
                // TODO if predicate is negated ... literal.getPredicate()
                String infra = literal.getValues().get(0);
                mxCell cell = cellMap.get(infra);
                graph.getModel().setStyle(cell, "fillColor=#FF2222");
            }
        } finally {
            graph.getModel().endUpdate();
        }

        logger.info("Executing layout");
        mxHierarchicalLayout layout = new mxHierarchicalLayout(graph);
        try {
            graph.getModel().beginUpdate();
            layout.execute(graph.getDefaultParent());
        } finally {
            graph.getModel().endUpdate();
            graph.repaint();
        }
    }

    private mxCell insertEntity(String entity, mxGraph graph) {
        Object parent = graph.getDefaultParent();
        int x = 100;
        int y = 100;
        int width = 160;
        int height = 120;
        return (mxCell) graph.insertVertex(parent, entity, entity, x, y, width, height);
    }

    private mxCell insertRisk(String risk, mxGraph graph) {
        Object parent = graph.getDefaultParent();
        int x = 100;
        int y = 100;
        int width = 160;
        int height = 120;
        return (mxCell) graph.insertVertex(parent, risk, risk, x, y, width, height, "rounded=1");
    }

    private mxCell insertDependsOn(mxCell source, mxCell target, mxGraph graph) {
        Object parent = graph.getDefaultParent();
        String id = "dependsOn(" + source.getValue() + "," + target.getValue() + ")";
        Object value = "dependsOn";
        return (mxCell) graph.insertEdge(parent, id, value, source, target);
    }

    private mxCell insertHasRisk(mxCell source, mxCell target, double weight, mxGraph graph) {
        Object parent = graph.getDefaultParent();
        String id = "hasRisk(" + source.getValue() + "," + target.getValue() + ")";
        Object value = "hasRisk: " + weight;
        return (mxCell) graph.insertEdge(parent, id, value, source, target);
    }

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        mxSwingConstants.SHADOW_COLOR = Color.LIGHT_GRAY;
        mxConstants.W3C_SHADOWCOLOR = "#D3D3D3";

        RoCA editor = new RoCA();
        editor.createFrame(new EditorMenuBar(editor)).setVisible(true);
    }

}
