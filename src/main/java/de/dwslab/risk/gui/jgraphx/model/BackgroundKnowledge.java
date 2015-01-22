package de.dwslab.risk.gui.jgraphx.model;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public interface BackgroundKnowledge {

    public static BackgroundKnowledge aggregate(BackgroundKnowledge... knowledgeBases) {
        return aggregate(Arrays.asList(knowledgeBases));

    }

    public static BackgroundKnowledge aggregate(Collection<BackgroundKnowledge> knowledgeBases) {
        Map<String, Predicate> predicates = new HashMap<>();
        Set<String> types = new HashSet<>();
        Map<String, Set<String>> entities = new HashMap<>();
        Map<String, Set<Literal>> groundings = new HashMap<>();

        for (BackgroundKnowledge knowledge : knowledgeBases) {
            for (Entry<String, Predicate> entry : knowledge.getPredicates().entrySet()) {
                if (!predicates.containsKey(entry.getKey())) {
                    predicates.put(entry.getKey(), entry.getValue());
                }
            }
            types.addAll(knowledge.getTypes());
            for (Entry<String, Set<String>> entry : knowledge.getEntities().entrySet()) {
                Set<String> set = entities.get(entry.getKey());
                if (set == null) {
                    set = new HashSet<>();
                    entities.put(entry.getKey(), set);
                }
                set.addAll(entry.getValue());
            }
            for (Entry<String, Set<Literal>> entry : knowledge.getGroundings().entrySet()) {
                Set<Literal> set = groundings.get(entry.getKey());
                if (set == null) {
                    set = new HashSet<>();
                    groundings.put(entry.getKey(), set);
                }
                set.addAll(entry.getValue());
            }
        }
        return new AggregatedBackgroundKnowledge(predicates, types, entities, groundings);
    }

    /**
     * @return the predicates
     */
    public Map<String, Predicate> getPredicates();

    /**
     * @return the types
     */
    public Set<String> getTypes();

    /**
     * @return the entities
     */
    public Map<String, Set<String>> getEntities();

    /**
     * @return the groundings
     */
    public Map<String, Set<Literal>> getGroundings();

}
