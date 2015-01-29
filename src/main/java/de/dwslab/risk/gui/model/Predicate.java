package de.dwslab.risk.gui.model;

import java.util.List;

public class Predicate {

    private final boolean negated;
    private final String name;
    private final List<String> types;

    public Predicate(String name, List<String> types) {
        this(false, name, types);
    }

    public Predicate(boolean negated, String name, List<String> types) {
        this.negated = negated;
        this.name = name;
        this.types = types;
    }

    public boolean isNegated() {
        return negated;
    }

    public String getName() {
        return name;
    }

    public List<String> getTypes() {
        return types;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + (negated ? 1231 : 1237);
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
        Predicate other = (Predicate) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (negated != other.negated) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder pred = new StringBuilder();
        if (negated) {
            pred.append("!");
        }
        pred.append(name + "(");

        for (int i = 0; i < types.size(); i++) {
            pred.append(types.get(i));
            if (i < (types.size() - 1)) {
                pred.append(", ");
            }
        }
        pred.append(")");

        return pred.toString();
    }

}
