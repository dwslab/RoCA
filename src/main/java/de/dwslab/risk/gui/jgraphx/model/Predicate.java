package de.dwslab.risk.gui.jgraphx.model;

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
