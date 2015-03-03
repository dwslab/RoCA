package de.dwslab.risk.gui.model;


public abstract class AbstractBackgroundKnowledge implements BackgroundKnowledge {

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getEntities() == null) ? 0 : getEntities().hashCode());
        result = prime * result + ((getFormulas() == null) ? 0 : getFormulas().hashCode());
        result = prime * result + ((getGroundings() == null) ? 0 : getGroundings().hashCode());
        result = prime * result + ((getPredicates() == null) ? 0 : getPredicates().hashCode());
        result = prime * result + ((getTypes() == null) ? 0 : getTypes().hashCode());
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
        if (!(obj instanceof AbstractBackgroundKnowledge)) {
            return false;
        }
        AbstractBackgroundKnowledge other = (AbstractBackgroundKnowledge) obj;
        if (getEntities() == null) {
            if (other.getEntities() != null) {
                return false;
            }
        } else if (!getEntities().equals(other.getEntities())) {
            return false;
        }
        if (getFormulas() == null) {
            if (other.getFormulas() != null) {
                return false;
            }
        } else if (!getFormulas().equals(other.getFormulas())) {
            return false;
        }
        if (getGroundings() == null) {
            if (other.getGroundings() != null) {
                return false;
            }
        } else if (!getGroundings().equals(other.getGroundings())) {
            return false;
        }
        if (getPredicates() == null) {
            if (other.getPredicates() != null) {
                return false;
            }
        } else if (!getPredicates().equals(other.getPredicates())) {
            return false;
        }
        if (getTypes() == null) {
            if (other.getTypes() != null) {
                return false;
            }
        } else if (!getTypes().equals(other.getTypes())) {
            return false;
        }
        return true;
    }

}
