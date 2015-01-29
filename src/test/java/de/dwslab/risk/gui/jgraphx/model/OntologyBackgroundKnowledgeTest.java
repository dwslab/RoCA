package de.dwslab.risk.gui.jgraphx.model;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import de.dwslab.risk.gui.model.BackgroundKnowledge;
import de.dwslab.risk.gui.model.Grounding;
import de.dwslab.risk.gui.model.OntologyBackgroundKnowledge;
import de.dwslab.risk.gui.model.Predicate;

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
        Set<String> types = kb.getTypes();
        for (String name : types) {
            System.out.println(name);
        }
        Assert.assertEquals("Wrong number of types", 3, types.size());
    }

    @Test
    public void testGetEntities() {
        BackgroundKnowledge kb = new OntologyBackgroundKnowledge(ontology);
        Map<String, Set<String>> entities = kb.getEntities();
        int size = 0;
        for (Set<String> names : entities.values()) {
            System.out.println(names);
            size += names.size();
        }
        Assert.assertEquals("Wrong number of entities", 11, size);
    }

    @Test
    public void testGetGroundings() {
        BackgroundKnowledge kb = new OntologyBackgroundKnowledge(ontology);
        Map<String, Set<Grounding>> groundings = kb.getGroundings();
        int size = 0;
        for (Set<Grounding> names : groundings.values()) {
            System.out.println(names);
            size += names.size();
        }
        Assert.assertEquals("Wrong number of groundings", 11, size);
    }

}
