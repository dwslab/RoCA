package de.dwslab.risk.gui.jgraphx.model;

import java.util.Arrays;
import java.util.Collection;

import de.dwslab.risk.gui.exception.RoCAException;

public interface BackgroundKnowledge {

    public static BackgroundKnowledge aggregate(BackgroundKnowledge... knowledgeBases) {
        return aggregate(Arrays.asList(knowledgeBases));

    }

    public static BackgroundKnowledge aggregate(Collection<BackgroundKnowledge> knowledgeBases) {

        for (BackgroundKnowledge knowledge : knowledgeBases) {

        }

        throw new RoCAException("Aggregation of background knowledge not implemented.");
    }
}
