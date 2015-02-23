package de.dwslab.risk.gui;

import static java.awt.Dialog.ModalityType.APPLICATION_MODAL;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.EventObject;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.view.mxCellEditor;
import com.mxgraph.view.mxCellState;

import de.dwslab.risk.gui.exception.RoCAException;
import de.dwslab.risk.gui.model.Entity;
import de.dwslab.risk.gui.model.Grounding;

public class CustomCellEditor extends mxCellEditor {

    public CustomCellEditor(mxGraphComponent graphComponent) {
        super(graphComponent);
    }

    @Override
    public void startEditing(Object cell, EventObject event) {
        editingCell = cell;

        UserObjectEditDialog dialog = new UserObjectEditDialog((mxCell) cell, event, graphComponent);
        dialog.pack();
        dialog.setModalityType(APPLICATION_MODAL);
        dialog.setLocationRelativeTo(graphComponent);
        dialog.setVisible(true);

        editingCell = null;
    }

    @Override
    public void stopEditing(boolean cancel) {
        // calling super(cancel) causes false labels
        // super.stopEditing(cancel);
    }

    private static class UserObjectEditDialog extends JDialog {

        private final JPanel panel;
        private final mxGraphComponent graphComponent;
        private final mxCell cell;
        private EventObject event;

        public UserObjectEditDialog(mxCell cell, EventObject event, mxGraphComponent graphComponent) {
            super(SwingUtilities.getWindowAncestor(graphComponent), "Eigenschaften");
            this.cell = cell;
            this.event = event;
            this.graphComponent = graphComponent;
            panel = new JPanel(new GridBagLayout());

            if (cell.getValue() instanceof Entity) {
                Entity entity = (Entity) cell.getValue();
                if (entity.getType().getName().equals("infra")) {
                    createDialogInfra(entity);
                } else {
                    createDialogRisk(entity);
                }
            } else if (cell.getValue() instanceof Grounding) {
                Grounding grounding = (Grounding) cell.getValue();
                if (grounding.getPredicate().getName().equals("hasRiskDegree")) {
                    createDialogHasRiskDegree(grounding);
                } else {
                    createDialogDependsOn(grounding);
                }
            } else {
                throw new RoCAException("Unknown graph UserObject: " + cell.getValue() + " "
                        + cell.getValue().getClass());
            }

            add(panel);
        }

        private void updateEdgeLabels() {
            for (int i = 0; i < cell.getEdgeCount(); i++) {
                mxCell edge = (mxCell) cell.getEdgeAt(i);
                Entity entity = (Entity) cell.getValue();
                Grounding grounding = (Grounding) edge.getValue();
                if (edge.getSource() == cell) {
                    grounding.getValues().set(0, entity.getName());
                } else if (edge.getTarget() == cell) {
                    grounding.getValues().set(1, entity.getName());
                } else {
                    throw new RoCAException("Something went wrong");
                }
            }
        }

        private void createDialogInfra(Entity entity) {
            GridBagConstraints c = new GridBagConstraints();

            c.fill = GridBagConstraints.HORIZONTAL;
            c.insets = new Insets(10, 10, 0, 10);
            c.gridx = 0;
            c.gridy = 0;
            panel.add(new JLabel("Name"), c);

            c.gridx = 1;
            JTextField textFieldName = new JTextField(15);
            textFieldName.setText(entity.getName());
            panel.add(textFieldName, c);

            c.gridx = 0;
            c.gridy = 1;
            panel.add(new JLabel("Status"), c);

            c.gridx = 1;
            String[] values = { "online", "unbekannt", "offline" };
            JComboBox<String> comboOffline = new JComboBox<>(values);
            if (TRUE.equals(entity.getOffline())) {
                comboOffline.setSelectedIndex(2);
            } else if (FALSE.equals(entity.getOffline())) {
                comboOffline.setSelectedIndex(0);
            } else {
                comboOffline.setSelectedIndex(1);
            }
            panel.add(comboOffline, c);

            c.insets = new Insets(30, 10, 10, 10);
            c.gridx = 0;
            c.gridy = 2;
            JButton buttonOk = new JButton("OK");
            buttonOk.addActionListener(l -> {
                setVisible(false);
                entity.setName(textFieldName.getText());
                mxCellState state = graphComponent.getGraph().getView().getState(cell, true);
                String style = cell.getStyle();
                int fillIndex = -1;
                if (style != null) {
                    fillIndex = style.indexOf("fillColor");
                }
                switch (comboOffline.getSelectedIndex()) {
                case 0:
                    entity.setOffline(FALSE);
                    if (fillIndex < 0) {

                        cell.setStyle((style == null ? "" : style + ";") + "fillColor=#22ff22");
                    } else {
                        StringBuilder str = new StringBuilder(style);
                        str.replace(fillIndex + 11, fillIndex + 17, "22ff22");
                        cell.setStyle(str.toString());
                    }
                    break;
                case 1:
                    entity.setOffline(null);
                    if (fillIndex < 0) {
                        cell.setStyle((style == null ? "" : style + ";") + "fillColor=#adc5ff");
                    } else {
                        StringBuilder str = new StringBuilder(style);
                        str.replace(fillIndex + 11, fillIndex + 17, "adc5ff");
                        cell.setStyle(str.toString());
                    }
                    break;
                case 2:
                    entity.setOffline(TRUE);
                    if (fillIndex < 0) {
                        cell.setStyle((style == null ? "" : style + ";") + "fillColor=#ff2222");
                    } else {
                        StringBuilder str = new StringBuilder(style);
                        str.replace(fillIndex + 11, fillIndex + 17, "ff2222");
                        cell.setStyle(str.toString());
                    }
                    break;
                default:
                    throw new RoCAException("Unknown ComboBox index");
                }
                updateEdgeLabels();
                graphComponent.redraw(state);
                graphComponent.labelChanged(cell, cell.getValue(), event);
            });
            getRootPane().setDefaultButton(buttonOk);
            panel.add(buttonOk, c);

            c.gridx = 1;
            JButton buttonCancel = new JButton("Abbrechen");
            buttonCancel.addActionListener(l -> {
                setVisible(false);
            });
            panel.add(buttonCancel, c);
        }

