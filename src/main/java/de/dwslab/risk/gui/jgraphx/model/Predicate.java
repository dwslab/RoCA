package de.dwslab.risk.gui.jgraphx.model;

import java.util.List;

public class Predicate {

    boolean observed;
    String name;
    List<String> types;

    public Predicate(boolean observed, String name, List<String> types) {
        this.observed = observed;
        this.name = name;
        this.types = types;
    }

    public boolean isObserved() {
        return observed;
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
        if (observed) {
            pred.append("*");
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
