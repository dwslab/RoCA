package de.dwslab.risk.gui.jgraphx.actions;

import static java.awt.Dialog.ModalityType.APPLICATION_MODAL;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import de.dwslab.risk.gui.RoCA;
import de.dwslab.risk.gui.exception.RoCAException;
import de.dwslab.risk.gui.jgraphx.model.BackgroundKnowledge;
import de.dwslab.risk.gui.jgraphx.model.MlnBackgroundKnowledge;

public class LoadBackgroundKnowledgeAction extends AbstractAction {

    private final KnowledgeType type;
    private RoCA roca;

    public LoadBackgroundKnowledgeAction(KnowledgeType type, RoCA roca) {
        this.type = type;
        this.roca = roca;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (type) {
        case MLN:
            MlnFileChooserDialog dialog = new MlnFileChooserDialog(
                    SwingUtilities.getWindowAncestor(roca));
            dialog.pack();
            dialog.setModalityType(APPLICATION_MODAL);
            dialog.setLocationRelativeTo(roca);
            dialog.addWindowListener(new WindowAdapter() {

                @Override
                public void windowClosed(WindowEvent e) {
                    new Thread(() -> {
                        BackgroundKnowledge knowledge = new MlnBackgroundKnowledge(
                                dialog.getPathMln(), dialog.getPathEvidence());
                        roca.handleKnowledgeUpdate(knowledge);
                    }).start();
                    super.windowClosed(e);
                }
            });
            dialog.setVisible(true);
            break;
        case ONTOLOGY:
            throw new RoCAException("Loading ontologies not possible yet");
            // break;
        default:
            throw new RoCAException("Unknown background knowledge type: " + type);
        }
    }

    public static enum KnowledgeType {
        ONTOLOGY,
        MLN;
    }

    private static class MlnFileChooserDialog extends JDialog {

        private Path pathMln;
        private Path pathEvidence;

        public MlnFileChooserDialog(Window parent) {
            super(parent, "MLN & Evidence auswählen");
            JPanel panel = new JPanel(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();

            c.fill = GridBagConstraints.HORIZONTAL;
            c.insets = new Insets(10, 10, 0, 10);
            c.gridx = 0;
            c.gridy = 0;
            c.gridwidth = 2;
            panel.add(new JLabel("MLN file:"), c);

            c.gridx = 2;
            JTextField textFieldMln = new JTextField(50);
            panel.add(textFieldMln, c);

            JButton buttonMln = new JButton("Durchsuchen...");
            buttonMln.addActionListener(l -> {
                JFileChooser fileChooser = new JFileChooser("src/test/resources/");
                int returnVal = fileChooser.showOpenDialog(MlnFileChooserDialog.this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    textFieldMln.setText(fileChooser.getSelectedFile().getPath());
                }
            });
            c.gridx = 4;
            c.gridwidth = 2;
            panel.add(buttonMln, c);

            c.gridx = 0;
            c.gridy = 1;
            c.gridwidth = 2;
            panel.add(new JLabel("Evidence file:"), c);

            c.gridx = 2;
            c.gridwidth = 2;
            JTextField textFieldEvidence = new JTextField(50);
            panel.add(textFieldEvidence, c);

            JButton buttonEvidence = new JButton("Durchsuchen...");
            buttonEvidence.addActionListener(l -> {
                JFileChooser fileChooser = new JFileChooser("src/test/resources/");
                int returnVal = fileChooser.showOpenDialog(MlnFileChooserDialog.this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    textFieldEvidence.setText(fileChooser.getSelectedFile().getPath());
                }
            });
            c.gridx = 4;
            c.gridwidth = 2;
            panel.add(buttonEvidence, c);

            c.insets = new Insets(10, 10, 10, 10);
            c.weightx = 1;
            c.gridwidth = 1;
            c.gridx = 2;
            c.gridy = 2;
            JButton buttonOk = new JButton("OK");
            buttonOk.addActionListener(l -> {
                setVisible(false);
                pathMln = Paths.get(textFieldMln.getText());
                pathEvidence = Paths.get(textFieldEvidence.getText());
                textFieldMln.setText(null);
                textFieldEvidence.setText(null);
            });
            panel.add(buttonOk, c);

            c.gridx = 3;
            c.gridy = 2;
            JButton buttonCancel = new JButton("Abbrechen");
            buttonCancel.addActionListener(l -> {
                pathMln = null;
                pathEvidence = null;
                textFieldMln.setText(null);
                textFieldEvidence.setText(null);
            });
            panel.add(buttonCancel, c);

            add(panel);
        }

        /**
         * @return the pathMln
         */
        public Path getPathMln() {
            return pathMln;
        }

        /**
         * @return the pathEvidence
         */
        public Path getPathEvidence() {
            return pathEvidence;
        }

    }

}
