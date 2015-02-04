package de.dwslab.risk.gui.model;

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
