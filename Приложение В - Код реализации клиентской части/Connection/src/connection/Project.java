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
public class Project {
    private String ProjectName;
    private Date Start;
    private Date DeadLine;
    private Float Completeness;
    private Short Priority;
    private String Comments;
    private String IsYour;

    public Project(String ProjectName, Date Start, Date DeadLine, Float Completeness, Short Priority, String Comments, String IsYour) {
        this.ProjectName = ProjectName;
        this.Start = Start;
        this.DeadLine = DeadLine;
        this.Completeness = Completeness;
        this.Priority = Priority;
        this.Comments = Comments;
        this.IsYour = IsYour;
    }

    public void setStart(Date Start) {
        this.Start = Start;
    }

    public Date getStart() {
        return Start;
    }
    
    public String getComments() {
        return Comments;
    }

    public Float getCompleteness() {
        return Completeness;
    }

    public Date getDeadLine() {
        return DeadLine;
    }

    public String getIsYour() {
        return IsYour;
    }

    public Short getPriority() {
        return Priority;
    }

    public String getProjectName() {
        return ProjectName;
    }

    public void setComments(String Comments) {
        this.Comments = Comments;
    }

    public void setCompleteness(Float Completeness) {
        this.Completeness = Completeness;
    }

    public void setDeadLine(Date DeadLine) {
        this.DeadLine = DeadLine;
    }

    public void setIsYour(String IsYour) {
        this.IsYour = IsYour;
    }

    public void setPriority(Short Priority) {
        this.Priority = Priority;
    }

    public void setProjectName(String ProjectName) {
        this.ProjectName = ProjectName;
    }

    @Override
    public String toString() {
        return "ProjectName: " + ProjectName + ", Start: " + Start + ", DeadLine: " + DeadLine + ", Completeness: " + Completeness + ", Priority: " + Priority + ", Comments: " + Comments + ", IsYour: " + IsYour;
    }
    
    public Vector getVector() {
        return new Vector(Arrays.asList(ProjectName, Start, DeadLine, Completeness, Priority, Comments, IsYour));
    }
}
