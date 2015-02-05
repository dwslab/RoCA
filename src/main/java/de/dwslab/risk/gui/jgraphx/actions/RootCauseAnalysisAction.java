package de.dwslab.risk.gui.jgraphx.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ProgressMonitor;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import de.dwslab.risk.gui.RoCA;

public class RootCauseAnalysisAction extends AbstractAction {

    private static final long serialVersionUID = -8773333133469858955L;
    private RoCA roca;

    public RootCauseAnalysisAction(RoCA roca) {
        this.roca = roca;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        ProgressMonitor monitor = new ProgressMonitor(
                SwingUtilities.getWindowAncestor(roca),
                "Running Root Cause Analysis...", "Please wait...", 0, 100);
        monitor.setMillisToPopup(0);
        monitor.setMillisToDecideToPopup(0);

        SwingWorker<Void, Void> sw = new SwingWorker<Void, Void>() {

            @Override
            protected Void doInBackground() throws Exception {
                // TODO convert the internal graph model to a rockit model

                // TODO extend the model for abductive reasoning

                // TODO call the rockit API

                // TODO retrieve the result

                // TODO diff the result and show the root cause

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
