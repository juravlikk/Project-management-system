/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package connection;

import java.util.Arrays;
import java.util.Vector;

/**
 *
 * @author Александр
 */
public class MilestoneTask {
    private String TaskName;
    private String Status;

    public MilestoneTask(String TaskName, String Status) {
        this.TaskName = TaskName;
        this.Status = Status;
    }

    public void setStatus(String Status) {
        this.Status = Status;
    }

    public String getStatus() {
        return Status;
    }


    public String getTaskName() {
        return TaskName;
    }

    public void setTaskName(String TaskName) {
        this.TaskName = TaskName;
    }

    @Override
    public String toString() {
        return "TaskName: " + TaskName + ", Status: " + Status;
    }
    
    public Vector getVector() {
        return new Vector(Arrays.asList(TaskName, Status));
    }
}
