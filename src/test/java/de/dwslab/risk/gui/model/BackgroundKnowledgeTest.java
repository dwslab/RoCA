package de.dwslab.risk.gui.model;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

import de.dwslab.ai.util.Utils;

public class BackgroundKnowledgeTest {

    private static final Path mln = Paths.get("src/test/resources/test4_2.mln");
    private static final Path evidence = Paths.get("src/test/resources/test4_2.db");

    @Test
    public void testExportAsMln() throws Exception {
        BackgroundKnowledge knowledge = new MlnBackgroundKnowledge(mln.toUri().toURL(), evidence);

        Path tempMln = Utils.createTempPath("mln-", ".mln");
        Path tempEvidence = Utils.createTempPath("evidence-", ".db");

        knowledge.exportAsMln(tempMln, tempEvidence);
    }

}
