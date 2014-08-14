/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package connection;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Александр
 */
public class TableCellRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (table.getColumn("Status") == null) {
            return comp;
        }
        if ((table.getValueAt(row, table.getColumnModel().getColumnIndex("Status"))).equals("Overdue")) {
            comp.setBackground(Color.RED);
            comp.setForeground(Color.BLACK);
        }
        if ((table.getValueAt(row, table.getColumnModel().getColumnIndex("Status"))).equals("Incomplete")) {
            comp.setBackground(Color.YELLOW);
            comp.setForeground(Color.BLACK);
        }
        if ((table.getValueAt(row, table.getColumnModel().getColumnIndex("Status"))).equals("Completed")) {
            comp.setBackground(Color.GREEN);
            comp.setForeground(Color.BLACK);
        }
        return comp;
    }
}