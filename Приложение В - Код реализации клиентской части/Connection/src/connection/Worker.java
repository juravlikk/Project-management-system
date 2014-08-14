/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package connection;

import java.util.Arrays;
import java.util.Vector;
import javax.swing.JButton;

/**
 *
 * @author Александр
 */
public class Worker {
    private String Login;
    private String Mail;
    private String FirstName;
    private String LastName;
    private String MiddleName;
    private Float LoadFactor;

    public Worker(String Login, String Mail, String FirstName, String LastName, String MiddleName, Float LoadFactor) {
        this.Login = Login;
        this.Mail = Mail;
        this.FirstName = FirstName;
        this.LastName = LastName;
        this.MiddleName = MiddleName;
        this.LoadFactor = LoadFactor;
    }

    public String getFirstName() {
        return FirstName;
    }

    public String getLastName() {
        return LastName;
    }

    public Float getLoadFactor() {
        return LoadFactor;
    }

    public String getLogin() {
        return Login;
    }

    public String getMail() {
        return Mail;
    }

    public String getMiddleName() {
        return MiddleName;
    }

    public void setFirstName(String FirstName) {
        this.FirstName = FirstName;
    }

    public void setLastName(String LastName) {
        this.LastName = LastName;
    }

    public void setLoadFactor(Float LoadFactor) {
        this.LoadFactor = LoadFactor;
    }

    public void setLogin(String Login) {
        this.Login = Login;
    }

    public void setMail(String Mail) {
        this.Mail = Mail;
    }

    public void setMiddleName(String MiddleName) {
        this.MiddleName = MiddleName;
    }

    @Override
    public String toString() {
        return "Login: " + Login + ", FirstName: " + FirstName + ", LastName: " + LastName + ", MiddleName: " + MiddleName + ", Mail: " + Mail + ", LoadFactor: " + LoadFactor;
    }
    
    public Vector getVector() {
        return new Vector(Arrays.asList(Login, FirstName, LastName, MiddleName, LoadFactor, Mail));
    }
}
