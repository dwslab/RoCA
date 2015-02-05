package de.dwslab.risk.gui.model;

public class Entity {

    private final String name;
    private Boolean offline;
    private final Type type;

    public Entity(String name, Type type) {
        this(name, type, null);
    }

    public Entity(String name, Type type, Boolean offline) {
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

    public Boolean isOffline() {
        return offline;
    }

    public void setOffline(Boolean offline) {
        this.offline = offline;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((offline == null) ? 0 : offline.hashCode());
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
        if (offline == null) {
            if (other.offline != null) {
                return false;
            }
        } else if (!offline.equals(other.offline)) {
            return false;
        }
        if (type == null) {
            if (other.type != null) {
                return false;
            }
        } else if (!type.equals(other.type)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return type.getName() + "(" + name + ")";
    }

}
