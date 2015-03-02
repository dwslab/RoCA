package de.dwslab.risk.gui;

import javax.swing.ImageIcon;

import de.dwslab.risk.gui.model.Entity;
import de.dwslab.risk.gui.model.Type;
import de.dwslab.risk.gui.model.UserObject;

public enum RoCAShape implements RoCAIcons {

    SERVICE("Service", IMAGE_WRENCH,
            "icon;image=/com/mxgraph/examples/swing/images/wrench.png",
            120, 120, Entity.create("New Service", new Type("infra"))),

    SERVER("Server", IMAGE_SERVER,
            "icon;image=/com/mxgraph/examples/swing/images/server.png",
            120, 120, Entity.create("New Server", new Type("infra"))),

    PRINTER("Printer", IMAGE_PRINTER,
            "icon;image=/com/mxgraph/examples/swing/images/printer.png",
            120, 120, Entity.create("New Printer", new Type("infra"))),

    COMPONENT("Component", ICON_RECTANGLE, null,
            160, 120, Entity.create("New Component", new Type("infra"))),

    REDUNDANCY("Redundancy", ICON_CONTAINER, "swimlane",
            280, 280, Entity.create("New Redundancy Component", new Type("redundancy"))),

    RISK("Risk", ICON_CLOUD, "ellipse;shape=cloud",
            160, 120, Entity.create("New Risk", new Type("risk")));

    private final String name;
    private final ImageIcon icon;
    private final String style;
    private final int width;
    private final int height;
    private final UserObject value;

    private RoCAShape(String name, ImageIcon icon, String style, int width, int height,
            UserObject value) {
        this.name = name;
        this.icon = icon;
        this.style = style;
        this.width = width;
        this.height = height;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public ImageIcon getIcon() {
        return icon;
    }

    public String getStyle() {
        return style;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public UserObject getValue() {
        return value;
    }

}
