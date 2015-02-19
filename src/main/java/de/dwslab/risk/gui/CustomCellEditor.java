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
import com.mxgraph.swing.view.mxICellEditor;
import com.mxgraph.view.mxCellState;

import de.dwslab.risk.gui.exception.RoCAException;
import de.dwslab.risk.gui.model.Entity;
import de.dwslab.risk.gui.model.Grounding;

public class CustomCellEditor implements mxICellEditor {

    private mxGraphComponent graphComponent;
    private Object editingCell;
    private EventObject trigger;

    public CustomCellEditor(mxGraphComponent graphComponent) {
        this.graphComponent = graphComponent;
    }

    @Override
    public Object getEditingCell() {
        return editingCell;
    }

    @Override
    public void startEditing(Object cell, EventObject event) {
        editingCell = cell;
        trigger = event;

        UserObjectEditDialog dialog = new UserObjectEditDialog((mxCell) cell, event, graphComponent);
        dialog.pack();
        dialog.setModalityType(APPLICATION_MODAL);
        dialog.setLocationRelativeTo(graphComponent);
        dialog.setVisible(true);

        editingCell = null;
    }

    @Override
    public void stopEditing(boolean cancel) {
        // what to do here?!
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
                createDialog((Entity) cell.getValue());
            } else if (cell.getValue() instanceof Grounding) {
                createDialog((Grounding) cell.getValue());
            } else {
                throw new RoCAException("Unknown graph UserObject: " + cell.getValue() + " "
                        + cell.getValue().getClass());
            }

            add(panel);
        }

        private void createDialog(Entity entity) {
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
                mxCellState state = graphComponent.getGraph().getView().getState(cell);
                String style = cell.getStyle();
                int fillIndex = style.indexOf("fillColor");
                switch (comboOffline.getSelectedIndex()) {
                case 0:
                    entity.setOffline(FALSE);
                    if (fillIndex < 0) {
                        cell.setStyle(style + ";fillColor=#22ff22");
                    } else {
                        StringBuilder str = new StringBuilder(style);
                        str.replace(fillIndex + 11, fillIndex + 17, "22ff22");
                        cell.setStyle(str.toString());
                    }
                    break;
                case 1:
                    entity.setOffline(null);
                    if (fillIndex < 0) {
                        cell.setStyle(style + ";fillColor=#adc5ff");
                    } else {
                        StringBuilder str = new StringBuilder(style);
                        str.replace(fillIndex + 11, fillIndex + 17, "adc5ff");
                        cell.setStyle(str.toString());
                    }
                    break;
                case 2:
                    entity.setOffline(TRUE);
                    if (fillIndex < 0) {
                        cell.setStyle(style + ";fillColor=#ff2222");
                    } else {
                        StringBuilder str = new StringBuilder(style);
                        str.replace(fillIndex + 11, fillIndex + 17, "ff2222");
                        cell.setStyle(str.toString());
                    }
                    break;
                }
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

        private void createDialog(Grounding grounding) {
            GridBagConstraints c = new GridBagConstraints();

            c.fill = GridBagConstraints.HORIZONTAL;
            c.insets = new Insets(10, 10, 0, 10);
            c.gridx = 0;
            c.gridy = 0;
            panel.add(new JLabel("Gewicht"), c);

            c.gridx = 1;
            JTextField textFieldName = new JTextField(15);
            textFieldName.setText(grounding.getValues().get(2));
            panel.add(textFieldName, c);

            c.insets = new Insets(30, 10, 10, 10);
            c.gridx = 0;
            c.gridy = 1;
            JButton buttonOk = new JButton("OK");

            buttonOk.addActionListener(l -> {
                setVisible(false);
                grounding.getValues().set(2, textFieldName.getText());

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
