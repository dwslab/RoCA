package de.dwslab.risk.gui.jgraphx.actions;

import static de.dwslab.ai.util.Utils.createTempFile;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.apache.logging.log4j.Level.INFO;

import java.awt.event.ActionEvent;
import java.io.File;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.ProgressMonitor;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.googlecode.rockit.app.RockItAPI;
import com.googlecode.rockit.app.result.RockItResult;

import de.dwslab.ai.riskmanagement.existential.ExistentialApi;
import de.dwslab.risk.gui.RoCA;
import de.dwslab.risk.gui.exception.RoCAException;

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

    private static class RootCauseAnalysisWorker extends SwingWorker<List<String>, Integer> {

        private final RoCA roca;
        private final ProgressMonitor monitor;
        private Exception e;

        public RootCauseAnalysisWorker(RoCA roca) {
            this.roca = roca;
            monitor = new ProgressMonitor(SwingUtilities.getWindowAncestor(roca),
                    "Running Root Cause Analysis...", "Please wait...", 0, 100);
            monitor.setMillisToPopup(0);
            monitor.setMillisToDecideToPopup(0);
        }

        @Override
        protected List<String> doInBackground() throws Exception {
            try {
                // Export the background knowledge to temporary files
                logger.log(INFO, "exporting to temporary file");
                monitor.setProgress(1);
                monitor.setNote("Generating temporary MLN files...");
                File mlnFile = createTempFile("mln-", ".mln");
                File evidenceFile = createTempFile("evidence-", ".db");
                roca.getBackgroundKnowledge().exportAsMln(mlnFile.toPath(), evidenceFile.toPath());
                monitor.setProgress(10);

                // Extend the MLN for abductive reasoning
                logger.log(INFO, "extending MLN");
                monitor.setNote("Extending MLN for root cause analysis...");
                ExistentialApi api = new ExistentialApi();
                File mlnExtFile = createTempFile("existential-mln-", ".mln");
                api.existentialApi(mlnFile.getAbsolutePath(), evidenceFile.getAbsolutePath(),
                        mlnExtFile.getAbsolutePath(),
                        createTempFile("existential-evidence-", ".db").getAbsolutePath());
                monitor.setProgress(20);

                // Run RockIt
                logger.log(INFO, "running map inference");
                monitor.setNote("Executing MAP inference...");
                RockItAPI rockit = new RockItAPI();
                List<RockItResult> mapState = rockit.doMapState(mlnExtFile.getAbsolutePath(),
                        evidenceFile.getAbsolutePath());
                monitor.setProgress(90);

                Set<String> provided = Files.lines(evidenceFile.toPath())
                        .map(String::trim)
                        .collect(toSet());

                // Process the result
                logger.log(INFO, "processing result");
                monitor.setNote("Processing MAP result...");
                List<String> rootCause = mapState
                        .stream()
                        .filter(m -> !provided.contains(m.getStatement()))
                        .map(m -> {
                            StringBuilder str = new StringBuilder();
                            str.append(m.getPredicate());
                            str.append('(');
                            m.getObjects()
                                    .stream()
                                    .forEach(s -> {
                                        str.append(s.substring(0, s.lastIndexOf('_')));
                                        str.append(',');
                                    });
                            str.deleteCharAt(str.length() - 1);
                            str.append(')');
                            return str.toString();
                        }).collect(toList());
                monitor.setProgress(100);

                Collections.sort(rootCause, (o1, o2) -> o1.toString().compareTo(o2.toString()));
                return rootCause;
            } catch (Exception e) {
                this.e = e;
                throw e;
            }
        }

        @Override
        protected void done() {
            monitor.close();
            if (e != null) {
                throw new RoCAException("Exception during root cause analysis", e);
            } else {
                try {
                    List<String> rootCause = get();
                    StringBuilder message = new StringBuilder();
                    message.append("Proposed root cause:\n");
                    rootCause.forEach(r -> {
                        message.append('\t');
                        message.append(r);
                        message.append('\n');
                    });
                    JOptionPane.showMessageDialog(roca, message);
                } catch (Exception e) {
                    throw new RoCAException("Should Not Happen™", e);
                }
            }
        }
    }

}
