package de.dwslab.risk.gui.model;

import java.util.HashMap;
import java.util.Map;

import de.dwslab.risk.gui.exception.RoCAException;

public abstract class Type implements UserObject {

    private static final long serialVersionUID = -4714484350120480342L;

    public static final Float FLOAT = new Float("float_");

    private static final Map<String, Component> COMPONENTS = new HashMap<>();
    private static final Map<String, Risk> RISKS = new HashMap<>();

    private final String name;

    protected Type(String name) {
        this.name = name;
    }

    public static Type get(String name) {
        if ("float_".equals(name)) {
            return FLOAT;
        }
        Component c = COMPONENTS.get(name);
        Risk r = RISKS.get(name);
        if (c == null && r == null) {
            throw new RoCAException("Type not found: " + name);
        } else if (c != null) {
            return c;
        } else {
            return r;
        }
    }

    public static Component newComponent(String name) {
        if (RISKS.containsKey(name)) {
            throw new RoCAException("Ambigiuous type" + name);
        }
        Component c = new Component(name);
        COMPONENTS.put(name, c);
        return c;
    }

    public static boolean isComponent(String name) {
        return COMPONENTS.containsKey(name);
    }

    public static Risk newRisk(String name) {
        if (COMPONENTS.containsKey(name)) {
            throw new RoCAException("Ambigiuous type" + name);
        }
        Risk r = new Risk(name);
        RISKS.put(name, r);
        return r;
    }

    public static boolean isRisk(String name) {
        return RISKS.containsKey(name);
    }

    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "Type [name=" + name + "]";
    }

}
