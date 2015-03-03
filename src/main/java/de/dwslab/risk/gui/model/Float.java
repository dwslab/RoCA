package de.dwslab.risk.gui.model;

public class Float extends Type {

    public static final Float FLOAT = new Float("_float");

    Float(String name) {
        super(name);
    }

    @Override
    public void accept(UserObjectVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Float)) {
            return false;
        }
        Type other = (Type) obj;
        if (getName() == null) {
            if (other.getName() != null) {
                return false;
            }
        } else if (!getName().equals(other.getName())) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Float [name=" + getName() + "]";
    }

}
