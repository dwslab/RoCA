package de.dwslab.risk.gui.model;

import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import de.dwslab.risk.gui.exception.RoCAException;

public class Entity implements UserObject {

    private static final long serialVersionUID = -566950411586680045L;

    private static final AtomicInteger NEXT_ID = new AtomicInteger();
    private static final Cache<Integer, Entity> BY_ID = CacheBuilder.newBuilder().weakValues()
            .build();
    private static final Cache<String, Entity> BY_NAME = CacheBuilder.newBuilder().weakValues()
            .build();
    private final int id = NEXT_ID.getAndIncrement();

    private String name;
    private Boolean offline;
    private Type type;

    protected Entity(String name, Type type) {
        this(name, type, null);
    }

    private Entity(String name, Type type, Boolean offline) {
        this.name = name;
        this.type = type;
        this.offline = offline;
    }

    public static Entity get(int id) {
        Entity entity = BY_ID.getIfPresent(id);
        if (entity == null) {
            throw new RoCAException("Entity not found: ID " + id);
        }
        return entity;
    }

    /**
     * Get the entity with the specified name if it exists, throw an exception otherwise. Beware
     * that this method returns the last created entity with that name. There might be additional
     * entities with the same name but a different ID.
     *
     * @param name
     * @return
     * @throws RoCAException
     */
    public static Entity get(String name) throws RoCAException {
        Entity entity = BY_NAME.getIfPresent(name);
        if (entity == null) {
            throw new RoCAException("Entity not found: " + name);
        }
        return entity;
    }

    public static Entity get(String name, Type type) {
        Entity entity = BY_NAME.getIfPresent(name);
        if (entity == null) {
            entity = create(name, type);
        }
        return entity;
    }

    public static Entity get(String name, Type type, Boolean offline) {
        Entity entity = BY_NAME.getIfPresent(name);
        if (entity == null) {
            entity = create(name, type, offline);
        }
        return entity;
    }

    public static Entity create(String name, Type type) {
        Entity entity;
        if (!"redundancy".equals(type.getName())) {
            entity = new Entity(name, type);
        } else {
            entity = new Redundancy(name);
        }
        BY_NAME.put(name, entity);
        BY_ID.put(entity.getId(), entity);
        return entity;
    }

    public static Entity create(String name, Type type, Boolean offline) {
        Entity entity = new Entity(name, type, offline);
        BY_NAME.put(name, entity);
        BY_ID.put(entity.getId(), entity);
        return entity;
    }

    protected int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getOffline() {
        return offline;
    }

    public void setOffline(Boolean offline) {
        this.offline = offline;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    @Override
    public void accept(UserObjectVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((offline == null) ? 0 : offline.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Entity)) {
            return false;
        }
        Entity other = (Entity) obj;
        if (id != other.id) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (offline == null) {
            if (other.offline != null) {
                return false;
            }
        } else if (!offline.equals(other.offline)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return type.getName() + "(" + (Boolean.TRUE.equals(offline) ? "!" : "")
                + (offline == null ? "?" : "") + name + "_" + id + ")";
    }
}
