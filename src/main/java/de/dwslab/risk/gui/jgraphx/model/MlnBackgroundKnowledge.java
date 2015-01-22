package de.dwslab.risk.gui.jgraphx.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.dwslab.risk.gui.exception.RoCAException;

public class MlnBackgroundKnowledge implements BackgroundKnowledge {

    private final Map<String, Predicate> predicates;
    private final Map<String, Set<String>> entities;
    private final Map<String, Set<Literal>> groundings;

    public MlnBackgroundKnowledge(Path mln, Path evidence) {
        try {
            predicates = parsePredicates(mln);
            entities = parseEntities(evidence);
            groundings = parseGroundings(evidence);
        } catch (Exception e) {
            throw new RoCAException("Cannot load background knowledge.", e);
        }
    }

    private Map<String, Predicate> parsePredicates(Path mln) throws IOException {
        Map<String, Predicate> map = new HashMap<>();

        Files.lines(mln)
                .filter(str -> !str.isEmpty() && !str.startsWith("//") && !str.contains("v")
                        && !str.contains("\""))
                .map(String::trim)
                .map(str -> (str.startsWith("*") ? str.substring(1) : str))
                .forEach(str -> {
                    String name = str.substring(0, str.indexOf('('));
                    String[] types = str.substring(str.indexOf('(') + 1, str.indexOf(')'))
                            .split("\\s*,\\s*");
                    Predicate pred = new Predicate(false, name, Arrays.asList(types));
                    map.put(name, pred);
                });

        return map;
    }

    private Map<String, Set<String>> parseEntities(Path evidence) throws IOException {
        Map<String, Set<String>> entities = new HashMap<>();

        Files.lines(evidence)
                .filter(str -> !str.isEmpty() && !str.startsWith("//"))
                .map(String::trim)
                .map(str -> (str.startsWith("!") ? str.substring(1) : str))
                .forEach(str -> {
                    String predicate = str.substring(0, str.indexOf('('));
                    String entitiesString = str.substring(str.indexOf('(') + 1, str.indexOf(')'));
                    String[] entititesArr = entitiesString.split("\\s*,\\s*");

                    List<String> types = predicates.get(predicate).getTypes();
                    for (int i = 0; i < types.size(); i++) {
                        String type = types.get(i);
                        String entity = entititesArr[i];
                        Set<String> entitySet = entities.get(type);
                        if (entitySet == null) {
                            entitySet = new HashSet<>();
                            entities.put(type, entitySet);
                        }
                        entitySet.add(entity);
                    }
                });

        return entities;
    }

    private static Map<String, Set<Literal>> parseGroundings(Path evidence) throws Exception {
        Map<String, Set<Literal>> groundings = new HashMap<>();

        Files.lines(evidence)
                .filter(str -> !str.isEmpty() && !str.startsWith("//"))
                .map(String::trim)
                .map(str -> (str.startsWith("!") ? str.substring(1) : str))
                .forEach(str -> {
                    String predicate = str.substring(0, str.indexOf('('));
                    String entitiesString = str.substring(str.indexOf('(') + 1, str.indexOf(')'));
                    String[] entititesArr = entitiesString.split("\\s*,\\s*");

                    Literal literal = new Literal(false, predicate, Arrays.asList(entititesArr));
                    Set<Literal> groundingsSet = groundings.get(predicate);
                    if (groundingsSet == null) {
                        groundingsSet = new HashSet<>();
                        groundings.put(predicate, groundingsSet);
                    }
                    groundingsSet.add(literal);
                });

        return groundings;
    }

    @Override
    public Map<String, Predicate> getPredicates() {
        return predicates;
    }

    @Override
    public Set<String> getTypes() {
        return entities.keySet();
    }

    @Override
    public Map<String, Set<String>> getEntities() {
        return entities;
    }

    @Override
    public Map<String, Set<Literal>> getGroundings() {
        return groundings;
    }

}
