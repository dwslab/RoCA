package de.dwslab.risk.gui.model;

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

    public GuiBackgroundKnowledge(mxGraph graph, BackgroundKnowledge knowledge) {
        predicates = knowledge.getPredicates();
        types = knowledge.getTypes();
        formulas = knowledge.getFormulas();

        entities = HashMultimap.create();
        groundings = HashMultimap.create();

        KnowledgeGraphVisitor visitor = new KnowledgeGraphVisitor(entities, groundings);
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

        private final HashMultimap<Type, Entity> entities;
        private final HashMultimap<Predicate, Grounding> groundings;

        public KnowledgeGraphVisitor(HashMultimap<Type, Entity> entities,
                HashMultimap<Predicate, Grounding> groundings) {
            this.entities = entities;
            this.groundings = groundings;
        }

        @Override
        public void visit(Entity entity) {
            entities.put(entity.getType(), entity);
        }

        @Override
        public void visit(Formula formula) {
            // ignore
        }

        @Override
        public void visit(Grounding grounding) {
            groundings.put(grounding.getPredicate(), grounding);
        }

        @Override
        public void visit(Predicate predicate) {
            // ignore
        }

        @Override
        public void visit(Type type) {
            // ignore
        }

    }

}