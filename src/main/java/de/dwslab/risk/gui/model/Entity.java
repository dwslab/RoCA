package de.dwslab.risk.gui.model;

public class Entity implements UserObject {

    private static final long serialVersionUID = -566950411586680045L;

    private String name;
    private Boolean offline;
    private Type type;

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

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getOffline() {
        return offline;
    }

    public void setOffline(Boolean offline) {
        this.offline = offline;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    @Override
    public void accept(UserObjectVisitor visitor) {
        visitor.visit(this);
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
        if (!(obj instanceof Entity)) {
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
        return type.getName() + "("
                + (Boolean.TRUE.equals(offline) ? "!" : "")
                + (offline == null ? "?" : "")
                + name + ")";
    }
}
