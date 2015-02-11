package de.dwslab.risk.gui.model;

public interface UserObjectVisitor {

    public void visit(Entity entity);

    public void visit(Formula formula);

    public void visit(Grounding grounding);

    public void visit(Predicate predicate);

    public void visit(Type type);

}
