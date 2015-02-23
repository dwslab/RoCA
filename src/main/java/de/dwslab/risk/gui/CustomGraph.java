package de.dwslab.risk.gui;

import java.util.Iterator;
import java.util.List;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.util.mxPoint;
import com.mxgraph.view.mxCellState;
import com.mxgraph.view.mxGraph;

import de.dwslab.risk.gui.model.Entity;
import de.dwslab.risk.gui.model.Grounding;

/**
 * A graph that creates new edges from a given template edge.
 */
public class CustomGraph extends mxGraph {

    /**
     * Holds the edge to be used as a template for inserting new edges.
     */
    protected Object edgeTemplate;

    /**
     * Custom graph that defines the alternate edge style to be used when
     * the middle control point of edges is double clicked (flipped).
     */
    public CustomGraph() {
        setAlternateEdgeStyle("edgeStyle=mxEdgeStyle.ElbowConnector;elbow=vertical");
    }

    /**
     * Sets the edge template to be used to inserting edges.
     */
    public void setEdgeTemplate(Object template) {
        edgeTemplate = template;
    }

    /**
     * Prints out some useful information about the cell in the tooltip.
     */
    @Override
    public String getToolTipForCell(Object cell) {
        String tip = "<html>";
        mxGeometry geo = getModel().getGeometry(cell);
        mxCellState state = getView().getState(cell);

        if (getModel().isEdge(cell)) {
            tip += "points={";

            if (geo != null) {
                List<mxPoint> points = geo.getPoints();

                if (points != null) {
                    Iterator<mxPoint> it = points.iterator();

                    while (it.hasNext()) {
                        mxPoint point = it.next();
                        tip += "[x=" + RoCA.numberFormat.format(point.getX()) + ",y="
                                + RoCA.numberFormat.format(point.getY()) + "],";
                    }

                    tip = tip.substring(0, tip.length() - 1);
                }
            }

            tip += "}<br>";
            tip += "absPoints={";

            if (state != null) {

                for (int i = 0; i < state.getAbsolutePointCount(); i++) {
                    mxPoint point = state.getAbsolutePoint(i);
                    tip += "[x=" + RoCA.numberFormat.format(point.getX()) + ",y="
                            + RoCA.numberFormat.format(point.getY()) + "],";
                }

                tip = tip.substring(0, tip.length() - 1);
            }

            tip += "}";
        } else {
            tip += "geo=[";

            if (geo != null) {
                tip += "x=" + RoCA.numberFormat.format(geo.getX()) + ",y="
                        + RoCA.numberFormat.format(geo.getY()) + ",width="
                        + RoCA.numberFormat.format(geo.getWidth()) + ",height="
                        + RoCA.numberFormat.format(geo.getHeight());
            }

            tip += "]<br>";
            tip += "state=[";

            if (state != null) {
                tip += "x=" + RoCA.numberFormat.format(state.getX()) + ",y="
                        + RoCA.numberFormat.format(state.getY()) + ",width="
                        + RoCA.numberFormat.format(state.getWidth()) + ",height="
                        + RoCA.numberFormat.format(state.getHeight());
            }

            tip += "]";
        }

        mxPoint trans = getView().getTranslate();

        tip += "<br>scale=" + RoCA.numberFormat.format(getView().getScale())
                + ", translate=[x="
                + RoCA.numberFormat.format(trans.getX())
                + ",y="
                + RoCA.numberFormat.format(trans.getY())
                + "]";
        tip += "</html>";

        return tip;
    }

    /**
     * Overrides the method to use the currently selected edge template for
     * new edges.
     *
     * @param graph
     * @param parent
     * @param id
     * @param value
     * @param source
     * @param target
     * @param style
     * @return
     */
    @Override
    public Object createEdge(Object parent, String id, Object value, Object source, Object target,
            String style) {
        if (edgeTemplate != null) {
            mxCell edge = (mxCell) cloneCells(new Object[] { edgeTemplate })[0];
            edge.setId(id);

            return edge;
        }
        return super.createEdge(parent, id, value, source, target, style);
    }

    @Override
    public String convertValueToString(Object cell) {
        if (((mxCell) cell).getValue() instanceof Entity) {
            Entity entity = (Entity) ((mxCell) cell).getValue();
            return entity.getName();
        } else if (((mxCell) cell).getValue() instanceof Grounding) {
            Grounding grounding = (Grounding) ((mxCell) cell).getValue();
            StringBuilder builder = new StringBuilder();
            builder.append(grounding.getPredicate().getName());
            builder.append('(');
            for (String value : grounding.getValues()) {
                builder.append(value);
                builder.append(", ");
            }
            builder.delete(builder.length() - 2, builder.length());
            builder.append(')');
            return builder.toString();
        }
        return super.convertValueToString(cell);
    }

}