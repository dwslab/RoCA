package de.dwslab.risk.gui;

import java.awt.Color;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.HashMultimap;
import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.swing.util.mxSwingConstants;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxResources;
import com.mxgraph.view.mxGraph;

import de.dwslab.risk.gui.exception.RoCAException;
import de.dwslab.risk.gui.jgraphx.BasicGraphEditor;
import de.dwslab.risk.gui.jgraphx.EditorMenuBar;
import de.dwslab.risk.gui.jgraphx.EditorPalette;
import de.dwslab.risk.gui.model.BackgroundKnowledge;
import de.dwslab.risk.gui.model.Entity;
import de.dwslab.risk.gui.model.Grounding;
import de.dwslab.risk.gui.model.GuiBackgroundKnowledge;
import de.dwslab.risk.gui.model.Predicate;
import de.dwslab.risk.gui.model.Type;

public class RoCA extends BasicGraphEditor {

    private static final long serialVersionUID = -4601740824088314699L;
    private static final Logger logger = LogManager.getLogger();

    private BackgroundKnowledge knowledge;

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
        graph.addListener(
                mxEvent.CELLS_ADDED,
                (sender, event) -> {
                    System.err.println(sender);
                    Object[] cells = (Object[]) event.getProperties().get("cells");
                    for (Object obj : cells) {
                        mxCell cell = (mxCell) obj;
                        if (cell.isVertex()) {
                            Entity entity = (Entity) cell.getValue();
                            cell.setValue(new Entity(entity.getName(), entity.getType()));
                        } else {
                            Entity source = (Entity) cell.getSource().getValue();
                            Entity target = (Entity) cell.getTarget().getValue();
                            String sourceName = source.getType().getName();
                            String targetName = target.getType().getName();
                            if (targetName.equals("risk") && sourceName.equals("infra")) {
                                Grounding grounding = new Grounding(new Predicate("hasRiskDegree"),
                                        Arrays.asList(source.getName(), target.getName(),
                                                String.valueOf(0d)));
                                cell.setValue(grounding);
                            } else if (targetName.equals("infra") && sourceName.equals("infra")) {
                                Grounding grounding = new Grounding(new Predicate("dependsOn"),
                                        Arrays.asList(source.getName(), target.getName()));
                                cell.setValue(grounding);
                            } else {
                                throw new RoCAException("Unknown edge type: "
                                        + source.getType().getName());
                            }
                        }
                    }
                });

        // set default edge template and insert grounding

        // Creates the shapes palette
        EditorPalette shapesPalette = insertPalette(mxResources.get("shapes"));

        // Adds some template cells for dropping into the graph
        shapesPalette.addTemplate("Component",
                new ImageIcon(RoCA.class
                        .getResource("/com/mxgraph/examples/swing/images/rectangle.png")),
                null, 160, 120, new Entity("New Component", new Type("infra")));
        // shapesPalette.addTemplate("Service",
        // new ImageIcon(RoCA.class
        // .getResource("/com/mxgraph/examples/swing/images/rounded.png")),
        // "rounded=1", 160, 120, new Entity("", new Type("infra")));
        shapesPalette.addTemplate("Risk",
                new ImageIcon(RoCA.class
                        .getResource("/com/mxgraph/examples/swing/images/ellipse.png")),
                "ellipse", 160, 120, new Entity("New Risk", new Type("risk")));
    }

    public BackgroundKnowledge getBackgroundKnowledge() {
        return new GuiBackgroundKnowledge(graphComponent.getGraph(), knowledge);
    }

    public void handleKnowledgeUpdate(BackgroundKnowledge knowledge) {
        logger.debug("Updating background knowledge");
        this.knowledge = knowledge;
        mxGraph graph = graphComponent.getGraph();
        try {
            graph.setEventsEnabled(false);
            graph.getModel().beginUpdate();

            // clear the current graph
            graph.selectAll();
            graph.removeCells();

            // add the new entities
            Map<String, mxCell> cellMap = new HashMap<>();
            Set<Entity> infras = knowledge.getEntities().get(new Type("infra"));
            for (Entity infra : infras) {
                mxCell cell = insertEntity(infra, graph);
                cellMap.put(infra.getName(), cell);
            }

            // add the risks
            Set<Entity> risks = knowledge.getEntities().get(new Type("risk"));
            for (Entity risk : risks) {
                mxCell cell = insertRisk(risk, graph);
                cellMap.put(risk.getName(), cell);
            }

            HashMultimap<Predicate, Grounding> groundings = knowledge.getGroundings();

            // connect the entities with edges
            Set<Grounding> dependsOns = groundings.get(new Predicate("dependsOn"));
            for (Grounding literal : dependsOns) {
                String source = literal.getValues().get(0);
                String target = literal.getValues().get(1);
                insertDependsOn(literal, cellMap.get(source), cellMap.get(target), graph);
            }

            Set<Grounding> hasRisks = groundings.get(new Predicate("hasRiskDegree"));
            for (Grounding literal : hasRisks) {
                String source = literal.getValues().get(0);
                String target = literal.getValues().get(1);
                insertHasRisk(literal, cellMap.get(source), cellMap.get(target), graph);
            }

            Set<Grounding> offlines = groundings.get(new Predicate("offline"));
            for (Grounding literal : offlines) {
                String infra = literal.getValues().get(0);
                mxCell cell = cellMap.get(infra);

                Entity value = (Entity) cell.getValue();
                knowledge.getEntities().remove(value.getType(), cell.getValue());
                value.setOffline(Boolean.TRUE);
                knowledge.getEntities().put(value.getType(), ((Entity) cell.getValue()));

                graph.getModel().setStyle(cell, "fillColor=#FF2222");
            }

            Set<Grounding> notOfflines = groundings.get(new Predicate(true, "offline"));
            for (Grounding literal : notOfflines) {
                String infra = literal.getValues().get(0);
                mxCell cell = cellMap.get(infra);

                Entity value = (Entity) cell.getValue();
                knowledge.getEntities().remove(value.getType(), cell.getValue());
                value.setOffline(Boolean.FALSE);
                knowledge.getEntities().put(value.getType(), ((Entity) cell.getValue()));

                graph.getModel().setStyle(cell, "fillColor=#22FF22");
            }
        } finally {
            graph.setEventsEnabled(true);
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

    private mxCell insertEntity(Entity entity, mxGraph graph) {
        Object parent = graph.getDefaultParent();
        int x = 100;
        int y = 100;
        int width = 160;
        int height = 120;
        return (mxCell) graph.insertVertex(parent, entity.getName(), entity, x, y, width, height);
    }

    private mxCell insertRisk(Entity risk, mxGraph graph) {
        Object parent = graph.getDefaultParent();
        int x = 100;
        int y = 100;
        int width = 160;
        int height = 120;
        return (mxCell) graph.insertVertex(parent, risk.getName(), risk, x, y, width, height,
                "rounded=1");
    }

    private mxCell insertDependsOn(Grounding grounding, mxCell source, mxCell target, mxGraph graph) {
        Object parent = graph.getDefaultParent();
        String id = "dependsOn(" + source.getValue() + "," + target.getValue() + ")";
        return (mxCell) graph.insertEdge(parent, id, grounding, source, target);
    }

    private mxCell insertHasRisk(Grounding grounding, mxCell source, mxCell target, mxGraph graph) {
        Object parent = graph.getDefaultParent();
        String id = "hasRiskDegree(" + source.getValue() + "," + target.getValue() + ")";
        return (mxCell) graph.insertEdge(parent, id, grounding, source, target);
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
