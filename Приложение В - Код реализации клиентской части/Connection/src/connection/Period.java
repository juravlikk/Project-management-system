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
public class Period {
    private Date StartPeriod;
    private Date EndPeriod;
    private Float CompletenessPart;
    private Float WorkTime;
    private String Comments;

    public Period(Date StartPeriod, Date EndPeriod, Float CompletenessPart, Float WorkTime, String Comments) {
        this.StartPeriod = StartPeriod;
        this.EndPeriod = EndPeriod;
        this.CompletenessPart = CompletenessPart;
        this.WorkTime = WorkTime;
        this.Comments = Comments;
    }

    public String getComments() {
        return Comments;
    }

    public Float getCompletenessPart() {
        return CompletenessPart;
    }

    public Date getEndPeriod() {
        return EndPeriod;
    }

    public Date getStartPeriod() {
        return StartPeriod;
    }

    public Float getWorkTime() {
        return WorkTime;
    }

    public void setComments(String Comments) {
        this.Comments = Comments;
    }

    public void setCompletenessPart(Float CompletenessPart) {
        this.CompletenessPart = CompletenessPart;
    }

    public void setEndPeriod(Date EndPeriod) {
        this.EndPeriod = EndPeriod;
    }

    public void setStartPeriod(Date StartPeriod) {
        this.StartPeriod = StartPeriod;
    }

    public void setWorkTime(Float WorkTime) {
        this.WorkTime = WorkTime;
    }

    @Override
    public String toString() {
        return "StartPeriod: " + StartPeriod + ", EndPeriod: " + EndPeriod + ", CompletenessPart: " + CompletenessPart + ", WorkTime: " + WorkTime + ", Comments: " + Comments;
    }
    
    public Vector getVector() {
        return new Vector(Arrays.asList(StartPeriod, EndPeriod, CompletenessPart, WorkTime, Comments));
    }
}
