package de.dwslab.risk.gui.model;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.HashMultimap;

public class MlnBackgroundKnowledgeTest {

    private static final Path mln = Paths.get("src/test/resources/test4_2.mln");
    private static final Path evidence = Paths.get("src/test/resources/test4_2.db");

    @Test
    public void testGetPredicates() throws Exception {
        BackgroundKnowledge kb = new MlnBackgroundKnowledge(mln.toUri().toURL(), evidence);
        Map<String, Predicate> predicates = kb.getPredicates();
        for (String name : predicates.keySet()) {
            System.out.println(name);
        }
        Assert.assertEquals("Wrong number of predicates", 4, predicates.size());
    }

    @Test
    public void testGetTypes() throws Exception {
        BackgroundKnowledge kb = new MlnBackgroundKnowledge(mln.toUri().toURL(), evidence);
        Set<Type> types = kb.getTypes();
        for (Type name : types) {
            System.out.println(name);
        }
        Assert.assertEquals("Wrong number of types", 3, types.size());
    }

    @Test
    public void testGetEntities() throws Exception {
        BackgroundKnowledge kb = new MlnBackgroundKnowledge(mln.toUri().toURL(), evidence);
        HashMultimap<Type, Entity> entities = kb.getEntities();
        Assert.assertEquals("Wrong number of entities", 11, entities.size());
    }

    @Test
    public void testGetGroundings() throws Exception {
        BackgroundKnowledge kb = new MlnBackgroundKnowledge(mln.toUri().toURL(), evidence);
        HashMultimap<Predicate, Grounding> groundings = kb.getGroundings();
        Assert.assertEquals("Wrong number of groundings", 8, groundings.size());
    }

}
