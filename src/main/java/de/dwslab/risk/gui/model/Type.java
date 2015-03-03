package de.dwslab.risk.gui.model;

import java.util.HashSet;
import java.util.Set;

public abstract class Type implements UserObject {

    private static final long serialVersionUID = -4714484350120480342L;

    private static final Set<String> COMPONENTS = new HashSet<>();
    private static final Set<String> RISKS = new HashSet<>();

    private final String name;

    protected Type(String name) {
        this.name = name;
    }

    public static Component newComponent(String name) {
        COMPONENTS.add(name);
        return new Component(name);
    }

    public static boolean isComponent(String name) {
        return COMPONENTS.contains(name);
    }

    public static Risk newRisk(String name) {
        RISKS.add(name);
        return new Risk(name);
    }

    public static boolean isRisk(String name) {
        return RISKS.contains(name);
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
