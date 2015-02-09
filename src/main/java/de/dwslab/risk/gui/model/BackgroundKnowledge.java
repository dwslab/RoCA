package de.dwslab.risk.gui.model;

import static org.jooq.lambda.Unchecked.consumer;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import de.dwslab.risk.gui.exception.RoCAException;

public interface BackgroundKnowledge {

    public static BackgroundKnowledge aggregate(BackgroundKnowledge... knowledgeBases) {
        return aggregate(Arrays.asList(knowledgeBases));

    }

    public static BackgroundKnowledge aggregate(Collection<BackgroundKnowledge> knowledgeBases) {
        Map<String, Predicate> predicates = new HashMap<>();
        Set<Type> types = new HashSet<>();
        Map<Type, Set<Entity>> entities = new HashMap<>();
        Map<Predicate, Set<Grounding>> groundings = new HashMap<>();

        for (BackgroundKnowledge knowledge : knowledgeBases) {
            for (Entry<String, Predicate> entry : knowledge.getPredicates().entrySet()) {
                if (!predicates.containsKey(entry.getKey())) {
                    predicates.put(entry.getKey(), entry.getValue());
                }
            }
            types.addAll(knowledge.getTypes());
            for (Entry<Type, Set<Entity>> entry : knowledge.getEntities().entrySet()) {
                Set<Entity> set = entities.get(entry.getKey());
                if (set == null) {
                    set = new HashSet<>();
                    entities.put(entry.getKey(), set);
                }
                set.addAll(entry.getValue());
            }
            for (Entry<Predicate, Set<Grounding>> entry : knowledge.getGroundings().entrySet()) {
                Set<Grounding> set = groundings.get(entry.getKey());
                if (set == null) {
                    set = new HashSet<>();
                    groundings.put(entry.getKey(), set);
                }
                set.addAll(entry.getValue());
            }
        }
        return new AggregatedBackgroundKnowledge(predicates, types, entities, groundings);
    }

    public default void exportAsMln(Path mln, Path evidence) {
        try (BufferedWriter mlnWriter = Files.newBufferedWriter(mln)) {
            Map<String, Predicate> predicates = getPredicates();
            for (Predicate predicate : predicates.values()) {
                if (predicate.isObserved()) {
                    mlnWriter.write('*');
                }
                mlnWriter.write(predicate.getName());
                mlnWriter.write('(');
                List<Type> types = predicate.getTypes();
                for (int i = 0; i < types.size() - 1; i++) {
                    mlnWriter.write(types.get(i).getName());
                    mlnWriter.write(',');
                }
                mlnWriter.write(types.get(types.size() - 1).getName());
                mlnWriter.write(')');
                mlnWriter.newLine();
            }

            // TODO write formulas

        } catch (IOException e) {
            throw new RoCAException("Cannot write model file.", e);
        }

        try (BufferedWriter evidenceWriter = Files.newBufferedWriter(evidence)) {
            Map<Predicate, Set<Grounding>> groundings = getGroundings();
            groundings.values().stream()
                    .flatMap(g -> g.stream())
                    .forEach(consumer(g -> {
                        Predicate predicate = g.getPredicate();
                        if (predicate.isNegated()) {
                            evidenceWriter.write('!');
                        }
                        evidenceWriter.write(predicate.getName());
                        evidenceWriter.write('(');
                        List<String> values = g.getValues();
                        for (int i = 0; i < values.size() - 1; i++) {
                            String value = values.get(i);
                            evidenceWriter.write(value);
                            evidenceWriter.write(',');
                        }
                        evidenceWriter.write(values.get(values.size() - 1));
                        evidenceWriter.write(')');
                        evidenceWriter.newLine();
                    }));
        } catch (IOException e) {
            throw new RoCAException("Cannot write evidence file.", e);
        }
    }

    public default void exportAsOntology(Path mln, Path owl) {
        throw new RoCAException("Exporting as ontology not implemented.");
    }

    /**
     * @return the predicates
     */
    public Map<String, Predicate> getPredicates();

    /**
     * @return the types
     */
    public Set<Type> getTypes();

    /**
     * @return the entities
     */
    public Map<Type, Set<Entity>> getEntities();

    /**
     * @return the groundings
     */
    public Map<Predicate, Set<Grounding>> getGroundings();

}
