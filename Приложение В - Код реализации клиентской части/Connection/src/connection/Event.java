/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package connection;

/**
 *
 * @author Александр
 */
public class Event {
    private String TableName;
    private String ColumnName;
    private String Operation;
    private String OldValue;
    private String NewValue;

    public Event(String TableName, String ColumnName, String Operation, String OldValue, String NewValue) {
        this.TableName = TableName;
        this.ColumnName = ColumnName;
        this.Operation = Operation;
        this.OldValue = OldValue;
        this.NewValue = NewValue;
    }

    public String getColumnName() {
        return ColumnName;
    }

    public String getNewValue() {
        return NewValue;
    }

    public String getOldValue() {
        return OldValue;
    }

    public String getTableName() {
        return TableName;
    }

    public String getOperation() {
        return Operation;
    }

    public void setColumnName(String ColumnName) {
        this.ColumnName = ColumnName;
    }

    public void setNewValue(String NewValue) {
        this.NewValue = NewValue;
    }

    public void setOldValue(String OldValue) {
        this.OldValue = OldValue;
    }

    public void setOperation(String Operation) {
        this.Operation = Operation;
    }

    public void setTableName(String TableName) {
        this.TableName = TableName;
    }
}
