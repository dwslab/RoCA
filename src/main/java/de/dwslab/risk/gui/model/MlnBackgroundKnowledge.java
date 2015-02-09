package de.dwslab.risk.gui.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.dwslab.risk.gui.exception.RoCAException;

public class MlnBackgroundKnowledge implements BackgroundKnowledge {

    private final Map<String, Predicate> predicates;
    private final Map<Type, Set<Entity>> entities;
    private final Map<Predicate, Set<Grounding>> groundings;

    private final Map<Predicate, List<Type>> predicateTypes;

    public MlnBackgroundKnowledge(Path mln, Path evidence) {
        try {
            predicateTypes = new HashMap<>();
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
                .forEach(str -> {
                    String name;
                    boolean observed;
                    if (str.startsWith("*")) {
                        name = str.substring(1, str.indexOf('('));
                        observed = true;
                    } else {
                        name = str.substring(0, str.indexOf('('));
                        observed = false;
                    }
                    String[] typesStr = str.substring(str.indexOf('(') + 1, str.indexOf(')'))
                            .split("\\s*,\\s*");
                    List<Type> types = new ArrayList<>();
                    for (String type : typesStr) {
                        types.add(new Type(type));
                    }
                    Predicate pred = new Predicate(name, observed, types);
                    predicateTypes.put(pred, types);
                    map.put(name, pred);
                });

        return map;
    }

    private Map<Type, Set<Entity>> parseEntities(Path evidence) throws IOException {
        Map<Type, Set<Entity>> entities = new HashMap<>();

        Files.lines(evidence)
                .filter(str -> !str.isEmpty() && !str.startsWith("//"))
                .map(String::trim)
                .map(str -> (str.startsWith("!") ? str.substring(1) : str))
                .forEach(str -> {
                    String predicate = str.substring(0, str.indexOf('('));
                    String entitiesString = str.substring(str.indexOf('(') + 1, str.indexOf(')'));
                    String[] entititesArr = entitiesString.split("\\s*,\\s*");

                    List<Type> types = predicateTypes.get(new Predicate(predicate));
                    for (int i = 0; i < types.size(); i++) {
                        Type type = types.get(i);
                        String entity = entititesArr[i];
                        Set<Entity> entitySet = entities.get(type);
                        if (entitySet == null) {
                            entitySet = new HashSet<>();
                            entities.put(type, entitySet);
                        }
                        entitySet.add(new Entity(entity, type));
                    }
                });

        return entities;
    }

    private Map<Predicate, Set<Grounding>> parseGroundings(Path evidence) throws Exception {
        Map<Predicate, Set<Grounding>> groundings = new HashMap<>();

        Files.lines(evidence)
                .filter(str -> !str.isEmpty() && !str.startsWith("//"))
                .map(String::trim)
                .forEach(str -> {
                    Predicate predicate;
                    if (str.startsWith("!")) {
                        predicate = new Predicate(true, str.substring(1, str.indexOf('(')));
                    } else {
                        predicate = new Predicate(false, str.substring(0, str.indexOf('(')));
                    }
                    String entitiesString = str.substring(str.indexOf('(') + 1, str.indexOf(')'));
                    String[] entititesArr = entitiesString.split("\\s*,\\s*");

                    Grounding literal = new Grounding(predicate, Arrays.asList(entititesArr));
                    Set<Grounding> groundingsSet = groundings.get(predicate);
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
    public Set<Type> getTypes() {
        return entities.keySet();
    }

    @Override
    public Map<Type, Set<Entity>> getEntities() {
        return entities;
    }

    @Override
    public Map<Predicate, Set<Grounding>> getGroundings() {
        return groundings;
    }

}
