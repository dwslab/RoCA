package de.dwslab.risk.gui.model;

public class Formula {

    private final String formula;

    public Formula(String formula) {
        this.formula = formula;
    }

    public String getFormula() {
        return formula;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((formula == null) ? 0 : formula.hashCode());
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
        Formula other = (Formula) obj;
        if (formula == null) {
            if (other.formula != null) {
                return false;
            }
        } else if (!formula.equals(other.formula)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return formula;
    }

}
