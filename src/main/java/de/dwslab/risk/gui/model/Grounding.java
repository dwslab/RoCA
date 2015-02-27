package de.dwslab.risk.gui.model;

import java.util.List;

public class Grounding implements UserObject {

    private static final long serialVersionUID = 4692532312791603293L;

    private final Predicate predicate;
    private final List<Entity> values;

    public Grounding(Predicate predicate, List<Entity> values) {
        this.predicate = predicate;
        this.values = values;
    }

    public Predicate getPredicate() {
        return predicate;
    }

    public List<Entity> getValues() {
        return values;
    }

    @Override
    public void accept(UserObjectVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((predicate == null) ? 0 : predicate.hashCode());
        result = prime * result + ((values == null) ? 0 : values.hashCode());
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
        Grounding other = (Grounding) obj;
        if (predicate == null) {
            if (other.predicate != null) {
                return false;
            }
        } else if (!predicate.equals(other.predicate)) {
            return false;
        }
        if (values == null) {
            if (other.values != null) {
                return false;
            }
        } else if (!values.equals(other.values)) {
            return false;
        }
        return true;
    }

    public String valuesToString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < values.size(); i++) {
            sb.append(values.get(i));
            if (i < (values.size() - 1)) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder atom = new StringBuilder();
        atom.append(predicate + "(");
        for (int i = 0; i < values.size(); i++) {
            atom.append(values.get(i));
            if (i < (values.size() - 1)) {
                atom.append(", ");
            }
        }
        atom.append(")");
        return atom.toString();
    }

}
