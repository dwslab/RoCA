package de.dwslab.risk.gui.model;

public class Entity {

    private final String name;
    private final boolean offline;
    private final Type type;

    public Entity(String name, Type type) {
        this(name, type, false);
    }

    public Entity(String name, Type type, boolean offline) {
        this.name = name;
        this.type = type;
        this.offline = offline;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public boolean isOffline() {
        return offline;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + (offline ? 1231 : 1237);
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Entity other = (Entity) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (offline != other.offline) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return type.getName() + "(" + name + ")";
    }

}
