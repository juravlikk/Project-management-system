/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package connection;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.swing.JOptionPane;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
/**
 *
 * @author Александр
 */

public class Functions {
    
    private Connection con;
    private Vector update = new Vector();
    private String UserL;
    
    public Functions() {
        con = new Connection();
    }

/*
 * Set paramentrs of the last called get function to vector
 */    
    public void setVector(Vector v) {
        update.clear();
        update.addAll(v);
    }
 
/*
 * Get paramentrs of the last called get function from vector
 */    
    public Vector getVector() {
        return update;
    }
    
/*
 * Clearing vector of paramentrs of the last called get function
 */    
    public void freeVector() {
        update.clear();
    }

    public Boolean signin(String login, String password) throws IOException, JSONException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        if (login == null | password == null) {
            return null;
        }

        Map fields = new HashMap();
        fields.put("login", login);
        fields.put("password", password);

        List result = con.request("login", fields);
        
        System.out.println(result.get(0));
        if (result.get(0).equals(2)) {
            UserL = login;
            return true;
        } 
        return false;
    }
    
    public Boolean signup(String login, String password, String password2, String mail, String firstname, String lastname, String middlename, boolean check) throws IOException, JSONException {
        if (!password.equals(password2) | password2 == null) {
           JOptionPane.showMessageDialog(null, "Wrong confirmation password", "Sign up", JOptionPane.ERROR_MESSAGE);
           return null;
        }
        if (login == null) {
           JOptionPane.showMessageDialog(null, "Empty login", "Sign up", JOptionPane.ERROR_MESSAGE);
           return null;
        }
        if (password == null) {
           JOptionPane.showMessageDialog(null, "Empty password", "Sign up", JOptionPane.ERROR_MESSAGE);
           return null;
        }
        if (mail == null) {
           JOptionPane.showMessageDialog(null, "Empty mail", "Sign up", JOptionPane.ERROR_MESSAGE);
           return null;
        }
        if (firstname == null) {
           JOptionPane.showMessageDialog(null, "Empty First name", "Sign up", JOptionPane.ERROR_MESSAGE);
           return null;
        }
        if (lastname == null) {
           JOptionPane.showMessageDialog(null, "Empty Last name", "Sign up", JOptionPane.ERROR_MESSAGE);
           return null;
        }
        if (middlename == null) {
           JOptionPane.showMessageDialog(null, "Empty Middle name", "Sign up", JOptionPane.ERROR_MESSAGE);
           return null;
        }
        if (!check) {
           JOptionPane.showMessageDialog(null, "You disagreed with rules", "Sign up", JOptionPane.ERROR_MESSAGE);
           return null;
        }
        if (!Signin.checkLogin(login)) {
           JOptionPane.showMessageDialog(null, "Wrong login", "Sign up", JOptionPane.ERROR_MESSAGE);
           return null;
        }
        if (!Signin.checkMail(mail)) {
           JOptionPane.showMessageDialog(null, "Wrong mail", "Sign up", JOptionPane.ERROR_MESSAGE);
           return null;
        }
        
        Map fields = new HashMap();
        fields.put("login", login);
        fields.put("password", password);
        fields.put("mail", mail);
        fields.put("firstname", firstname);
        fields.put("lastname", lastname);
        fields.put("middlename", middlename);
        
        List result = con.request("registration", fields);
        
        if (result == null) {
            return null;
        }
        
        System.out.println(result.get(0));
        JOptionPane.showMessageDialog(null, result.get(0), "Sign up", JOptionPane.INFORMATION_MESSAGE);
        return true;
    }
    
    public Boolean changePassword(String login, String Old, String New, String New2) throws IOException, JSONException {
        if (!New.equals(New2) | New2 == null) {
           JOptionPane.showMessageDialog(null, "Wrong confirmation password", "Change password", JOptionPane.ERROR_MESSAGE);
           return null;
        }
        if (login == null | !Signin.checkLogin(login)) {
           JOptionPane.showMessageDialog(null, "Wrong login", "Change password", JOptionPane.ERROR_MESSAGE);
           return null;
        }
        if (New == null) {
           JOptionPane.showMessageDialog(null, "Wrong new password", "Change password", JOptionPane.ERROR_MESSAGE);
           return null;
        }
        if (Old == null) {
           JOptionPane.showMessageDialog(null, "Wrong old password", "Change password", JOptionPane.ERROR_MESSAGE);
           return null;
        }
        
        Map fields = new HashMap();
        fields.put("login", login);
        fields.put("old", Old);
        fields.put("new", New);
        
        List result = con.request("changepassword", fields);
        
        if (result == null) {
            return null;
        }
        
        System.out.println(result.get(0));
        JOptionPane.showMessageDialog(null, result.get(0), "Change password", JOptionPane.INFORMATION_MESSAGE);
        return true;
    }

    public Task[] getTasks(String login, String project, boolean free) throws IOException, JSONException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        if (login != null & free) {
            JOptionPane.showMessageDialog(null, "Wrong options", "Get tasks", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        Map fields = new HashMap();
        fields.put("login", login);
        fields.put("project", project);
        fields.put("check", UserL);
        if (free) {
            fields.put("free", 1);
        } else {
            fields.put("free", 0);
        }
        
        List result = con.request("gettasks", fields);

        if (result == null) {
            return null;
        }
        
        Task tasks[] = new Task[result.size()];
        for (int i=0; i<result.size(); i++) {
            JSONObject obj = (JSONObject) result.get(i);
            Period [] periods = null;
            if ((obj.get("Periods")) != null) {
                JSONArray array = (JSONArray) obj.get("Periods");
                periods = new Period[array.length()];
                for (int j=0; j<array.length(); j++) {
                    periods[j] = (Period) fromJSON("connection.Period", array.get(j));
                }
            }
            obj.remove("Periods");
            tasks[i] = (Task) fromJSON("connection.Task", result.get(i));
            tasks[i].setPeriods(periods);
            System.out.println(tasks[i].toString());
        }
        return tasks;
    }    

    public Milestone[] getMilestones(String login) throws IOException, JSONException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Map fields = new HashMap();
        fields.put("login", login);
        fields.put("check", UserL);
        
        List result = con.request("getmilestones", fields);

        if (result == null) {
            return null;
        }
        
        Milestone milestones[] = new Milestone[result.size()];
        for (int i=0; i<result.size(); i++) {
            JSONObject obj = (JSONObject) result.get(i);
            MilestoneTask [] tasks = null;
            if ((obj.get("Tasks")) != null) {
                JSONArray array = (JSONArray) obj.get("Tasks");
                tasks = new MilestoneTask[array.length()];
                for (int j=0; j<array.length(); j++) {
                    tasks[j] = (MilestoneTask) fromJSON("connection.MilestoneTask", array.get(j));
                }
            }
            obj.remove("Tasks");
            milestones[i] = (Milestone) fromJSON("connection.Milestone", result.get(i));
            milestones[i].setTasks(tasks);
            System.out.println(milestones[i].toString());
        }
        return milestones;
    }    
    
    public Project[] getProjects(String login) throws IOException, JSONException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        if (login == null) {
            JOptionPane.showMessageDialog(null, "Wrong options", "Get projects", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        Map fields = new HashMap();
        fields.put("login", login);
        fields.put("check", UserL);
        
        List result = con.request("getprojects", fields);

        if (result == null) {
            return null;
        }
        
        Project projects[] = new Project[result.size()];
        for (int i=0; i<result.size(); i++) {
            JSONObject obj = (JSONObject) result.get(i);
            projects[i] = (Project) fromJSON("connection.Project", result.get(i));
            System.out.println(projects[i].toString());
        }
        return projects;
    }  
    
    public Worker[] getWorkers() throws IOException, JSONException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Map fields = new HashMap();
        fields.put("check", UserL);
        
        List result = con.request("getworkers", fields);
        
        if (result == null) {
            return null;
        }
        
        Worker workers[] = new Worker[result.size()];
        for (int i=0; i<result.size(); i++) {
            JSONObject obj = (JSONObject) result.get(i);
            workers[i] = (Worker) fromJSON("connection.Worker", result.get(i));
            System.out.println(workers[i].toString());
        }
        return workers;
    }
    
    public Boolean updateCompleteness(float Completeness, Task task, String Next, String Comments) throws IOException, JSONException {
        if (task.getTaskName() == null) {
           JOptionPane.showMessageDialog(null, "Wrong task name", "Update completeness", JOptionPane.ERROR_MESSAGE);
           return null;
        }
        if (!"Static".equals(task.getType())) {
           JOptionPane.showMessageDialog(null, "Wrong options", "Update completeness", JOptionPane.ERROR_MESSAGE);
           return null;
        }
        if ((Completeness > 100) | (Completeness < 0) | (task.getCompleteness() > Completeness)) {
           JOptionPane.showMessageDialog(null, "Wrong completeness", "Update completeness", JOptionPane.ERROR_MESSAGE);
           return null;
        }

        Map fields = new HashMap();
        fields.put("task", task.getTaskName());
        fields.put("completeness", Completeness);
        fields.put("next", Next);
        fields.put("comments", Comments);
        fields.put("check", UserL);
        
        List result = con.request("updatecompleteness", fields);
        
        if (result == null) {
            return null;
        }

        task.setCompleteness(Completeness);
        
        System.out.println(result.get(0));
        JOptionPane.showMessageDialog(null, result.get(0), "Update Completeness", JOptionPane.INFORMATION_MESSAGE);
        return true;
    }
    
    public Boolean createTask(String Login, String Parent, String ProjectName, String TaskName, int Priority, int Status, Date Start, float ScheduledDuration, String Type, String Comments) throws IOException, JSONException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        if (ProjectName == null) {
           JOptionPane.showMessageDialog(null, "Empty project name", "Create task", JOptionPane.ERROR_MESSAGE);
           return null;
        }
        if (TaskName == null) {
           JOptionPane.showMessageDialog(null, "Empty task name", "Create task", JOptionPane.ERROR_MESSAGE);
           return null;
        }
        if (ScheduledDuration == 0 | Start.after(new Date())) {
           JOptionPane.showMessageDialog(null, "Wrong options", "Create task", JOptionPane.ERROR_MESSAGE);
           return null;
        }
        if (Type == null) {
           JOptionPane.showMessageDialog(null, "Empty type", "Create task", JOptionPane.ERROR_MESSAGE);
           return null;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");        
        Map fields = new HashMap();
        fields.put("project", ProjectName);
        fields.put("name", TaskName);
        fields.put("priority", Priority);
        fields.put("status", Status);
        fields.put("start", dateFormat.format(Start));
        fields.put("scheduled", ScheduledDuration);
        fields.put("type", Type);
        fields.put("comments", Comments);
        fields.put("login", Login);
        fields.put("parent", Parent);
        
        List result = con.request("createtask", fields);

        if (result == null) {
            return null;
        }
        System.out.println(result.get(0));
        JOptionPane.showMessageDialog(null, result.get(0), "Create Task", JOptionPane.INFORMATION_MESSAGE);   
        return true;
    }  
    
    public Boolean createProject(String ProjectName, Date DeadLine, int Priority, String Comments) throws IOException, JSONException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        if (ProjectName == null) {
           JOptionPane.showMessageDialog(null, "Empty project name", "Create project", JOptionPane.ERROR_MESSAGE);
           return null;
        }
        if (DeadLine == null) {
           JOptionPane.showMessageDialog(null, "Empty deadline", "Create project", JOptionPane.ERROR_MESSAGE);
           return null;
        }
        if (DeadLine.after(new Date())) {
            JOptionPane.showMessageDialog(null, "Wrong options", "Create project", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");        
        Map fields = new HashMap();
        fields.put("project", ProjectName);
        fields.put("priority", Priority);
        fields.put("deadline", dateFormat.format(DeadLine));
        fields.put("comments", Comments);
        fields.put("check", UserL);
        
        List result = con.request("createproject", fields);

        if (result == null) {
            return null;
        }
        System.out.println(result.get(0));
        JOptionPane.showMessageDialog(null, result.get(0), "Create Project", JOptionPane.INFORMATION_MESSAGE);  
        return true;
    } 
     
    public Boolean createMilestone(String Login, String Name, Date DeadLine, String[] Tasks) throws IOException, JSONException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        if (Login == null) {
            JOptionPane.showMessageDialog(null, "Empty login", "Create milestone", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        if (DeadLine == null) {
            JOptionPane.showMessageDialog(null, "Empty deadlin", "Create milestone", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        if (Name == null) {
            JOptionPane.showMessageDialog(null, "Empty milestone name", "Create milestone", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        if (Tasks == null) {
            JOptionPane.showMessageDialog(null, "Empty tasks", "Create milestone", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        if (DeadLine.before(new Date())) {
            JOptionPane.showMessageDialog(null, "Wrong options", "Create milestone", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
        Map fields = new HashMap();
        fields.put("login", Login);
        fields.put("name", Name);
        fields.put("deadline", dateFormat.format(DeadLine));
        fields.put("tasks", Tasks);
        fields.put("check", UserL);
        
        List result = con.request("createmilestone", fields);

        if (result == null) {
            return null;
        }
        System.out.println(result.get(0));
        JOptionPane.showMessageDialog(null, result.get(0), "Create Milestone", JOptionPane.INFORMATION_MESSAGE); 
        return true;
    } 
    
    public Boolean setWorker(String Login, String Name) throws IOException, JSONException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        if (Login == null) {
            JOptionPane.showMessageDialog(null, "Empty login", "Set worker", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        if (Name == null) {
            JOptionPane.showMessageDialog(null, "Empty task name", "Set worker", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        Map fields = new HashMap();
        fields.put("login", Login);
        fields.put("name", Name);
        fields.put("check", UserL);
        
        List result = con.request("setworker", fields);

        if (result == null) {
            return null;
        }
        System.out.println(result.get(0));
        JOptionPane.showMessageDialog(null, result.get(0), "Set Worker", JOptionPane.INFORMATION_MESSAGE); 
        return true;
    } 

    public Boolean changeParent(String Parent, String Name) throws IOException, JSONException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        if (Name == null) {
            JOptionPane.showMessageDialog(null, "Empty task name", "Change parent", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        Map fields = new HashMap();
        fields.put("parent", Parent);
        fields.put("name", Name);
        fields.put("check", UserL);
        
        List result = con.request("changeparent", fields);

        if (result == null) {
            return null;
        }
        System.out.println(result.get(0));
        JOptionPane.showMessageDialog(null, result.get(0), "Change Parent", JOptionPane.INFORMATION_MESSAGE);   
        return true;
    } 
    
    public int getLastID() throws IOException, JSONException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Map fields = new HashMap();
        
        List result = con.request("getlastid", fields);

        if (result == null) {
            return -1;
        }
        System.out.println(result.get(0));
        return (int) result.get(0);
    }
    
    public Event[] getUpdates(int ID) throws IOException, JSONException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Map fields = new HashMap();
        fields.put("id", ID);
        
        List result = con.request("getupdates", fields);

        if (result == null) {
            return null;
        }

        Event events[] = new Event[result.size()];
        for (int i=0; i<result.size(); i++) {
            JSONObject obj = (JSONObject) result.get(i);
            events[i] = (Event) fromJSON("connection.Worker", result.get(i));
            System.out.println(events[i].toString());
        }
        return events;
    }
    
    public Boolean setLoadFactor(String Login, float LoadFactor) throws IOException, JSONException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        if (Login == null) {
            JOptionPane.showMessageDialog(null, "Empty login", "Set load factor", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        if (LoadFactor > 1 | LoadFactor < 0) {
            JOptionPane.showMessageDialog(null, "Wrong options", "Set load factor", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        Map fields = new HashMap();
        fields.put("login", Login);
        fields.put("loadfactor", LoadFactor);
        fields.put("check", UserL);
        
        List result = con.request("setloadfactor", fields);

        if (result == null) {
            return null;
        }
        System.out.println(result.get(0));
        return true;
    }
    
    public Boolean changePriority(String task, int Priority) throws IOException, JSONException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        if (task == null) {
            JOptionPane.showMessageDialog(null, "Empty task", "Change priority", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        if (Priority < 0) {
            JOptionPane.showMessageDialog(null, "Wrong options", "Change priority", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        Map fields = new HashMap();
        fields.put("task", task);
        fields.put("priority", Priority);
        fields.put("check", UserL);
        
        List result = con.request("changepriority", fields);

        if (result == null) {
            return null;
        }
        System.out.println(result.get(0));
        return true;
    }
    
    public Boolean setManager(String Login) throws IOException, JSONException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        if (Login == null) {
            JOptionPane.showMessageDialog(null, "Empty login", "Set manager", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        Map fields = new HashMap();
        fields.put("login", Login);
        fields.put("check", UserL);

        List result = con.request("setmanager", fields);

        if (result == null) {
            return null;
        }
        System.out.println(result.get(0));
        return true;
    }    

/*
 * Method to creating Class by name and JSONObject. 
 * name - name of the creating Class,
 * json - JSON Object of the respond
 */    
    private Object fromJSON(String name, Object json) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, JSONException {
        Class cls = Class.forName(name);
        Field field[] = cls.getDeclaredFields();
        JSONObject obj = (JSONObject) json;
        Class array[] = new Class[obj.length()];
        Object param[] = new Object[obj.length()];
        for (int i=0; i<obj.length(); i++) {
            array[i] = field[i].getType();
            if (array[i] == Date.class) {
                param[i] = new Date(obj.get(field[i].getName()).toString().substring(0, 17));
            } else {
                param[i] = array[i].getConstructor(String.class).newInstance(obj.get(field[i].getName()).toString());
            }
        }
        return cls.getDeclaredConstructor(array).newInstance(param);
    }
}