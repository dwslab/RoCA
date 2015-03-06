package de.dwslab.risk.gui;

import java.awt.Color;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.HashMultimap;
import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.swing.util.mxSwingConstants;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxResources;
import com.mxgraph.view.mxGraph;

import de.dwslab.ai.util.Utils;
import de.dwslab.risk.gui.jgraphx.BasicGraphEditor;
import de.dwslab.risk.gui.jgraphx.EditorMenuBar;
import de.dwslab.risk.gui.jgraphx.EditorPalette;
import de.dwslab.risk.gui.model.BackgroundKnowledge;
import de.dwslab.risk.gui.model.Component;
import de.dwslab.risk.gui.model.Entity;
import de.dwslab.risk.gui.model.Grounding;
import de.dwslab.risk.gui.model.GuiBackgroundKnowledge;
import de.dwslab.risk.gui.model.MlnBackgroundKnowledge;
import de.dwslab.risk.gui.model.Predicate;
import de.dwslab.risk.gui.model.Risk;
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

    public RoCA() throws IOException {
        super("RoCA", new CustomGraphComponent(new CustomGraph()));
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            String message = e.getMessage();
            if (message == null || message.trim().isEmpty()) {
                message = "Unhandled exception: " + e.getClass();
            }
            JOptionPane.showMessageDialog(RoCA.this, message);
            logger.error("Unhandled exception", e);
        });

        final mxGraph graph = graphComponent.getGraph();
        // graph.addListener(
        // mxEvent.CELLS_ADDED,
        // (sender, event) -> {
        // Object[] cells = (Object[]) event.getProperties().get("cells");
        // for (Object obj : cells) {
        // mxCell cell = (mxCell) obj;
        // if (cell.isVertex()) {
        // Entity entity = (Entity) cell.getValue();
        // cell.setValue(Entity.create(entity.getName(), entity.getType()));
        // } else {
        // if (cell.getTarget() == null) {
        // event.consume();
        // graph.removeCells(new Object[] { cell });
        // } else {
        // Entity source = (Entity) cell.getSource().getValue();
        // Entity target = (Entity) cell.getTarget().getValue();
        // Type sourceType = source.getType();
        // Type targetType = target.getType();
        // if (sourceType instanceof Component && targetType instanceof Risk) {
        // Entity weight = Entity.create(String.valueOf(0d), FLOAT);
        // Grounding grounding = new Grounding(new Predicate(
        // "hasRiskDegree"), Arrays.asList(source, target, weight));
        // cell.setValue(grounding);
        // } else if (sourceType instanceof Component
        // && targetType instanceof Component) {
        // Grounding grounding = new Grounding(new Predicate("dependsOn"),
        // Arrays.asList(source, target));
        // cell.setValue(grounding);
        // } else {
        // event.consume();
        // graph.removeCells(new Object[] { cell });
        // JOptionPane.showMessageDialog(
        // SwingUtilities.getWindowAncestor(graphComponent),
        // "Relation wird nicht unterstützt: " + source + " --> "
        // + target);
        // }
        // }
        // }
        // }
        // });

        // Creates the shapes palette
        EditorPalette shapesPalette = insertPalette(mxResources.get("shapes"));
        for (RoCAShape shape : RoCAShape.values()) {
            shapesPalette.addTemplate(shape.getName(), shape.getIcon(), shape.getStyle(),
                    shape.getWidth(), shape.getHeight(), shape.getValue());
        }

        Path dummy = Utils.createTempPath("dummy", ".db");
        knowledge = new MlnBackgroundKnowledge(RoCA.class.getResource("/default.mln"), dummy);
        Files.delete(dummy);
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
            Map<Entity, mxCell> cellMap = new HashMap<>();

            for (Entry<Type, Entity> entry : knowledge.getEntities().entries()) {
                if (entry.getKey() instanceof Component) {
                    mxCell cell = insertEntity(entry.getValue(), graph);
                    cellMap.put(entry.getValue(), cell);
                } else if (entry.getKey() instanceof Risk) {
                    mxCell cell = insertRisk(entry.getValue(), graph);
                    cellMap.put(entry.getValue(), cell);
                }
            }

            // connect the entities with edges
            HashMultimap<Predicate, Grounding> groundings = knowledge.getGroundings();
            Set<Grounding> dependsOns = groundings.get(new Predicate("dependsOn"));
            for (Grounding literal : dependsOns) {
                Entity source = literal.getValues().get(0);
                Entity target = literal.getValues().get(1);
                insertDependsOn(literal, cellMap.get(source), cellMap.get(target), graph);
            }

            Set<Grounding> hasRisks = groundings.get(new Predicate("hasRiskDegree"));
            for (Grounding literal : hasRisks) {
                Entity source = literal.getValues().get(0);
                Entity target = literal.getValues().get(1);
                insertHasRisk(literal, cellMap.get(source), cellMap.get(target), graph);
            }
            Set<Grounding> offlines = groundings.get(new Predicate("offline"));
            for (Grounding literal : offlines) {
                Entity infra = literal.getValues().get(0);
                mxCell cell = cellMap.get(infra);

                Entity value = (Entity) cell.getValue();
                knowledge.getEntities().remove(value.getType(), cell.getValue());
                value.setOffline(Boolean.TRUE);
                knowledge.getEntities().put(value.getType(), ((Entity) cell.getValue()));

                graph.getModel().setStyle(cell, cell.getStyle() + ";fillColor=#FF2222");
            }
            Set<Grounding> notOfflines = groundings.get(new Predicate(true, "offline"));
            for (Grounding literal : notOfflines) {
                Entity infra = literal.getValues().get(0);
                mxCell cell = cellMap.get(infra);

                Entity value = (Entity) cell.getValue();
                knowledge.getEntities().remove(value.getType(), cell.getValue());
                value.setOffline(Boolean.FALSE);
                knowledge.getEntities().put(value.getType(), ((Entity) cell.getValue()));

                graph.getModel().setStyle(cell, cell.getStyle() + ";fillColor=#22FF22");
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
        mxCell cell = (mxCell) graph.insertVertex(parent, entity.getName(), entity, x, y, width,
                height);
        RoCAShape shape = RoCAShape.valueOf(entity.getType().getName().toUpperCase());
        cell.setStyle(shape.getStyle());
        return cell;
    }

    private mxCell insertRisk(Entity risk, mxGraph graph) {
        Object parent = graph.getDefaultParent();
        int x = 100;
        int y = 100;
        int width = 160;
        int height = 120;
        mxCell cell = (mxCell) graph
                .insertVertex(parent, risk.getName(), risk, x, y, width, height);
        RoCAShape shape = RoCAShape.valueOf(risk.getType().getName().toUpperCase());
        cell.setStyle(shape.getStyle());
        return cell;
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
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
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
