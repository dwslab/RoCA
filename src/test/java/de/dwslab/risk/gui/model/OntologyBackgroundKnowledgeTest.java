package de.dwslab.risk.gui.model;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.HashMultimap;

public class OntologyBackgroundKnowledgeTest {

    private static final Path ontology = Paths.get("src/test/resources/usecase-printer-prob.owl");

    @Test
    public void testGetPredicates() {
        BackgroundKnowledge kb = new OntologyBackgroundKnowledge(ontology);
        Map<String, Predicate> predicates = kb.getPredicates();
        for (String name : predicates.keySet()) {
            System.out.println(name);
        }
        Assert.assertEquals("Wrong number of predicates", 4, predicates.size());
    }

    @Test
    public void testGetTypes() {
        BackgroundKnowledge kb = new OntologyBackgroundKnowledge(ontology);
        Set<Type> types = kb.getTypes();
        for (Type name : types) {
            System.out.println(name);
        }
        Assert.assertEquals("Wrong number of types", 3, types.size());
    }

    @Test
    public void testGetEntities() {
        BackgroundKnowledge kb = new OntologyBackgroundKnowledge(ontology);
        HashMultimap<Type, Entity> entities = kb.getEntities();
        Assert.assertEquals("Wrong number of entities", 11, entities.size());
    }

    @Test
    public void testGetGroundings() {
        BackgroundKnowledge kb = new OntologyBackgroundKnowledge(ontology);
        HashMultimap<Predicate, Grounding> groundings = kb.getGroundings();
        Assert.assertEquals("Wrong number of groundings", 11, groundings.size());
    }

}
