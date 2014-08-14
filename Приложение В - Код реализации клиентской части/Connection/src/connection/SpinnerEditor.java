/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package connection;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Map;
import javax.swing.AbstractCellEditor;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerListModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.TableCellEditor;

/**
 *
 * @author Александр
 */
public class SpinnerEditor extends AbstractCellEditor implements TableCellEditor {
    private final JSpinner spinner = new JSpinner();
    private final Map mins = new HashMap();

    public SpinnerEditor(Map mins) {
        this.mins.putAll(mins);
        spinner.setModel(new SpinnerNumberModel());
    }
    
    public void setSpinner(Float value) {
        spinner.setModel(new SpinnerNumberModel(new Float(value), new Float(value), new Float(100), new Float(0.01)));
    }
    
    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        setSpinner((Float)mins.get(row));
        spinner.setValue(value);
        return spinner;
    }

    @Override
    public boolean isCellEditable(EventObject evt) {
        if (evt instanceof MouseEvent) {
            return ((MouseEvent) evt).getClickCount() >= 2;
        }
        return true;
    }

    @Override
    public Object getCellEditorValue() {
        return spinner.getValue();
    }
}
