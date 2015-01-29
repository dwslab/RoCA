package de.dwslab.risk.gui.model;

import java.util.List;

public class Grounding {

    private final Predicate predicate;
    private final List<String> values;

    public Grounding(Predicate predicate, List<String> values) {
        this.predicate = predicate;
        this.values = values;
    }

    public Predicate getPredicate() {
        return predicate;
    }

    public List<String> getValues() {
        return values;
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
