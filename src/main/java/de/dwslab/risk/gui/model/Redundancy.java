package de.dwslab.risk.gui.model;

import java.util.HashSet;
import java.util.Set;

public class Redundancy extends Entity {

    private final Set<Entity> components = new HashSet<>();

    Redundancy(String name) {
        super(name, Type.get("redundancy"));
    }

    public void add(Entity entity) {
        components.add(entity);
    }

    public Set<Entity> getComponents() {
        return components;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((components == null) ? 0 : components.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof Redundancy)) {
            return false;
        }
        Redundancy other = (Redundancy) obj;
        if (components == null) {
            if (other.components != null) {
                return false;
            }
        } else if (!components.equals(other.components)) {
            return false;
        }
        return true;
    }

}
