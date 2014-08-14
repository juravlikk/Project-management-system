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
public class Task {
    private String Comments;
    private String ProjectName;
    private Date Start;
    private String Type;
    private String UserName;
    private Float Completeness;
    private String TaskName;
    private Integer Priority;
    private Float ScheduledDuration;
    private String Status;
    private String ParentTask;
    private Float Duration;
    private Period[] periods;
        
    public Task(String Comments, String ProjectName, Date Start, String Type, String UserName, Float Completeness, String TaskName, Integer Priority, Float ScheduledDuration, String Status, String ParentTask, Float Duration) {
        this.Comments = Comments;
        this.ProjectName = ProjectName;
        this.Start = Start;
        this.Type = Type;
        this.UserName = UserName;
        this.Completeness = Completeness;
        this.TaskName = TaskName;
        this.Priority = Priority;
        this.ScheduledDuration = ScheduledDuration;
        this.Status = Status;
        this.ParentTask = ParentTask;
        this.Duration = Duration;
    }
    
    public String getStatus() {
        return Status;
    }
    
    public void setStatus(String Status) {
        this.Status = Status;
    }
    
    public String getComments() {
        return Comments;
    }
    
    public void setComments(String Comments) {
        this.Comments = Comments;
    }  
    
    public String getProjectName() {
        return ProjectName;
    }
    
    public void setProjectName(String ProjectName) {
        this.ProjectName = ProjectName;
    }    

    public Float getCompleteness() {
        return Completeness;
    }

    public String getParentTask() {
        return ParentTask;
    }

    public Integer getPriority() {
        return Priority;
    }

    public Float getScheduledDuration() {
        return ScheduledDuration;
    }

    public Float getDuration() {
        return Duration;
    }

    public Date getStart() {
        return Start;
    }

    public String getTaskName() {
        return TaskName;
    }

    public String getType() {
        return Type;
    }

    public String getUserName() {
        return UserName;
    }

    public void setCompleteness(Float Completeness) {
        this.Completeness = Completeness;
    }

    public void setDuration(Float Duration) {
        this.Duration = Duration;
    }

    public void setParentTask(String ParentTask) {
        this.ParentTask = ParentTask;
    }

    public void setPriority(Integer Priority) {
        this.Priority = Priority;
    }

    public void setScheduledDuration(Float ScheduledDuration) {
        this.ScheduledDuration = ScheduledDuration;
    }

    public void setStart(Date Start) {
        this.Start = Start;
    }

    public void setTaskName(String TaskName) {
        this.TaskName = TaskName;
    }

    public void setType(String Type) {
        this.Type = Type;
    }

    public void setUserName(String UserName) {
        this.UserName = UserName;
    }

    public void setPeriods(Period[] periods) {
        this.periods = periods;
    }

    public Period[] getPeriods() {
        return periods;
    }

    public String periodsToString() {
        String result = new String();
        for (int i=0; i<periods.length; i++) {
            result += periods[i].toString();
        }
        return result;
    }
    
    @Override
    public String toString() {
        return "TaskName: " + TaskName + ", ProjectName: " + ProjectName + ", UserName: " + UserName + ", Completeness" + Completeness + ", Priority: " + Priority + ", Start: " + Start + ", ScheduledDuration: " + ScheduledDuration + ", Duration: " + Duration + ", Type: " + Type + ", ParentTask: " + ParentTask + ", Status: " + Status + ", Comments: " + Comments + "Peiods: {" + periodsToString() + "}";
    }
    
    public boolean isStatic() {
        return (Type.equals("Static"));
    }
    
    public Vector getVector() {
        return new Vector(Arrays.asList(false ,TaskName, ProjectName, UserName, Completeness, Priority, Start, ScheduledDuration, Duration, Type, ParentTask, Status, Comments));
    }
}