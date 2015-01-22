package de.dwslab.risk.gui;

import java.awt.Color;
import java.text.NumberFormat;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

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

public class RoCA extends BasicGraphEditor {

    private static final long serialVersionUID = -4601740824088314699L;

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
            e.printStackTrace(System.err);
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
        mxGraph graph = graphComponent.getGraph();
        graph.selectAll();
        graph.removeCells();

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
