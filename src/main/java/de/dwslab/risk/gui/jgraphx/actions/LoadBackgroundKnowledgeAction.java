package de.dwslab.risk.gui.jgraphx.actions;

import static java.awt.Dialog.ModalityType.APPLICATION_MODAL;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.net.URL;
import java.nio.file.Paths;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

import de.dwslab.risk.gui.RoCA;
import de.dwslab.risk.gui.exception.RoCAException;
import de.dwslab.risk.gui.model.BackgroundKnowledge;
import de.dwslab.risk.gui.model.MlnBackgroundKnowledge;

public class LoadBackgroundKnowledgeAction extends AbstractAction {

    private static final long serialVersionUID = -6601753337328725L;

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
            dialog.setVisible(true);
            break;
        case ONTOLOGY:
            throw new RoCAException("Loading ontologies not possible yet");
        default:
            throw new RoCAException("Unknown background knowledge type: " + type);
        }
    }

    public static enum KnowledgeType {
        ONTOLOGY,
        MLN;
    }

    private class MlnFileChooserDialog extends JDialog {

        private static final long serialVersionUID = -6670451737041631711L;

        public MlnFileChooserDialog(Window parent) {
            super(parent, "Evidence File auswählen");
            JPanel panel = new JPanel(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();

            c.fill = GridBagConstraints.HORIZONTAL;
            c.insets = new Insets(10, 10, 0, 10);
            c.gridx = 0;
            c.gridy = 0;
            c.gridwidth = 2;
            // panel.add(new JLabel("MLN file:"), c);
            //
            // c.gridx = 2;
            // JTextField textFieldMln = new JTextField(50);
            // textFieldMln
            // .setText("D:\\Documents\\3000 Projekte\\2013 Risikomanagement\\workspace\\RoCA\\src\\test\\resources\\test4_2.mln");
            // panel.add(textFieldMln, c);
            //
            // JButton buttonMln = new JButton("Durchsuchen...");
            // buttonMln.addActionListener(l -> {
            // JFileChooser fileChooser = new JFileChooser("src/test/resources/");
            // fileChooser.setFileFilter(new FileNameExtensionFilter("MLN Files", "mln"));
            // int returnVal = fileChooser.showOpenDialog(MlnFileChooserDialog.this);
            // if (returnVal == JFileChooser.APPROVE_OPTION) {
            // textFieldMln.setText(fileChooser.getSelectedFile().getPath());
            // }
            // });
            // c.gridx = 4;
            // c.gridwidth = 2;
            // panel.add(buttonMln, c);

            c.gridx = 0;
            c.gridy = 1;
            c.gridwidth = 2;
            panel.add(new JLabel("Evidence file:"), c);

            c.gridx = 2;
            c.gridwidth = 2;
            JTextField textFieldEvidence = new JTextField(50);
            // textFieldEvidence
            // .setText("D:\\Documents\\3000 Projekte\\2013 Risikomanagement\\workspace\\RoCA\\src\\test\\resources\\test4_3.db");
            panel.add(textFieldEvidence, c);

            JButton buttonEvidence = new JButton("Durchsuchen...");
            buttonEvidence.addActionListener(l -> {
                JFileChooser fileChooser = new JFileChooser("src/test/resources/");
                fileChooser.setFileFilter(new FileNameExtensionFilter("Evidence Files", "db"));
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
                new Thread(() -> {
                    URL mln = MlnFileChooserDialog.class.getResource("/default.mln");
                    BackgroundKnowledge knowledge = new MlnBackgroundKnowledge(
                            mln, Paths.get(textFieldEvidence.getText()));
                    roca.handleKnowledgeUpdate(knowledge);
                }).start();
            });
            getRootPane().setDefaultButton(buttonOk);
            panel.add(buttonOk, c);

            c.gridx = 3;
            c.gridy = 2;
            JButton buttonCancel = new JButton("Abbrechen");
            buttonCancel.addActionListener(l -> {
                setVisible(false);
            });
            panel.add(buttonCancel, c);

            getRootPane().getInputMap()
                    .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "CANCEL");
            getRootPane().getActionMap().put("CANCEL", new AbstractAction() {

                private static final long serialVersionUID = -5058924623712220514L;

                @Override
                public void actionPerformed(ActionEvent e) {
                    buttonCancel.doClick();
                }
            });

            add(panel);
        }
    }

}
