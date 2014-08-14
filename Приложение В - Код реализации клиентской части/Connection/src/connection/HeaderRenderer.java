/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package connection;

import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

/**
 *
 * @author Александр
 */
class HeaderRenderer implements TableCellRenderer {

    private final JCheckBox check = new JCheckBox();

    public HeaderRenderer(JTableHeader header, boolean set) {
        check.setOpaque(false);
        check.setFont(header.getFont());
        check.setEnabled(set);
        header.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (!check.isEnabled()) {
                    return;
                }
                JTable table = ((JTableHeader) e.getSource()).getTable();
                TableColumnModel columnModel = table.getColumnModel();
                int viewColumn = columnModel.getColumnIndexAtX(e.getX());
                int modelColumn = table.convertColumnIndexToModel(viewColumn);
                if (modelColumn == 0) {
                    check.setSelected(!check.isSelected());
                    TableModel m = table.getModel();
                    Boolean f = check.isSelected();
                    for (int i = 0; i < m.getRowCount(); i++) {
                        m.setValueAt(f, i, 0);
                    }
                    ((JTableHeader) e.getSource()).repaint();
                }
            }
        });
    }

    public void setCheck(boolean check) {
        this.check.setEnabled(false);
    }
    
    @Override
    public Component getTableCellRendererComponent(JTable tbl, Object val, boolean isS, boolean hasF, int row, int col) {
        TableCellRenderer r = tbl.getTableHeader().getDefaultRenderer();
        JLabel l = (JLabel) r.getTableCellRendererComponent(tbl, val, isS, hasF, row, col);
        l.setIcon(new CheckBoxIcon(check));
        return l;
    }

    private static class CheckBoxIcon implements Icon {

        private final JCheckBox check;

        public CheckBoxIcon(JCheckBox check) {
            this.check = check;
        }

        @Override
        public int getIconWidth() {
            return check.getPreferredSize().width;
        }

        @Override
        public int getIconHeight() {
            return check.getPreferredSize().height;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            SwingUtilities.paintComponent(g, check, (Container) c, x, y, getIconWidth(), getIconHeight());
        }
    }
}