package de.dwslab.risk.gui.model;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Joerg Schoenfisch
 */
class AggregatedBackgroundKnowledge implements BackgroundKnowledge {

    private final Map<String, Predicate> predicates;
    private final Set<Type> types;
    private final Map<Type, Set<Entity>> entities;
    private final Map<Predicate, Set<Grounding>> groundings;
    private List<Formula> formulas;

    AggregatedBackgroundKnowledge(Map<String, Predicate> predicates, Set<Type> types,
            List<Formula> formulas, Map<Type, Set<Entity>> entities,
            Map<Predicate, Set<Grounding>> groundings) {
        this.predicates = predicates;
        this.types = types;
        this.formulas = formulas;
        this.entities = entities;
        this.groundings = groundings;
    }

    @Override
    public Map<String, Predicate> getPredicates() {
        return Collections.unmodifiableMap(predicates);
    }

    @Override
    public Set<Type> getTypes() {
        return Collections.unmodifiableSet(types);
    }

    @Override
    public List<Formula> getFormulas() {
        return formulas;
    }

    @Override
    public Map<Type, Set<Entity>> getEntities() {
        return Collections.unmodifiableMap(entities);
    }

    @Override
    public Map<Predicate, Set<Grounding>> getGroundings() {
        return Collections.unmodifiableMap(groundings);
    }

}
