package de.dwslab.risk.gui.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.mxgraph.model.mxCell;
import com.mxgraph.view.mxGraph;

public class GuiBackgroundKnowledge implements BackgroundKnowledge {

    private final Map<String, Predicate> predicates;
    private final Set<Type> types;
    private final HashMultimap<Type, Entity> entities;
    private final HashMultimap<Predicate, Grounding> groundings;
    private final List<Formula> formulas;

    public GuiBackgroundKnowledge(mxGraph graph) {
        predicates = new HashMap<>();
        types = new HashSet<>();
        entities = HashMultimap.create();
        groundings = HashMultimap.create();
        formulas = new ArrayList<>();

        KnowledgeGraphVisitor visitor = new KnowledgeGraphVisitor(predicates, types, formulas,
                entities, groundings);
        mxCell parent = (mxCell) graph.getDefaultParent();
        for (int i = 0; i < parent.getChildCount(); i++) {
            mxCell cell = (mxCell) parent.getChildAt(i);
            UserObject object = (UserObject) cell.getValue();
            object.accept(visitor);
        }
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

    private static class KnowledgeGraphVisitor implements UserObjectVisitor {

        private final Map<String, Predicate> predicates;
        private final Set<Type> types;
        private final HashMultimap<Type, Entity> entities;
        private final HashMultimap<Predicate, Grounding> groundings;
        private final List<Formula> formulas;

        public KnowledgeGraphVisitor(Map<String, Predicate> predicates, Set<Type> types,
                List<Formula> formulas, HashMultimap<Type, Entity> entities,
                HashMultimap<Predicate, Grounding> groundings) {
            this.predicates = predicates;
            this.types = types;
            this.formulas = formulas;
            this.entities = entities;
            this.groundings = groundings;
        }

        @Override
        public void visit(Entity entity) {
            entities.put(entity.getType(), entity);
        }

        @Override
        public void visit(Formula formula) {
            formulas.add(formula);
        }

        @Override
        public void visit(Grounding grounding) {
            groundings.put(grounding.getPredicate(), grounding);
        }

        @Override
        public void visit(Predicate predicate) {
            predicates.put(predicate.getName(), predicate);
        }

        @Override
        public void visit(Type type) {
            types.add(type);
        }

    }

}
