package de.dwslab.risk.gui.model;

import static org.jooq.lambda.Unchecked.consumer;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.math.NumberUtils;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import de.dwslab.risk.gui.exception.RoCAException;

public interface BackgroundKnowledge {

    public static BackgroundKnowledge aggregate(BackgroundKnowledge... knowledgeBases) {
        return aggregate(Arrays.asList(knowledgeBases));

    }

    public static BackgroundKnowledge aggregate(Collection<BackgroundKnowledge> knowledgeBases) {
        Map<String, Predicate> predicates = new HashMap<>();
        Set<Type> types = new HashSet<>();
        List<Formula> formulas = new ArrayList<>();
        HashMultimap<Type, Entity> entities = HashMultimap.create();
        HashMultimap<Predicate, Grounding> groundings = HashMultimap.create();

        for (BackgroundKnowledge knowledge : knowledgeBases) {
            predicates.putAll(knowledge.getPredicates());
            types.addAll(knowledge.getTypes());
            entities.putAll(knowledge.getEntities());
            formulas.addAll(knowledge.getFormulas());
            groundings.putAll(knowledge.getGroundings());
        }
        return new AggregatedBackgroundKnowledge(predicates, types, formulas, entities, groundings);
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
            mlnWriter.newLine();
            for (Formula formula : getFormulas()) {
                mlnWriter.write(formula.getFormula());
                mlnWriter.newLine();
            }
            mlnWriter.flush();
        } catch (IOException e) {
            throw new RoCAException("Cannot write model file.", e);
        }

        try (BufferedWriter evidenceWriter = Files.newBufferedWriter(evidence)) {
            HashMultimap<Type, Entity> entities = getEntities();
            entities.values().stream()
                    .forEach(consumer(
                            e -> {
                                evidenceWriter.write(e.getType().getName());
                                evidenceWriter.write("(\"");
                                evidenceWriter.write(e.getName());
                                evidenceWriter.write("\")");
                                evidenceWriter.newLine();
                            }));

            Multimap<Predicate, Grounding> groundings = getGroundings();
            groundings.values().stream()
                    .forEach(consumer(g -> {
                        Predicate predicate = g.getPredicate();
                        if (predicate.isNegated()) {
                            evidenceWriter.write('!');
                        }

                        evidenceWriter.write(predicate.getName());
                        evidenceWriter.write('(');
                        List<Entity> values = g.getValues();
                        for (int i = 0; i < values.size() - 1; i++) {
                            Entity value = values.get(i);
                            if (!NumberUtils.isNumber(value.getName())) {
                                evidenceWriter.write('"');
                                evidenceWriter.write(value.getName());
                                evidenceWriter.write('_');
                                evidenceWriter.write(Integer.toString(value.getId()));
                                evidenceWriter.write('"');
                            } else {
                                evidenceWriter.write(value.getName());
                            }
                            evidenceWriter.write(',');
                        }
                        Entity value = values.get(values.size() - 1);
                        if (!NumberUtils.isNumber(value.getName())) {
                            evidenceWriter.write('"');
                            evidenceWriter.write(value.getName());
                            evidenceWriter.write('_');
                            evidenceWriter.write(Integer.toString(value.getId()));
                            evidenceWriter.write('"');
                        } else {
                            evidenceWriter.write(value.getName());
                        }

                        evidenceWriter.write(')');
                        evidenceWriter.newLine();
                    }));
            evidenceWriter.flush();
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
     * @return the formulas
     */
    public List<Formula> getFormulas();

    /**
     * @return the entities
     */
    public HashMultimap<Type, Entity> getEntities();

    /**
     * @return the groundings
     */
    public HashMultimap<Predicate, Grounding> getGroundings();

}
