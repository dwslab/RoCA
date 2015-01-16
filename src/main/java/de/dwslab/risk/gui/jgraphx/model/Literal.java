package de.dwslab.risk.gui.jgraphx.model;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Literal {

    boolean negated;
    String predicate;
    List<String> values;

    public Literal(boolean negated, String predicate, List<String> values) {
        this.negated = negated;
        this.predicate = predicate;
        this.values = values;
    }

    public Literal(String literal) {
        // negated + name
        Pattern pattern = Pattern.compile("\\s*(!)?(\\w+)\\((.*)\\)\\s*");
        // Pattern pattern = Pattern.compile("(!)?\\s*([\\w]*).*");
        Matcher matcher = pattern.matcher(literal.trim());

        String elements = "";
        negated = false;
        if (matcher.find()) {
            if ((matcher.group(1) != null) && matcher.group(1).equals("!")) {
                negated = true;
            }
            predicate = matcher.group(2);
            elements = matcher.group(3).trim();
        }

        // variables
        // Pattern pattern2 = Pattern.compile("([^\\s(),]+)[,\\s)]+");
        Pattern pattern2 = Pattern.compile("([\\w\\?]+|(\\[.*\\]))+\\s*,?");
        Matcher matcher2 = pattern2.matcher(elements);

        values = new ArrayList<>();
        while (matcher2.find()) {
            values.add(matcher2.group(1));
        }
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
