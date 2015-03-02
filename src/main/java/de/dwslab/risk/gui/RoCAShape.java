package de.dwslab.risk.gui;

import javax.swing.ImageIcon;

import de.dwslab.risk.gui.model.Entity;
import de.dwslab.risk.gui.model.Type;
import de.dwslab.risk.gui.model.UserObject;

public enum RoCAShape implements RoCAIcons {

    COMPONENT("Component", ICON_RECTANGLE, null, 160, 120, Entity.create("New Component", new Type(
            "infra"))),

    RISK("Risk", ICON_ELLIPSE, "ellipse", 160, 120, Entity.create("New Risk", new Type("risk"))),

    REDUNDANCY("Redundancy", ICON_CONTAINER, "swimlane", 280, 280, Entity.create(
            "New Redundancy Component", new Type("redundancy")));

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
