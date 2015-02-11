package de.dwslab.risk.gui.model;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class Predicate implements Serializable {

    private static final long serialVersionUID = -5665299561795081532L;

    private final boolean negated;
    private final String name;
    private final boolean observed;
    private final List<Type> types;

    public Predicate(String name) {
        this(false, name, false, Collections.emptyList());
    }

    public Predicate(boolean negated, String name) {
        this(negated, name, true, Collections.emptyList());
    }

    public Predicate(String name, boolean observed, List<Type> types) {
        this(false, name, observed, types);
    }

    private Predicate(boolean negated, String name, boolean observed, List<Type> types) {
        this.negated = negated;
        this.name = name;
        this.observed = observed;
        this.types = types;
    }

    public boolean isNegated() {
        return negated;
    }

    public String getName() {
        return name;
    }

    public boolean isObserved() {
        return observed;
    }

    public List<Type> getTypes() {
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
        pred.append(name);
        return pred.toString();
    }

}
