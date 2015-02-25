package de.dwslab.risk.gui.model;

import static org.junit.Assert.assertEquals;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

import de.dwslab.risk.gui.RoCA;

public class RoCATest {

    private static final Path mln = Paths.get("src/test/resources/test4_2.mln");
    private static final Path evidence = Paths.get("src/test/resources/test4_2.db");

    @Test
    public void testGetBackgroundKnowledge() throws Exception {
        BackgroundKnowledge kb = new MlnBackgroundKnowledge(mln.toUri().toURL(), evidence);
        RoCA roca = new RoCA();

        roca.handleKnowledgeUpdate(kb);
        BackgroundKnowledge graphKb = roca.getBackgroundKnowledge();

        assertEquals("Predicates do not match", kb.getPredicates(), graphKb.getPredicates());
        assertEquals("Types do not match", kb.getTypes(), graphKb.getTypes());
        assertEquals("Formulas do not match", kb.getFormulas(), graphKb.getFormulas());
        assertEquals("Entities do not match", kb.getEntities(), graphKb.getEntities());
        assertEquals("Groundings do not match", kb.getGroundings(), graphKb.getGroundings());

        assertEquals("Knowledge bases do not match", kb, graphKb);
    }

}
