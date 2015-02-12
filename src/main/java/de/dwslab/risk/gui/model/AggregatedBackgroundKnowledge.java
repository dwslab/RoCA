package de.dwslab.risk.gui.model;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.HashMultimap;

/**
 * @author Joerg Schoenfisch
 */
class AggregatedBackgroundKnowledge extends AbstractBackgroundKnowledge {

    private final Map<String, Predicate> predicates;
    private final Set<Type> types;
    private final HashMultimap<Type, Entity> entities;
    private final HashMultimap<Predicate, Grounding> groundings;
    private final List<Formula> formulas;

    AggregatedBackgroundKnowledge(Map<String, Predicate> predicates, Set<Type> types,
            List<Formula> formulas, HashMultimap<Type, Entity> entities,
            HashMultimap<Predicate, Grounding> groundings) {
        this.predicates = predicates;
        this.types = types;
        this.formulas = formulas;
        this.entities = entities;
        this.groundings = groundings;
    }

    @Override
    public Map<String, Predicate> getPredicates() {
        return predicates;
    }

    @Override
    public Set<Type> getTypes() {
        return types;
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
