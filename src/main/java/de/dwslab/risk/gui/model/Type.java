package de.dwslab.risk.gui.model;

public class Type implements UserObject {

    private static final long serialVersionUID = -4714484350120480342L;

    private final String name;

    public Type(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
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
        if (!(obj instanceof Type)) {
            return false;
        }
        Type other = (Type) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Type [name=" + name + "]";
    }

}
