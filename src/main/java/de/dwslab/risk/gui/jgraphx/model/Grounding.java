package de.dwslab.risk.gui.jgraphx.model;

import java.util.List;

public class Grounding {

    boolean negated;
    String predicate;
    List<String> values;

    public Grounding(boolean negated, String predicate, List<String> values) {
        this.negated = negated;
        this.predicate = predicate;
        this.values = values;
    }

    public boolean isNegated() {
        return negated;
    }

    public String getPredicate() {
        return predicate;
    }

    public List<String> getValues() {
        return values;
    }

    public String valuesToString() {

        StringBuilder sb = new StringBuilder();
        if (negated) {
            sb.append("!" + " ");
        }

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
        if (negated) {
            atom.append("!");
        }
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
