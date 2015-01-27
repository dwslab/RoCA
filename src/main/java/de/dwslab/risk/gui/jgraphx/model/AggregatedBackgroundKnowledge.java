package de.dwslab.risk.gui.jgraphx.model;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * @author Joerg Schoenfisch
 */
class AggregatedBackgroundKnowledge implements BackgroundKnowledge {

    private final Map<String, Predicate> predicates;
    private final Set<String> types;
    private final Map<String, Set<String>> entities;
    private final Map<String, Set<Literal>> groundings;

    AggregatedBackgroundKnowledge(Map<String, Predicate> predicates, Set<String> types,
            Map<String, Set<String>> entities, Map<String, Set<Literal>> groundings) {
        this.predicates = predicates;
        this.types = types;
        this.entities = entities;
        this.groundings = groundings;
    }

    @Override
    public Map<String, Predicate> getPredicates() {
        return Collections.unmodifiableMap(predicates);
    }

    @Override
    public Set<String> getTypes() {
        return Collections.unmodifiableSet(types);
    }

    @Override
    public Map<String, Set<String>> getEntities() {
        return Collections.unmodifiableMap(entities);
    }

    @Override
    public Map<String, Set<Literal>> getGroundings() {
        return Collections.unmodifiableMap(groundings);
    }

}