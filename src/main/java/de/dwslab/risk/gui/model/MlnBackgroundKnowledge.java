package de.dwslab.risk.gui.model;

import static de.dwslab.ai.util.Utils.lines;
import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;

import de.dwslab.risk.gui.exception.RoCAException;

public class MlnBackgroundKnowledge extends AbstractBackgroundKnowledge {

    private final Map<String, Predicate> predicates;
    private final HashMultimap<Type, Entity> entities;
    private final HashMultimap<Predicate, Grounding> groundings;
    private final List<Formula> formulas;
    private final ArrayListMultimap<Predicate, Type> predicateTypes;

    public MlnBackgroundKnowledge(URL mln, Path evidence) {
        try {
            predicateTypes = ArrayListMultimap.create();
            predicates = parsePredicates(mln);
            formulas = parseFormulas(mln);
            entities = parseEntities(evidence);
            groundings = parseGroundings(evidence);
        } catch (Exception e) {
            throw new RoCAException("Cannot load background knowledge.", e);
        }
    }

    private Map<String, Predicate> parsePredicates(URL mln) throws IOException {
        Map<String, Predicate> map = new HashMap<>();

        lines(mln.openStream())
                .map(String::trim)
                .filter(str -> !str.isEmpty() && !str.startsWith("//") && !str.contains(" v ")
                        && !str.contains("\""))
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
                        types.add(Type.get(type));
                    }
                    Predicate pred = new Predicate(name, observed, types);
                    predicateTypes.putAll(pred, types);
                    map.put(name, pred);
                });

        return map;
    }

    private List<Formula> parseFormulas(URL mln) throws IOException {
        List<Formula> formulas = new ArrayList<>();
        lines(mln.openStream())
                .filter(str -> !str.isEmpty() && (str.startsWith("// ?") || str.contains(" v ")))
                .forEach(str -> {
                    formulas.add(new Formula(str));
                });
        return formulas;
    }

    private HashMultimap<Type, Entity> parseEntities(Path evidence) throws IOException {
        HashMultimap<Type, Entity> entities = HashMultimap.create();

        Files.lines(evidence)
                .map(String::trim)
                .filter(str -> !str.isEmpty() && !str.startsWith("//") && !str.contains(","))
                .map(str -> (str.startsWith("!") ? str.substring(1) : str))
                .filter(str -> !str.startsWith("offline("))
                .forEach(
                        str -> {
                            String predicate = str.substring(0, str.indexOf('('));
                            String entityString = str.substring(str.indexOf('(') + 1,
                                    str.indexOf(')')).replaceAll("\"", "");
                            Type type = Type.get(predicate);
                            entities.put(type, Entity.get(entityString, type));
                        });

        Files.lines(evidence)
                .map(String::trim)
                .filter(str -> !str.isEmpty() && !str.startsWith("//"))
                .map(str -> (str.startsWith("!") ? str.substring(1) : str))
                .forEach(
                        str -> {
                            String predicate = str.substring(0, str.indexOf('('));
                            String entitiesString = str.substring(str.indexOf('(') + 1,
                                    str.indexOf(')')).replaceAll("\"", "");
                            String[] entititesArr = entitiesString.split("\\s*,\\s*");

                            List<Type> types = predicateTypes.get(new Predicate(predicate));
                            for (int i = 0; i < types.size(); i++) {
                                Type type = types.get(i);
                                String entityStr = entititesArr[i];
                                Entity entity = Entity.get(entityStr, type);
                                entities.put(entity.getType(), entity);
                            }
                        });

        return entities;
    }

    private HashMultimap<Predicate, Grounding> parseGroundings(Path evidence) throws Exception {
        HashMultimap<Predicate, Grounding> groundings = HashMultimap.create();

        Files.lines(evidence)
                .map(String::trim)
                .filter(str -> !str.isEmpty() && !str.startsWith("//"))
                .forEach(
                        str -> {
                            Predicate predicate;
                            if (str.startsWith("!")) {
                                predicate = new Predicate(true, str.substring(1, str.indexOf('(')));
                            } else {
                                predicate = new Predicate(false, str.substring(0, str.indexOf('(')));
                            }
                            String entitiesString = str.substring(str.indexOf('(') + 1,
                                    str.indexOf(')')).replaceAll("\"", "");
                            String[] entititesArr = entitiesString.split("\\s*,\\s*");
                            List<Entity> entities = Arrays.stream(entititesArr)
                                    .map(e -> Entity.get(e)).collect(toList());
                            Grounding literal = new Grounding(predicate, entities);
                            groundings.put(predicate, literal);
                        });

        groundings.get(new Predicate("componentOf")).forEach(g -> {
            Entity entity = g.getValues().get(0);
            entities.values().remove(entity);
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
    public List<Formula> getFormulas() {
        return formulas;
    }

    @Override
    public HashMultimap<Type, Entity> getEntities() {
        return entities;
    }

    @Override
    public HashMultimap<Predicate, Grounding> getGroundings() {
        return groundings;
    }

}
