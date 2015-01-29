package de.dwslab.risk.gui.model;

public class Predicate {

    private final boolean negated;
    private final String name;

    public Predicate(String name) {
        this(false, name);
    }

    public Predicate(boolean negated, String name) {
        this.negated = negated;
        this.name = name;
    }

    public boolean isNegated() {
        return negated;
    }

    public String getName() {
        return name;
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