        private void createDialogRisk(Entity entity) {
            GridBagConstraints c = new GridBagConstraints();

            c.fill = GridBagConstraints.HORIZONTAL;
            c.insets = new Insets(10, 10, 0, 10);
            c.gridx = 0;
            c.gridy = 0;
            panel.add(new JLabel("Name"), c);

            c.gridx = 1;
            JTextField textFieldName = new JTextField(15);
            textFieldName.setText(entity.getName());
            panel.add(textFieldName, c);

            c.insets = new Insets(30, 10, 10, 10);
            c.gridx = 0;
            c.gridy = 1;
            JButton buttonOk = new JButton("OK");
            buttonOk.addActionListener(l -> {
                setVisible(false);
                entity.setName(textFieldName.getText());
                mxCellState state = graphComponent.getGraph().getView().getState(cell, true);
                updateEdgeLabels();
                graphComponent.redraw(state);
                graphComponent.labelChanged(cell, cell.getValue(), event);
            });
            getRootPane().setDefaultButton(buttonOk);
            panel.add(buttonOk, c);

            c.gridx = 1;
            JButton buttonCancel = new JButton("Abbrechen");
            buttonCancel.addActionListener(l -> {
                setVisible(false);
            });
            panel.add(buttonCancel, c);
        }

        private void createDialogDependsOn(Grounding grounding) {
            GridBagConstraints c = new GridBagConstraints();

            c.fill = GridBagConstraints.HORIZONTAL;
            c.insets = new Insets(10, 10, 0, 10);
            c.gridx = 0;
            c.gridy = 0;
            panel.add(new JLabel("Relation:"), c);

            c.gridx = 1;
            JLabel labelName = new JLabel(grounding.toString());
            panel.add(labelName, c);

            c.insets = new Insets(30, 10, 10, 10);
            c.gridx = 0;
            c.gridy = 1;
            c.gridwidth = 2;
            JButton buttonOk = new JButton("OK");
            buttonOk.addActionListener(l -> {
                setVisible(false);
            });
            getRootPane().setDefaultButton(buttonOk);
            panel.add(buttonOk, c);
        }

        private void createDialogHasRiskDegree(Grounding grounding) {
            GridBagConstraints c = new GridBagConstraints();

            c.fill = GridBagConstraints.HORIZONTAL;
            c.insets = new Insets(10, 10, 0, 10);
            c.gridx = 0;
            c.gridy = 0;
            panel.add(new JLabel("Gewicht"), c);

            c.gridx = 1;
            JTextField textFieldWeight = new JTextField(15);
            textFieldWeight.setText(grounding.getValues().get(2));
            panel.add(textFieldWeight, c);

            c.insets = new Insets(30, 10, 10, 10);
            c.gridx = 0;
            c.gridy = 1;
            JButton buttonOk = new JButton("OK");

            buttonOk.addActionListener(l -> {
                setVisible(false);
                grounding.getValues().set(2, textFieldWeight.getText());

                mxCellState state = graphComponent.getGraph().getView().getState(cell);
                graphComponent.redraw(state);
                graphComponent.labelChanged(cell, cell.getValue(), event);
            });
            getRootPane().setDefaultButton(buttonOk);
            panel.add(buttonOk, c);

            c.gridx = 1;
            JButton buttonCancel = new JButton("Abbrechen");
            buttonCancel.addActionListener(l -> {
                setVisible(false);
            });
            panel.add(buttonCancel, c);
        }

    }

}
