package de.dwslab.risk.gui.jgraphx.actions;

import static de.dwslab.ai.util.Utils.createTempFile;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.ProgressMonitor;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.googlecode.rockit.app.RockItAPI;
import com.googlecode.rockit.app.result.RockItResult;

import de.dwslab.ai.riskmanagement.abduction.existential.ExistentialApi;
import de.dwslab.risk.gui.RoCA;

public class RootCauseAnalysisAction extends AbstractAction {

    private static final long serialVersionUID = -8773333133469858955L;
    private static final Logger logger = LogManager.getLogger();

    private RoCA roca;

    public RootCauseAnalysisAction(RoCA roca) {
        this.roca = roca;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        RootCauseAnalysisWorker worker = new RootCauseAnalysisWorker(roca);
        worker.execute();
    }

    private static class RootCauseAnalysisWorker extends SwingWorker<Void, Void> {

        private final RoCA roca;
        private ProgressMonitor monitor;

        public RootCauseAnalysisWorker(RoCA roca) {
            this.roca = roca;
            monitor = new ProgressMonitor(
                    SwingUtilities.getWindowAncestor(roca),
                    "Running Root Cause Analysis...", "Please wait...", 0, 100);
            monitor.setMillisToPopup(0);
            monitor.setMillisToDecideToPopup(0);
        }

        @Override
        protected Void doInBackground() throws Exception {
            // Export the background knwoledge to temporary files
            File mlnFile = createTempFile("mln-", ".mln");
            File evidenceFile = createTempFile("evidence-", ".db");
            roca.getBackgroundKnowledge().exportAsMln(mlnFile.toPath(), evidenceFile.toPath());

            // Extend the MLN for abductive reasoning
            ExistentialApi api = new ExistentialApi();
            File mlnExtFile = createTempFile("mln-", ".mln");
            File evidenceExtFile = createTempFile("evidence-", ".db");
            api.existenitalApi(mlnFile.getAbsolutePath(), evidenceFile.getAbsolutePath(),
                    mlnExtFile.getAbsolutePath(), evidenceExtFile.getAbsolutePath());

            // Run RockIt
            RockItAPI rockit = new RockItAPI("src/main/resources/rockit.properties");
            List<RockItResult> mapState = rockit.doMapState(mlnExtFile.getAbsolutePath(),
                    evidenceExtFile.getAbsolutePath());

            // Process the result
            mapState.forEach(m -> System.err.println(m));

            return null;
        }

        @Override
        protected void done() {
            monitor.close();
        }

    }

}
