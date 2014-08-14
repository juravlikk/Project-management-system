/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package connection;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Александр
 */
public class Model extends DefaultTableModel {

    public Model(Object[][] data, Object[] columnNames) {
        super(data, columnNames);
    }

    public Model(Vector data, Vector columnNames) {
        super(data, columnNames);
    }

    public Model(Object[] columnNames, int rowCount) {
        super(columnNames, rowCount);
    }

    public Model(int rowCount, int columnCount) {
        super(rowCount, columnCount);
    }
    
    @Override
    public void fireTableRowsUpdated(int firstRow, int lastRow) {
        super.fireTableRowsUpdated(firstRow, lastRow);
    }
    
    @Override
    public void fireTableRowsInserted(int firstRow, int lastRow) {
        super.fireTableRowsInserted(firstRow, lastRow);
    }
    
    @Override
    public void fireTableDataChanged() {
        super.fireTableDataChanged();
    }
    
    @Override
    public void fireTableChanged(TableModelEvent e) {
        super.fireTableChanged(e);
    }
    
    @Override
    public void fireTableCellUpdated(int row, int column) {
        super.fireTableCellUpdated(row, column);
    }
    
    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
    
    @Override
    public void addRow(Object[] rowData) {
        super.addRow(rowData);
    }
    
    @Override
    public void removeRow(int row) {
        super.removeRow(row);
    }
    
    @Override
    public Object getValueAt(final int row, int column) {
        return super.getValueAt(row, column);
    }
    
    @Override
    public void addTableModelListener(TableModelListener l) {
        super.addTableModelListener(l);
    }
    
    @Override
    public void setValueAt(Object aValue, int row, int column) {
        super.setValueAt(aValue, row, column);
    }
    
    public Model(Vector columnNames, int rowCount) {
        super(columnNames, rowCount);
    }
    
    public Model() {
        super();
    }
    
    private ArrayList nonEditableCells = new ArrayList();
    
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return getValueAt(0, columnIndex).getClass();
    }

    public void setCellEditable(int row, int column, boolean editable) {
        Cell cell = new Cell(row, column);
        if (editable) {
            while (nonEditableCells.remove(cell)) {}
        } else {
            nonEditableCells.add(cell);
        }
    }
  
    public void setRowEditable(int row, boolean editable) {
        for (int i = 0; i < this.getColumnCount(); i++) {
            if (editable) {
                while (nonEditableCells.remove(new Cell(row, i))) {}
            } else {
                nonEditableCells.add(new Cell(row, i));
            }
        }
    }
    
    public void setColumnEditable(int column, boolean editable) {
        for (int i = 0; i < this.getRowCount(); i++) {
            if (editable) {
                while (nonEditableCells.remove(new Cell(i, column))) {}
            } else {
                nonEditableCells.add(new Cell(i, column));
            }
        }    
    }
    
    @Override
    public boolean isCellEditable(int row, int column) {
        if (getColumnName(column).equals("") | (getColumnName(column).equals("Completeness") & getColumnCount() >= 12)) {
            Cell cell = new Cell(row, column);
            for (int i = 0; i < nonEditableCells.size(); i++) {
                if (cell.equals(nonEditableCells.get(i))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    
    class Cell {
        private Integer Row = null;
        private Integer Col = null;
        
        public Cell(Integer row, Integer col) {
            if (row != null) {
                this.Row = row;
            } else {
                this.Row = 0;
            }
            if (col != null) {
                this.Col = col;
            } else {
                this.Col = 0;
            }
        }
        
        public Integer getRow() {
            return this.Row;
        }
        
        public Integer getCol() {
            return this.Col;
        }   
        
        public boolean equals(Cell cell) {
            if (cell.getRow() == this.getRow() && cell.getCol() == this.getCol()) {
                return true;
            } else {
                return false;
            }
        }
        
        public boolean equals(Object oCell) {
            Cell cell = (Cell)oCell;
            return equals(cell);
        }    
    }
}