package de.dwslab.risk.gui.jgraphx.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Formula {

    boolean isHard = true;
    double weight = -1;

    List<Grounding> literals = new ArrayList<>();

    public void addLiteral(Grounding l) {
        literals.add(l);
    }

    @Override
    public String toString() {
        StringBuilder temp = new StringBuilder();
        if (!isHard) {
            temp.append(weight + " ");
        }

        for (int i = 0; i < literals.size(); i++) {
            temp.append(literals.get(i).toString());
            if (i < (literals.size() - 1)) {
                temp.append(" v ");
            }
        }

        if (isHard) {
            temp.append(".");
        }

        return temp.toString();
    }

    public List<String> getGroundFormulas(Map<String, Predicate> predicates,
            Map<String, String> predMapping, Map<String, List<String>> entities,
            Map<String, Set<Grounding>> groundings) {
        List<String> groundFormulas = new ArrayList<>();

        List<Map<String, String>> combintations = generateVariableGroundings(
                getVariables(predicates), entities);
        for (Map<String, String> c : combintations) {
            Formula f = new Formula();

            for (Grounding l : literals) {
                boolean negated = l.isNegated();
                String predicate = l.getPredicate();

                List<String> vars = l.getValues();

                List<List<String>> c2 = new ArrayList<>();
                for (int i = 0; i < vars.size(); i++) {
                    Set<String> groundValue = new HashSet<>();
                    String var = vars.get(i);
                    if (c.containsKey(var)) {
                        // normal variable
                        groundValue.add(c.get(var));
                    } else if (var.startsWith("\"") && var.endsWith("\"")) {
                        // constant
                        groundValue.add(var);
                    } else if (var.startsWith("[") && var.endsWith("]")) {
                        // get "?"
                        var = var.substring(1, var.length() - 1);
                        Grounding exLit = new Grounding(var);
                        String exPred = exLit.getPredicate();

                        List<String> exPredVars = new ArrayList<>();
                        for (int j = 0; j < exLit.getValues().size(); j++) {
                            String exVar = exLit.getValues().get(j);
                            if (c.containsKey(exVar)) {
                                // variable occurs in formula
                                exPredVars.add(c.get(exVar));
                            } else if (exVar.equals("?")) {
                                // target variable
                                exPredVars.add(exVar);
                            } else if (exVar.startsWith("\"") && exVar.endsWith("\"")) {
                                // constant
                                exPredVars.add(exVar);
                            } else {
                                System.err.println("Something is wrong with this formula:");
                                System.err.println(toString());
                                System.err.println(var);
                            }
                        }

                        // collect all ground axioms of exPred
                        Set<Grounding> relLit = new HashSet<>();
                        if (groundings.containsKey(exPred)) {
                            relLit.addAll(groundings.get(exPred));
                        }

                        // collect weighted ground values
                        if (predMapping.containsKey(exPred)) {
                            Set<Grounding> relLit2 = new HashSet<>();
                            String pred3 = predMapping.get(exPred);
                            if (groundings.containsKey(pred3)) {
                                for (Grounding l3 : groundings.get(pred3)) {
                                    boolean n3 = l3.isNegated();
                                    List<String> v3 = new ArrayList<>(l3.getValues());
                                    v3.remove(v3.size() - 1);
                                    relLit2.add(new Grounding(n3, exPred, v3));
                                }
                            }
                            relLit.addAll(relLit2);
                        }

                        for (Grounding groundLiteral : relLit) {
                            if (groundLiteral.isNegated()) {
                                continue;
                            }

                            String q = null;
                            for (int j = 0; j < groundLiteral.getValues().size(); j++) {
                                String t0 = exPredVars.get(j);
                                String t1 = groundLiteral.getValues().get(j);
                                if (t0.equals("?")) {
                                    q = t1;
                                } else if (!t0.equals(t1)) {
                                    // no match
                                    break;
                                }
                            }
                            if (q != null) {
                                groundValue.add(q);
                            }

                        }
                    } // end: get "?"
                    c2.add(new ArrayList<String>(groundValue));
                } // for: end elements of literal

                List<List<String>> groundPredComp = generateCombinations2(c2);
                for (List<String> groundPred : groundPredComp) {
                    f.addLiteral(new Grounding(negated, predicate, groundPred));
                }

            }
            // System.out.println(f);

            groundFormulas.add(f.toString());
            // break;
        }

        return groundFormulas;
    }

    private List<List<String>> generateCombinations2(List<List<String>> vars) {
        List<List<String>> combinations = new ArrayList<>();

        int varCount = vars.size();
        int counter = 1;

        // init mod
        List<Integer> mod = new ArrayList<>();
        for (int i = 0; i < varCount; i++) {
            mod.add(0);
        }

        // count combinations
        for (int i = 0; i < varCount; i++) {
            List<String> e = vars.get(i);
            counter *= e.size();
            for (int j = 0; j < i; j++) {
                mod.set(j, mod.get(j) + e.size());
            }
        }
        mod.set(mod.size() - 1, 1);
        counter *= varCount;

        List<String> tempComp = new ArrayList<>();

        for (int i = 1; i <= counter; i++) {
            int v = (i - 1) % varCount;
            List<String> e = vars.get(v);

            int entityNumber = (((i - 1) / varCount) / mod.get(v)) % e.size();
            String nextEntity = e.get(entityNumber);

            tempComp.add(nextEntity);
            if ((i % varCount) == 0) {
                combinations.add(tempComp);
                tempComp = new ArrayList<>();
            }
        }
        return combinations;
    }

    private List<Map<String, String>> generateVariableGroundings(Map<String, String> variables,
            Map<String, List<String>> entities) {
        // list: variable => ground value
        List<Map<String, String>> combinations = new ArrayList<>();

        // list of all variables
        List<String> vars = new ArrayList<>(variables.keySet());

        // counter
        int varCount = vars.size();
        int counter = 1;

        // init mod
        List<Integer> mod = new ArrayList<>();
        for (int i = 0; i < varCount; i++) {
            mod.add(0);
        }

        // count combinations
        for (int i = 0; i < varCount; i++) {
            List<String> e = entities.get(variables.get(vars.get(i)));
            counter *= e.size();
            for (int j = 0; j < i; j++) {
                mod.set(j, mod.get(j) + e.size());
            }
        }
        mod.set(mod.size() - 1, 1);
        counter *= varCount;

        Map<String, String> tempComp = new HashMap<>();

        for (int i = 1; i <= counter; i++) {
            int v = (i - 1) % varCount;
            String vName = vars.get(v);
            List<String> e = entities.get(variables.get(vName));

            int entityNumber = (((i - 1) / varCount) / mod.get(v)) % e.size();
            String nextEntity = e.get(entityNumber);

            tempComp.put(vName, nextEntity);
            if ((i % varCount) == 0) {
                combinations.add(tempComp);
                tempComp = new HashMap<>();
            }
        }

        return combinations;
    }

    private Map<String, String> getVariables(Map<String, Predicate> predicates) {
        Map<String, String> variables = new HashMap<>();

        for (Grounding l : literals) {
            List<String> vars = l.getValues();
            List<String> types = predicates.get(l.getPredicate()).getTypes();
            for (int i = 0; i < vars.size(); i++) {
                String var = vars.get(i);
                String type = types.get(i);

                if (var.matches(".*\\W+.*")) {
                    continue;
                }

                if (!variables.containsKey(var)) {
                    variables.put(var, type);
                }

                // consistency check
                if (!variables.containsKey(var) && !variables.get(var).equals(type)) {
                    System.err.println("Variable " + var + " is assigned to different types.");
                    System.err.println(toString());
                }

            }
        }

        return variables;
    }

}
