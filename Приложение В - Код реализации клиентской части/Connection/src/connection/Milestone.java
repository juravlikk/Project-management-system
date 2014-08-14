/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package connection;

import java.util.Arrays;
import java.util.Date;
import java.util.Vector;

/**
 *
 * @author Александр
 */
public class Milestone {
    private String Name;
    private String UserName;
    private Date DeadLine;
    private String Status;
    private MilestoneTask [] tasks;

    public Milestone(String Name, String UserName, Date DeadLine, String Status) {
        this.Name = Name;
        this.UserName = UserName;
        this.DeadLine = DeadLine;
        this.Status = Status;
    }

    public Date getDeadLine() {
        return DeadLine;
    }

    public String getName() {
        return Name;
    }

    public String getStatus() {
        return Status;
    }

    public String getUserName() {
        return UserName;
    }

    public void setDeadLine(Date DeadLine) {
        this.DeadLine = DeadLine;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public void setStatus(String Status) {
        this.Status = Status;
    }

    public void setUserName(String UserName) {
        this.UserName = UserName;
    }


    public void setTasks(MilestoneTask[] tasks) {
        this.tasks = tasks;
    }

    public MilestoneTask[] getTasks() {
        return tasks;
    }
    
    public String tasksToString() {
        String result = new String();
        for (int i=0; i<tasks.length; i++) {
            result += tasks[i].toString();
        }
        return result;
    }
    
    @Override
    public String toString() {
        return "Name: " + Name + ", UserName: " + UserName + ", DeadLine: " + DeadLine + ", Status: " + Status + ", Tasks: {" + tasksToString() + "}"; 
    }
    
    public Vector getVector() {
        return new Vector(Arrays.asList(Name, UserName,DeadLine, Status));
    }
}
