package de.dwslab.risk.gui.jgraphx.actions;

import static de.dwslab.ai.util.Utils.createTempFile;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.ProgressMonitor;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import com.googlecode.rockit.app.RockItAPI;
import com.googlecode.rockit.app.result.RockItResult;

import de.dwslab.risk.gui.RoCA;

public class RootCauseAnalysisAction extends AbstractAction {

    private static final long serialVersionUID = -8773333133469858955L;
    private RoCA roca;

    public RootCauseAnalysisAction(RoCA roca) {
        this.roca = roca;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.err.println("action performed");

        ProgressMonitor monitor = new ProgressMonitor(
                SwingUtilities.getWindowAncestor(roca),
                "Running Root Cause Analysis...", "Please wait...", 0, 100);
        monitor.setMillisToPopup(0);
        monitor.setMillisToDecideToPopup(0);

        SwingWorker<Void, Void> sw = new SwingWorker<Void, Void>() {

            @Override
            protected Void doInBackground() throws Exception {
                System.err.println("doing in background");

                File mlnFile = createTempFile("mln-", ".mln");
                File evidenceFile = createTempFile("evidence-", ".db");

                roca.getBackgroundKnowledge().exportAsMln(mlnFile.toPath(), evidenceFile.toPath());

                RockItAPI rockit = new RockItAPI("src/main/resources/rockit.properties");
                List<RockItResult> mapState = rockit.doMapState(mlnFile.getAbsolutePath(),
                        evidenceFile.getAbsolutePath());

                mapState.forEach(m -> System.err.println(m));

                System.err.println(mapState.size());

                return null;
            }

            @Override
            protected void done() {
                monitor.close();
            }
        };

        sw.execute();
    }
}
