package de.dwslab.risk.gui.model;

import java.io.Serializable;

public interface UserObject extends Serializable {

    public void accept(UserObjectVisitor visitor);

}
