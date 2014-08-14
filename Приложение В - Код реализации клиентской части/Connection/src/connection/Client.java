/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package connection;

import chrriis.dj.nativeswing.swtimpl.NativeInterface;
import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;
import java.awt.AWTException;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.RowFilter;
import javax.swing.SpinnerNumberModel;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import org.jdesktop.swingx.painter.MattePainter;
import org.jdesktop.swingx.plaf.PainterUIResource;
import org.json.JSONException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 *
 * @author Александр
 */
public class Client extends javax.swing.JFrame {

    private Signin form;
    private Functions func;
    private JButton pCreate;
    private JButton pGet;
    private JButton tCreate;
    private JButton tGet;
    private JButton mCreate;
    private JButton mGet;
    private Vector taskCols, projectCols, milestoneCols, workerCols, periodCols, milestoneTaskCols;
    private Map projectMap, milestoneMap, taskMap, workerMap, mins,changes;
    private Vector names;
    private TableRowSorter<Model> sorter;
    private final Timer timer;
    private int ID;
    private TrayIcon trayIcon;
    private SystemTray tray;
    private JWebBrowser webBrowser;
       
    public Client() {
        
        initComponents();
        
        func = new Functions();
        form = new Signin(func, this);
        
        System.loadLibrary("JNI");
        try {
            ID = func.getLastID();
        } catch (IOException | JSONException | ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        setLocationRelativeTo(null);
        
        setGetTasksButtons();
        setGetMilestonesButtons();
        setCreateTaskButtons();
        setCreateProjectButtons();
        setCreateMilestoneButtons();
        setChangeParentButtons();
        setChangeLFButtons();
        setChangePriorityBts();
        intiButtons();
        
        setTrayIcon();
        
        timer = new Timer(30000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    if (func.getLastID() > ID) {
                        Event[] events = func.getUpdates(ID);
                        String s = new String();
                        for (Event e: events) {
                            switch(e.getTableName()) {
                                case "Tasks": {
                                    if (e.getColumnName().equals("Login")) {
                                        if (e.getNewValue().equals(form.getLogin())) {
                                            s+= "New task was assigned to you\n\n";
                                        }
                                    }
                                    if (e.getOperation().equals("INSERT")) {
                                        s+= "New task was added\n\n\n";
                                    }
                                    if (e.getColumnName().equals("Completeness")) {
                                        s+= "Completeness of one task was changed\n\n";
                                    }
                                    if (e.getColumnName().equals("ParentTask")) {
                                        s+= "Parent task of one task was changed\n\n";
                                    }
                                    if (e.getColumnName().equals("Priority")) {
                                        s+= "Priority of one task was changed\n\n";
                                    }
                                    break;
                                } 
                                case "Users": {
                                    if (e.getColumnName().equals("Completeness")) {
                                        s+= "Now you are manager. Logout an signin again to update your rules\n\n";
                                    }
                                    if (e.getColumnName().equals("LoadFactor")) {
                                        s+= "Load factor of one user was changed\n\n";
                                    }
                                    if (e.getOperation().equals("INSERT")) {
                                        s+= "New worker signed up\n\n";
                                    }
                                    break;
                                } 
                                case "Milestones": {
                                    if (e.getOperation().equals("INSERT")) {
                                        s+= "New milestone was added\n\n";
                                    }
                                    break;
                                } 
                                case "Project": {
                                    if (e.getOperation().equals("INSERT")) {
                                        s+= "New project was added\n\n";
                                    }
                                    break;
                                }
                            }
                        }
                        JOptionPane.showMessageDialog(null, s);
                    }
                } catch (IOException | JSONException | ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        timer.setRepeats(true);
    
        Runnable time = new Runnable() {

            @Override
            public void run() {
                timer.start();
            }
        };
        time.run();
        
        pack();
    }

    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */

        try{
            UIManager.put("TaskPaneContainer.backgroundPainter",new PainterUIResource(new MattePainter(
                    new GradientPaint(0f, 0f, new Color(240,240,240), 0f, 1f, new Color(204,204,255)), true)));
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }catch(ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        //</editor-fold>

        /* Create and display the form */
/*
        try {
            UIManager.setLookAndFeel("com.birosoft.liquid.LiquidLookAndFeel");
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }

*/
        NativeInterface.open();
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Client();
            }
        });
        NativeInterface.runEventPump();
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        signup = new javax.swing.JDialog();
        login = new javax.swing.JTextField();
        title = new javax.swing.JLabel();
        loginL = new javax.swing.JLabel();
        passwordL = new javax.swing.JLabel();
        password = new javax.swing.JPasswordField();
        passwordLC = new javax.swing.JLabel();
        passwordC = new javax.swing.JPasswordField();
        mailL = new javax.swing.JLabel();
        mail = new javax.swing.JTextField();
        fNameL = new javax.swing.JLabel();
        fName = new javax.swing.JTextField();
        lNameL = new javax.swing.JLabel();
        lName = new javax.swing.JTextField();
        mNameL = new javax.swing.JLabel();
        mName = new javax.swing.JTextField();
        check = new javax.swing.JCheckBox();
        cancel = new javax.swing.JButton();
        ok = new javax.swing.JButton();
        getTasks = new javax.swing.JDialog();
        getTasksL = new javax.swing.JLabel();
        notFree = new javax.swing.JRadioButton();
        free = new javax.swing.JRadioButton();
        loginT = new javax.swing.JTextField();
        allT = new javax.swing.JRadioButton();
        getT = new javax.swing.JButton();
        project = new javax.swing.JTextField();
        projectT = new javax.swing.JRadioButton();
        allP = new javax.swing.JRadioButton();
        tasksG = new javax.swing.ButtonGroup();
        projectsG = new javax.swing.ButtonGroup();
        getMilestones = new javax.swing.JDialog();
        getMilestonesL = new javax.swing.JLabel();
        loginMB = new javax.swing.JRadioButton();
        loginMBAll = new javax.swing.JRadioButton();
        loginM = new javax.swing.JTextField();
        getM = new javax.swing.JButton();
        milestoneG = new javax.swing.ButtonGroup();
        createTask = new javax.swing.JDialog();
        createTaskL = new javax.swing.JLabel();
        taskNameL = new javax.swing.JLabel();
        taskName = new javax.swing.JTextField();
        taskProject = new javax.swing.JComboBox();
        taskScroll = new javax.swing.JScrollPane();
        taskComments = new javax.swing.JTextArea();
        taskParent = new javax.swing.JComboBox();
        taskSDL = new javax.swing.JLabel();
        taskStatus = new javax.swing.JComboBox();
        taskWorker = new javax.swing.JComboBox();
        taskStart = new com.toedter.calendar.JDateChooser();
        taskStartL = new javax.swing.JLabel();
        taskType = new javax.swing.JComboBox();
        taskPriorityL = new javax.swing.JLabel();
        createT = new javax.swing.JButton();
        taskProjectL = new javax.swing.JLabel();
        taskParentL = new javax.swing.JLabel();
        taskStatusL = new javax.swing.JLabel();
        taskWorkerL = new javax.swing.JLabel();
        taskTypeL = new javax.swing.JLabel();
        taskDaysL = new javax.swing.JLabel();
        taskPriority = new javax.swing.JSpinner();
        taskSD = new javax.swing.JSpinner();
        createProject = new javax.swing.JDialog();
        createProjectL = new javax.swing.JLabel();
        projectNameL = new javax.swing.JLabel();
        projectName = new javax.swing.JTextField();
        projectPriorityL = new javax.swing.JLabel();
        projectPriority = new javax.swing.JSpinner();
        projectDL = new com.toedter.calendar.JDateChooser();
        projectDLL = new javax.swing.JLabel();
        projectScroll = new javax.swing.JScrollPane();
        projectComments = new javax.swing.JTextArea();
        createP = new javax.swing.JButton();
        createMilestone = new javax.swing.JDialog();
        createMilestoneL = new javax.swing.JLabel();
        milestoneNameL = new javax.swing.JLabel();
        milestoneName = new javax.swing.JTextField();
        milestoneWorkerL = new javax.swing.JLabel();
        milestoneWorker = new javax.swing.JComboBox();
        createM = new javax.swing.JButton();
        changeParent = new javax.swing.JDialog();
        changeParentL = new javax.swing.JLabel();
        parentLL = new javax.swing.JLabel();
        parentL = new javax.swing.JComboBox();
        changeP = new javax.swing.JButton();
        setLoadFactor = new javax.swing.JDialog();
        setLoadFactorL = new javax.swing.JLabel();
        loadFL = new javax.swing.JLabel();
        setLoad = new javax.swing.JButton();
        setLF = new javax.swing.JSpinner();
        gantChart = new javax.swing.JDialog();
        changePriority = new javax.swing.JDialog();
        changePriorityL = new javax.swing.JLabel();
        priorityL = new javax.swing.JLabel();
        changePr = new javax.swing.JButton();
        priorityC = new javax.swing.JSpinner();
        popup = new javax.swing.JDialog();
        scroll = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();
        container = new org.jdesktop.swingx.JXTaskPaneContainer();
        projectPane = new org.jdesktop.swingx.JXTaskPane();
        taskPane = new org.jdesktop.swingx.JXTaskPane();
        milestonePane = new org.jdesktop.swingx.JXTaskPane();
        subscroll = new javax.swing.JScrollPane();
        subtable = new javax.swing.JTable();
        subLabel2 = new javax.swing.JLabel();
        subcom = new javax.swing.JScrollPane();
        subcomt = new javax.swing.JTextArea();
        subLabel = new javax.swing.JLabel();
        separator = new javax.swing.JSeparator();
        add = new javax.swing.JButton();
        filter = new javax.swing.JTextField();
        checkFilter = new javax.swing.JComboBox();
        barTasks = new javax.swing.JToolBar();
        complete = new javax.swing.JButton();
        parent = new javax.swing.JButton();
        gant = new javax.swing.JButton();
        priority = new javax.swing.JButton();
        worker = new javax.swing.JButton();
        barSetting = new javax.swing.JToolBar();
        update = new javax.swing.JButton();
        changePass = new javax.swing.JButton();
        logout = new javax.swing.JButton();
        barUsers = new javax.swing.JToolBar();
        workers = new javax.swing.JButton();
        load = new javax.swing.JButton();
        manager = new javax.swing.JButton();
        logo = new javax.swing.JLabel();

        signup.setTitle("Registration");
        signup.setAlwaysOnTop(true);
        signup.setMinimumSize(new java.awt.Dimension(400, 320));
        signup.setResizable(false);
        signup.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentHidden(java.awt.event.ComponentEvent evt) {
                signupComponentHidden(evt);
            }
            public void componentShown(java.awt.event.ComponentEvent evt) {
                signupComponentShown(evt);
            }
        });

        login.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N

        title.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        title.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        title.setText("Sign up");

        loginL.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        loginL.setLabelFor(login);
        loginL.setText("Login:");

        passwordL.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        passwordL.setLabelFor(password);
        passwordL.setText("Password:");

        password.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N

        passwordLC.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        passwordLC.setLabelFor(passwordC);
        passwordLC.setText("Confirm password:");

        passwordC.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N

        mailL.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        mailL.setLabelFor(mail);
        mailL.setText("Mail:");

        mail.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N

        fNameL.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        fNameL.setLabelFor(fName);
        fNameL.setText("First namel:");

        fName.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N

        lNameL.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        lNameL.setLabelFor(lName);
        lNameL.setText("Last name:");

        lName.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N

        mNameL.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        mNameL.setLabelFor(mName);
        mNameL.setText("Middle name:");

        mName.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N

        check.setText("Agree with all rules");

        cancel.setText("Cancel");
        cancel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cancelMouseClicked(evt);
            }
        });

        ok.setText("OK");
        ok.setPreferredSize(new java.awt.Dimension(65, 23));
        ok.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                okMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout signupLayout = new javax.swing.GroupLayout(signup.getContentPane());
        signup.getContentPane().setLayout(signupLayout);
        signupLayout.setHorizontalGroup(
            signupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(signupLayout.createSequentialGroup()
                .addGroup(signupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(signupLayout.createSequentialGroup()
                        .addGap(140, 140, 140)
                        .addComponent(title, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(signupLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(signupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, signupLayout.createSequentialGroup()
                                .addComponent(loginL, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(login, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(signupLayout.createSequentialGroup()
                                .addComponent(passwordL, javax.swing.GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(password, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, signupLayout.createSequentialGroup()
                                .addComponent(passwordLC, javax.swing.GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(passwordC, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(signupLayout.createSequentialGroup()
                                .addComponent(mailL, javax.swing.GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(mail, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, signupLayout.createSequentialGroup()
                                .addComponent(fNameL, javax.swing.GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(fName, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(signupLayout.createSequentialGroup()
                                .addComponent(lNameL, javax.swing.GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(lName, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, signupLayout.createSequentialGroup()
                                .addComponent(mNameL, javax.swing.GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(signupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(signupLayout.createSequentialGroup()
                                        .addComponent(cancel)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(ok, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(mName, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                .addContainerGap())
            .addGroup(signupLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(check)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        signupLayout.setVerticalGroup(
            signupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(signupLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(title, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(signupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(login)
                    .addComponent(loginL, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(signupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(passwordL, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(password))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(signupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(passwordLC, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(passwordC, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(signupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(mail)
                    .addComponent(mailL, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(signupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(fName)
                    .addComponent(fNameL, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(signupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lName)
                    .addComponent(lNameL, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(signupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(mName)
                    .addComponent(mNameL, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(check)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 4, Short.MAX_VALUE)
                .addGroup(signupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancel)
                    .addComponent(ok, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        getTasks.setTitle("Get tasks");
        getTasks.setAlwaysOnTop(true);
        getTasks.setMinimumSize(new java.awt.Dimension(387, 265));
        getTasks.setResizable(false);
        getTasks.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentHidden(java.awt.event.ComponentEvent evt) {
                getTasksComponentHidden(evt);
            }
            public void componentShown(java.awt.event.ComponentEvent evt) {
                getTasksComponentShown(evt);
            }
        });

        getTasksL.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        getTasksL.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        getTasksL.setText("Get tasks");
        getTasksL.setAlignmentX(0.5F);

        notFree.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        notFree.setText("Tasks by User:");

        free.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        free.setText("Free tasks");

        loginT.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        loginT.setEnabled(false);

        allT.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        allT.setSelected(true);
        allT.setText("All");

        getT.setText("Get");
        getT.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                getTMouseClicked(evt);
            }
        });

        project.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        project.setEnabled(false);

        projectT.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        projectT.setText("Project name:");

        allP.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        allP.setSelected(true);
        allP.setText("All");

        javax.swing.GroupLayout getTasksLayout = new javax.swing.GroupLayout(getTasks.getContentPane());
        getTasks.getContentPane().setLayout(getTasksLayout);
        getTasksLayout.setHorizontalGroup(
            getTasksLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(getTasksLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(getT)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, getTasksLayout.createSequentialGroup()
                .addGap(0, 131, Short.MAX_VALUE)
                .addComponent(getTasksL, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(131, 131, 131))
            .addGroup(getTasksLayout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(getTasksLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(notFree)
                    .addComponent(allT)
                    .addComponent(free)
                    .addComponent(projectT)
                    .addComponent(allP))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(getTasksLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(loginT, javax.swing.GroupLayout.DEFAULT_SIZE, 183, Short.MAX_VALUE)
                    .addComponent(project))
                .addGap(20, 20, 20))
        );
        getTasksLayout.setVerticalGroup(
            getTasksLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(getTasksLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(getTasksL, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(getTasksLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(loginT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(getTasksLayout.createSequentialGroup()
                        .addComponent(notFree)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(free)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(allT)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(getTasksLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(project, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(projectT))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(allP)
                .addGap(12, 12, 12)
                .addComponent(getT)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        getMilestones.setTitle("Get milestones");
        getMilestones.setAlwaysOnTop(true);
        getMilestones.setResizable(false);
        getMilestones.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentHidden(java.awt.event.ComponentEvent evt) {
                getMilestonesComponentHidden(evt);
            }
            public void componentShown(java.awt.event.ComponentEvent evt) {
                getMilestonesComponentShown(evt);
            }
        });

        getMilestonesL.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        getMilestonesL.setText("Get milestones");

        loginMB.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        loginMB.setText("User name:");

        loginMBAll.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        loginMBAll.setSelected(true);
        loginMBAll.setText("All");

        loginM.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        loginM.setEnabled(false);

        getM.setText("Get");
        getM.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                getMMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout getMilestonesLayout = new javax.swing.GroupLayout(getMilestones.getContentPane());
        getMilestones.getContentPane().setLayout(getMilestonesLayout);
        getMilestonesLayout.setHorizontalGroup(
            getMilestonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(getMilestonesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(getMilestonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(getMilestonesLayout.createSequentialGroup()
                        .addGroup(getMilestonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(getMilestonesLayout.createSequentialGroup()
                                .addComponent(loginMB)
                                .addGap(18, 18, 18)
                                .addComponent(loginM))
                            .addGroup(getMilestonesLayout.createSequentialGroup()
                                .addComponent(loginMBAll)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, getMilestonesLayout.createSequentialGroup()
                        .addGap(0, 104, Short.MAX_VALUE)
                        .addGroup(getMilestonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, getMilestonesLayout.createSequentialGroup()
                                .addComponent(getMilestonesL)
                                .addGap(110, 110, 110))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, getMilestonesLayout.createSequentialGroup()
                                .addComponent(getM)
                                .addContainerGap())))))
        );
        getMilestonesLayout.setVerticalGroup(
            getMilestonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(getMilestonesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(getMilestonesL)
                .addGap(22, 22, 22)
                .addGroup(getMilestonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(loginMB)
                    .addComponent(loginM, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(loginMBAll)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(getM)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        createTask.setTitle("Create task");
        createTask.setAlwaysOnTop(true);
        createTask.setResizable(false);
        createTask.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentHidden(java.awt.event.ComponentEvent evt) {
                createTaskComponentHidden(evt);
            }
            public void componentShown(java.awt.event.ComponentEvent evt) {
                createTaskComponentShown(evt);
            }
        });

        createTaskL.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        createTaskL.setText("Create task");

        taskNameL.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        taskNameL.setLabelFor(taskName);
        taskNameL.setText("Task name:");

        taskName.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N

        taskProject.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        taskProject.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "For project..." }));

        taskComments.setColumns(20);
        taskComments.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        taskComments.setRows(5);
        taskComments.setText("Comments...");
        taskScroll.setViewportView(taskComments);

        taskParent.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        taskParent.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Task parent..." }));

        taskSDL.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        taskSDL.setLabelFor(taskSD);
        taskSDL.setText("Scheduled duration:");

        taskStatus.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        taskStatus.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Status...", "Active", "Delayed", "Have a problem" }));

        taskWorker.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        taskWorker.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Worker for task..." }));

        taskStart.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N

        taskStartL.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        taskStartL.setLabelFor(taskStart);
        taskStartL.setText("Start:");

        taskType.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        taskType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Type...", "Static", "Dynamic", "Fixed" }));
        taskType.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                taskTypeItemStateChanged(evt);
            }
        });

        taskPriorityL.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        taskPriorityL.setLabelFor(taskPriority);
        taskPriorityL.setText("Priority:");

        createT.setText("Create");
        createT.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                createTMouseClicked(evt);
            }
        });

        taskProjectL.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        taskProjectL.setLabelFor(taskProject);
        taskProjectL.setText("Project name:");

        taskParentL.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        taskParentL.setLabelFor(taskParent);
        taskParentL.setText("Task parent:");

        taskStatusL.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        taskStatusL.setLabelFor(taskStatus);
        taskStatusL.setText("Status:");

        taskWorkerL.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        taskWorkerL.setLabelFor(taskWorker);
        taskWorkerL.setText("Worker:");

        taskTypeL.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        taskTypeL.setLabelFor(taskType);
        taskTypeL.setText("Type:");

        taskDaysL.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        taskDaysL.setLabelFor(taskSD);
        taskDaysL.setText("days");

        taskPriority.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N

        taskSD.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N

        javax.swing.GroupLayout createTaskLayout = new javax.swing.GroupLayout(createTask.getContentPane());
        createTask.getContentPane().setLayout(createTaskLayout);
        createTaskLayout.setHorizontalGroup(
            createTaskLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(createTaskLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(createTaskLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(createTaskLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, createTaskLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, createTaskLayout.createSequentialGroup()
                                .addGroup(createTaskLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(taskNameL)
                                    .addComponent(taskStartL))
                                .addGap(67, 67, 67))
                            .addGroup(createTaskLayout.createSequentialGroup()
                                .addComponent(taskPriorityL)
                                .addGap(10, 10, 10)))
                        .addGroup(createTaskLayout.createSequentialGroup()
                            .addComponent(taskSDL)
                            .addGap(24, 24, 24)))
                    .addGroup(createTaskLayout.createSequentialGroup()
                        .addGroup(createTaskLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(taskProjectL)
                            .addComponent(taskParentL)
                            .addComponent(taskStatusL)
                            .addComponent(taskWorkerL)
                            .addComponent(taskTypeL))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addGroup(createTaskLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(taskType, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(taskStart, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(taskWorker, javax.swing.GroupLayout.Alignment.LEADING, 0, 150, Short.MAX_VALUE)
                    .addComponent(taskStatus, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(taskParent, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(taskProject, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(taskName)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, createTaskLayout.createSequentialGroup()
                        .addGroup(createTaskLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(taskPriority, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 46, Short.MAX_VALUE)
                            .addComponent(taskSD, javax.swing.GroupLayout.Alignment.LEADING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(taskDaysL)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(createTaskLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(createTaskLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(createTaskL)
                        .addGap(0, 274, Short.MAX_VALUE))
                    .addComponent(taskScroll)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, createTaskLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(createT)))
                .addContainerGap())
        );
        createTaskLayout.setVerticalGroup(
            createTaskLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(createTaskLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(createTaskL)
                .addGap(18, 18, 18)
                .addGroup(createTaskLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(createTaskLayout.createSequentialGroup()
                        .addGroup(createTaskLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(taskNameL)
                            .addComponent(taskName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(createTaskLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(taskProject, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(taskProjectL))
                        .addGap(18, 18, 18)
                        .addGroup(createTaskLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(taskParent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(taskParentL))
                        .addGap(18, 18, 18)
                        .addGroup(createTaskLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(taskStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(taskStatusL))
                        .addGap(18, 18, 18)
                        .addGroup(createTaskLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(taskWorker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(taskWorkerL))
                        .addGap(18, 18, 18)
                        .addGroup(createTaskLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(taskStart, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(taskStartL, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(createTaskLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(taskType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(taskTypeL))
                        .addGap(18, 18, 18)
                        .addGroup(createTaskLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(taskPriorityL)
                            .addComponent(taskPriority, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(taskScroll))
                .addGap(18, 18, 18)
                .addGroup(createTaskLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(taskSDL)
                    .addComponent(createT)
                    .addComponent(taskDaysL)
                    .addComponent(taskSD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        createProject.setTitle("Create project");
        createProject.setAlwaysOnTop(true);
        createProject.setResizable(false);
        createProject.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentHidden(java.awt.event.ComponentEvent evt) {
                createProjectComponentHidden(evt);
            }
            public void componentShown(java.awt.event.ComponentEvent evt) {
                createProjectComponentShown(evt);
            }
        });

        createProjectL.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        createProjectL.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        createProjectL.setText("Create project");
        createProjectL.setAlignmentX(0.5F);

        projectNameL.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        projectNameL.setLabelFor(projectName);
        projectNameL.setText("Name:");

        projectName.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N

        projectPriorityL.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        projectPriorityL.setLabelFor(projectPriority);
        projectPriorityL.setText("Priority:");

        projectPriority.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N

        projectDL.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N

        projectDLL.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        projectDLL.setLabelFor(projectDL);
        projectDLL.setText("Deadline:");

        projectComments.setColumns(20);
        projectComments.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        projectComments.setRows(5);
        projectComments.setText("Comments...");
        projectScroll.setViewportView(projectComments);

        createP.setText("Create");
        createP.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                createPMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout createProjectLayout = new javax.swing.GroupLayout(createProject.getContentPane());
        createProject.getContentPane().setLayout(createProjectLayout);
        createProjectLayout.setHorizontalGroup(
            createProjectLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(createProjectLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(createProjectLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(createProjectLayout.createSequentialGroup()
                        .addGroup(createProjectLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(projectScroll)
                            .addGroup(createProjectLayout.createSequentialGroup()
                                .addGroup(createProjectLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(projectNameL)
                                    .addComponent(projectPriorityL))
                                .addGap(10, 10, 10)
                                .addGroup(createProjectLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(createProjectLayout.createSequentialGroup()
                                        .addComponent(projectPriority, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(32, 32, 32)
                                        .addComponent(projectDLL)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(projectDL, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(projectName))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, createProjectLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(createProjectLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, createProjectLayout.createSequentialGroup()
                                .addComponent(createProjectL, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(96, 96, 96))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, createProjectLayout.createSequentialGroup()
                                .addComponent(createP)
                                .addContainerGap())))))
        );
        createProjectLayout.setVerticalGroup(
            createProjectLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(createProjectLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(createProjectL)
                .addGap(18, 18, 18)
                .addGroup(createProjectLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(projectName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(projectNameL))
                .addGap(18, 18, 18)
                .addGroup(createProjectLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(projectDL, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(createProjectLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(projectPriority, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(projectPriorityL)
                        .addComponent(projectDLL)))
                .addGap(18, 18, 18)
                .addComponent(projectScroll, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(createP)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        createMilestone.setTitle("Create milestone");
        createMilestone.setAlwaysOnTop(true);
        createMilestone.setResizable(false);
        createMilestone.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentHidden(java.awt.event.ComponentEvent evt) {
                createMilestoneComponentHidden(evt);
            }
            public void componentShown(java.awt.event.ComponentEvent evt) {
                createMilestoneComponentShown(evt);
            }
        });

        createMilestoneL.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        createMilestoneL.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        createMilestoneL.setText("Create milestone");
        createMilestoneL.setAlignmentX(0.5F);

        milestoneNameL.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        milestoneNameL.setLabelFor(createMilestone);
        milestoneNameL.setText("Milestone name:");

        milestoneName.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N

        milestoneWorkerL.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        milestoneWorkerL.setLabelFor(milestoneWorker);
        milestoneWorkerL.setText("Choose worker:");

        milestoneWorker.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        milestoneWorker.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Worker..." }));

        createM.setText("Create");
        createM.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                createMMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout createMilestoneLayout = new javax.swing.GroupLayout(createMilestone.getContentPane());
        createMilestone.getContentPane().setLayout(createMilestoneLayout);
        createMilestoneLayout.setHorizontalGroup(
            createMilestoneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(createMilestoneLayout.createSequentialGroup()
                .addGroup(createMilestoneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(createMilestoneLayout.createSequentialGroup()
                        .addGap(84, 84, 84)
                        .addComponent(createMilestoneL, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(createMilestoneLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(milestoneNameL)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 35, Short.MAX_VALUE)
                        .addComponent(milestoneName, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(createMilestoneLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(milestoneWorkerL)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(milestoneWorker, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, createMilestoneLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(createM)))
                .addContainerGap())
        );
        createMilestoneLayout.setVerticalGroup(
            createMilestoneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(createMilestoneLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(createMilestoneL)
                .addGap(18, 18, 18)
                .addGroup(createMilestoneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(milestoneNameL)
                    .addComponent(milestoneName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(createMilestoneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(milestoneWorker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(milestoneWorkerL))
                .addGap(18, 18, 18)
                .addComponent(createM)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        changeParent.setTitle("Change parent");
        changeParent.setAlwaysOnTop(true);
        changeParent.setResizable(false);
        changeParent.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentHidden(java.awt.event.ComponentEvent evt) {
                changeParentComponentHidden(evt);
            }
            public void componentShown(java.awt.event.ComponentEvent evt) {
                changeParentComponentShown(evt);
            }
        });

        changeParentL.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        changeParentL.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        changeParentL.setText("Change parent");
        changeParentL.setAlignmentX(0.5F);

        parentLL.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        parentLL.setLabelFor(parentL);
        parentLL.setText("Parent:");

        parentL.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        parentL.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Task..." }));

        changeP.setText("Change");
        changeP.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                changePMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout changeParentLayout = new javax.swing.GroupLayout(changeParent.getContentPane());
        changeParent.getContentPane().setLayout(changeParentLayout);
        changeParentLayout.setHorizontalGroup(
            changeParentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(changeParentLayout.createSequentialGroup()
                .addGroup(changeParentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(changeParentLayout.createSequentialGroup()
                        .addGap(84, 84, 84)
                        .addComponent(changeParentL, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(changeParentLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(parentLL)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 87, Short.MAX_VALUE)
                        .addComponent(parentL, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, changeParentLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(changeP)))
                .addContainerGap())
        );
        changeParentLayout.setVerticalGroup(
            changeParentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(changeParentLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(changeParentL)
                .addGap(18, 18, 18)
                .addGroup(changeParentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(parentL, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(parentLL))
                .addGap(18, 18, 18)
                .addComponent(changeP)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        setLoadFactor.setTitle("Set load factor");
        setLoadFactor.setAlwaysOnTop(true);
        setLoadFactor.setResizable(false);
        setLoadFactor.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentHidden(java.awt.event.ComponentEvent evt) {
                setLoadFactorComponentHidden(evt);
            }
            public void componentShown(java.awt.event.ComponentEvent evt) {
                setLoadFactorComponentShown(evt);
            }
        });

        setLoadFactorL.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        setLoadFactorL.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        setLoadFactorL.setText("Set load factor");
        setLoadFactorL.setAlignmentX(0.5F);

        loadFL.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        loadFL.setLabelFor(parentL);
        loadFL.setText("Load factor:");

        setLoad.setText("Set");
        setLoad.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                setLoadMouseClicked(evt);
            }
        });

        setLF.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N

        javax.swing.GroupLayout setLoadFactorLayout = new javax.swing.GroupLayout(setLoadFactor.getContentPane());
        setLoadFactor.getContentPane().setLayout(setLoadFactorLayout);
        setLoadFactorLayout.setHorizontalGroup(
            setLoadFactorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(setLoadFactorLayout.createSequentialGroup()
                .addGroup(setLoadFactorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, setLoadFactorLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(setLoad))
                    .addGroup(setLoadFactorLayout.createSequentialGroup()
                        .addGap(84, 84, 84)
                        .addComponent(setLoadFactorL, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 74, Short.MAX_VALUE))
                    .addGroup(setLoadFactorLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(loadFL)
                        .addGap(103, 103, 103)
                        .addComponent(setLF)))
                .addContainerGap())
        );
        setLoadFactorLayout.setVerticalGroup(
            setLoadFactorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(setLoadFactorLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(setLoadFactorL)
                .addGap(18, 18, 18)
                .addGroup(setLoadFactorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(loadFL)
                    .addComponent(setLF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(setLoad)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        gantChart.setTitle("Gantt Chart");
        gantChart.setMinimumSize(new java.awt.Dimension(200, 100));

        javax.swing.GroupLayout gantChartLayout = new javax.swing.GroupLayout(gantChart.getContentPane());
        gantChart.getContentPane().setLayout(gantChartLayout);
        gantChartLayout.setHorizontalGroup(
            gantChartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 800, Short.MAX_VALUE)
        );
        gantChartLayout.setVerticalGroup(
            gantChartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );

        changePriority.setTitle("Change priority");
        changePriority.setAlwaysOnTop(true);
        changePriority.setResizable(false);
        changePriority.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentHidden(java.awt.event.ComponentEvent evt) {
                changePriorityComponentHidden(evt);
            }
            public void componentShown(java.awt.event.ComponentEvent evt) {
                changePriorityComponentShown(evt);
            }
        });

        changePriorityL.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        changePriorityL.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        changePriorityL.setText("Change Priority");
        changePriorityL.setAlignmentX(0.5F);

        priorityL.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        priorityL.setLabelFor(parentL);
        priorityL.setText("Priority:");

        changePr.setText("Change");
        changePr.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                changePrMouseClicked(evt);
            }
        });

        priorityC.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N

        javax.swing.GroupLayout changePriorityLayout = new javax.swing.GroupLayout(changePriority.getContentPane());
        changePriority.getContentPane().setLayout(changePriorityLayout);
        changePriorityLayout.setHorizontalGroup(
            changePriorityLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(changePriorityLayout.createSequentialGroup()
                .addGroup(changePriorityLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, changePriorityLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(changePr))
                    .addGroup(changePriorityLayout.createSequentialGroup()
                        .addGap(84, 84, 84)
                        .addComponent(changePriorityL, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 74, Short.MAX_VALUE))
                    .addGroup(changePriorityLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(priorityL)
                        .addGap(103, 103, 103)
                        .addComponent(priorityC)))
                .addContainerGap())
        );
        changePriorityLayout.setVerticalGroup(
            changePriorityLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(changePriorityLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(changePriorityL)
                .addGap(18, 18, 18)
                .addGroup(changePriorityLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(priorityL)
                    .addComponent(priorityC, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(changePr)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout popupLayout = new javax.swing.GroupLayout(popup.getContentPane());
        popup.getContentPane().setLayout(popupLayout);
        popupLayout.setHorizontalGroup(
            popupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        popupLayout.setVerticalGroup(
            popupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("ProjectL");
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setIconImage(Toolkit.getDefaultToolkit().getImage("images/icon.png"));
        setName("Client"); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        addWindowStateListener(new java.awt.event.WindowStateListener() {
            public void windowStateChanged(java.awt.event.WindowEvent evt) {
                formWindowStateChanged(evt);
            }
        });

        scroll.setToolTipText("");
        scroll.setAutoscrolls(true);

        table.setAutoCreateRowSorter(true);
        table.setModel(new Model());
        table.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_LAST_COLUMN);
        table.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        scroll.setViewportView(table);

        container.setBackground(new java.awt.Color(204, 204, 255));
        container.setToolTipText("");
        container.setAutoscrolls(true);
        container.setScrollableTracksViewportHeight(true);
        container.setLayout(new java.awt.GridBagLayout());

        projectPane.setToolTipText("Projects");
        projectPane.setOpaque(true);
        projectPane.setTitle("Projects");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        container.add(projectPane, gridBagConstraints);

        taskPane.setToolTipText("Tasks");
        taskPane.setAutoscrolls(true);
        taskPane.setScrollOnExpand(true);
        taskPane.setTitle("Tasks");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 12;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        container.add(taskPane, gridBagConstraints);

        milestonePane.setToolTipText("Milestones");
        milestonePane.setTitle("Milestones");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_START;
        gridBagConstraints.weighty = 1.0;
        container.add(milestonePane, gridBagConstraints);

        subtable.setModel(new Model());
        subtable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_LAST_COLUMN);
        subtable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        subscroll.setViewportView(subtable);

        subLabel2.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        subLabel2.setText("Comments to period:");

        subcomt.setColumns(20);
        subcomt.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        subcomt.setRows(5);
        subcom.setViewportView(subcomt);

        subLabel.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        subLabel.setText("Periods:");

        separator.setOrientation(javax.swing.SwingConstants.VERTICAL);

        add.setText("Add");

        filter.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        filter.setEnabled(false);
        filter.setMinimumSize(new java.awt.Dimension(6, 39));
        filter.setPreferredSize(new java.awt.Dimension(6, 39));

        checkFilter.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        checkFilter.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "<None filter>" }));

        barTasks.setRollover(true);
        barTasks.setMaximumSize(new java.awt.Dimension(208, 137));
        barTasks.setMinimumSize(new java.awt.Dimension(83, 32));

        complete.setToolTipText("");
        complete.setBorderPainted(false);
        complete.setContentAreaFilled(false);
        complete.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        complete.setEnabled(false);
        complete.setFocusPainted(false);
        complete.setMaximumSize(new java.awt.Dimension(30, 30));
        complete.setMinimumSize(new java.awt.Dimension(30, 30));
        complete.setPreferredSize(new java.awt.Dimension(30, 30));
        barTasks.add(complete);

        parent.setBorderPainted(false);
        parent.setContentAreaFilled(false);
        parent.setEnabled(false);
        parent.setFocusPainted(false);
        parent.setMaximumSize(new java.awt.Dimension(30, 30));
        parent.setMinimumSize(new java.awt.Dimension(30, 30));
        parent.setPreferredSize(new java.awt.Dimension(30, 30));
        barTasks.add(parent);

        gant.setBorderPainted(false);
        gant.setContentAreaFilled(false);
        gant.setFocusPainted(false);
        gant.setFocusable(false);
        gant.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        gant.setMaximumSize(new java.awt.Dimension(30, 30));
        gant.setMinimumSize(new java.awt.Dimension(30, 30));
        gant.setPreferredSize(new java.awt.Dimension(30, 30));
        gant.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        barTasks.add(gant);

        priority.setToolTipText("Select task and push on in to change task priority");
        priority.setBorderPainted(false);
        priority.setContentAreaFilled(false);
        priority.setFocusPainted(false);
        priority.setFocusable(false);
        priority.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        priority.setMaximumSize(new java.awt.Dimension(30, 30));
        priority.setMinimumSize(new java.awt.Dimension(30, 30));
        priority.setPreferredSize(new java.awt.Dimension(30, 30));
        priority.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        barTasks.add(priority);

        worker.setBorderPainted(false);
        worker.setContentAreaFilled(false);
        worker.setEnabled(false);
        worker.setFocusPainted(false);
        worker.setMaximumSize(new java.awt.Dimension(30, 30));
        worker.setMinimumSize(new java.awt.Dimension(30, 30));
        worker.setPreferredSize(new java.awt.Dimension(30, 30));
        barTasks.add(worker);

        barSetting.setRollover(true);

        update.setBorderPainted(false);
        update.setContentAreaFilled(false);
        update.setFocusPainted(false);
        update.setMaximumSize(new java.awt.Dimension(30, 30));
        update.setMinimumSize(new java.awt.Dimension(30, 30));
        update.setPreferredSize(new java.awt.Dimension(30, 30));
        barSetting.add(update);

        changePass.setBorderPainted(false);
        changePass.setContentAreaFilled(false);
        changePass.setFocusPainted(false);
        changePass.setMaximumSize(new java.awt.Dimension(30, 30));
        changePass.setMinimumSize(new java.awt.Dimension(30, 30));
        changePass.setPreferredSize(new java.awt.Dimension(30, 30));
        changePass.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                changePassMouseClicked(evt);
            }
        });
        barSetting.add(changePass);

        logout.setBorderPainted(false);
        logout.setContentAreaFilled(false);
        logout.setFocusPainted(false);
        logout.setMaximumSize(new java.awt.Dimension(30, 30));
        logout.setMinimumSize(new java.awt.Dimension(30, 30));
        logout.setPreferredSize(new java.awt.Dimension(30, 30));
        logout.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                logoutMouseClicked(evt);
            }
        });
        barSetting.add(logout);

        barUsers.setRollover(true);

        workers.setBorderPainted(false);
        workers.setContentAreaFilled(false);
        workers.setFocusPainted(false);
        workers.setMaximumSize(new java.awt.Dimension(30, 30));
        workers.setMinimumSize(new java.awt.Dimension(30, 30));
        workers.setPreferredSize(new java.awt.Dimension(30, 30));
        workers.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                workersMouseClicked(evt);
            }
        });
        barUsers.add(workers);

        load.setBorderPainted(false);
        load.setContentAreaFilled(false);
        load.setEnabled(false);
        load.setFocusPainted(false);
        load.setMaximumSize(new java.awt.Dimension(30, 30));
        load.setMinimumSize(new java.awt.Dimension(30, 30));
        load.setPreferredSize(new java.awt.Dimension(30, 30));
        barUsers.add(load);

        manager.setBorderPainted(false);
        manager.setContentAreaFilled(false);
        manager.setEnabled(false);
        manager.setFocusPainted(false);
        manager.setMaximumSize(new java.awt.Dimension(30, 30));
        manager.setMinimumSize(new java.awt.Dimension(30, 30));
        manager.setPreferredSize(new java.awt.Dimension(30, 30));
        barUsers.add(manager);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(container, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(logo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scroll, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(barTasks, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(barUsers, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(barSetting, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(checkFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(filter, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(subscroll, javax.swing.GroupLayout.DEFAULT_SIZE, 643, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(subLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addGap(14, 14, 14)
                        .addComponent(separator, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(subLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(subcom, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(add, javax.swing.GroupLayout.Alignment.TRAILING))))
                .addGap(2, 2, 2))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(3, 3, 3)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(barSetting, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(barUsers, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(barTasks, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                            .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(checkFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(filter, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(9, 9, 9))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(logo, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(container, javax.swing.GroupLayout.DEFAULT_SIZE, 450, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(scroll, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(subLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(subLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(subcom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(add))
                                    .addComponent(subscroll, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(3, 3, 3)
                                .addComponent(separator, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap())))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void signupComponentHidden(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_signupComponentHidden
        form.setEnabled(true);
    }//GEN-LAST:event_signupComponentHidden

    private void signupComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_signupComponentShown
        form.setEnabled(false);
    }//GEN-LAST:event_signupComponentShown

    private void cancelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cancelMouseClicked
        signup.hide();
        defaultVs();
    }//GEN-LAST:event_cancelMouseClicked

    private void okMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_okMouseClicked
        try {
            Boolean res = func.signup(login.getText(), password.getText(), passwordC.getText(), mail.getText(), fName.getText(), lName.getText(), mName.getText(), check.isSelected());
            if (res != null | res == true) {
                JOptionPane.showMessageDialog(null, "Now you can use login and password to Sign in", "Sign up", JOptionPane.INFORMATION_MESSAGE);
                signup.hide();
                defaultVs();
            }
        } catch (IOException | JSONException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_okMouseClicked

    private void getTMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_getTMouseClicked
        String UserL = null;
        String ProjectN = null;
        if (notFree.isSelected()) {
            if (loginT.getText().isEmpty() | !Signin.checkLogin(loginT.getText())) {
                JOptionPane.showMessageDialog(null, "Wrong login", "Get tasks", JOptionPane.ERROR_MESSAGE);
                return;
            }
            UserL = loginT.getText().trim();
        }
        if (projectT.isSelected()) {
            if (project.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Wrong project name", "Get tasks", JOptionPane.ERROR_MESSAGE);
                return;
            }
            ProjectN = project.getText().trim();
        }
        Task [] tasks = null;
        try {
            tasks = func.getTasks(UserL, ProjectN, free.isSelected());
        } catch (IOException | JSONException | ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        tasksToTable(tasks);
        Vector v = new Vector(Arrays.asList(1, UserL, ProjectN, free.isSelected()));
        func.setVector(v);
        getTasks.hide();
    }//GEN-LAST:event_getTMouseClicked

    private void getMMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_getMMouseClicked
        String UserL = null;
        if (loginMB.isSelected()) {
            if (loginM.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Wrong login", "Get tasks", JOptionPane.ERROR_MESSAGE);
                return;
            }
            UserL = loginM.getText().trim();
        }
        Milestone [] miles = null;
        try {
            miles = func.getMilestones(UserL);
        } catch (IOException | JSONException | ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        milestonesToTable(miles);
        getMilestones.hide();
        Vector v = new Vector(Arrays.asList(2, UserL));
        func.setVector(v);
    }//GEN-LAST:event_getMMouseClicked

    private void getMilestonesComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_getMilestonesComponentShown
        setEnabled(false);
    }//GEN-LAST:event_getMilestonesComponentShown

    private void getTasksComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_getTasksComponentShown
        setEnabled(false);
    }//GEN-LAST:event_getTasksComponentShown

    private void getTasksComponentHidden(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_getTasksComponentHidden
        setEnabled(true);
        setVisible(true);
    }//GEN-LAST:event_getTasksComponentHidden

    private void getMilestonesComponentHidden(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_getMilestonesComponentHidden
        setEnabled(true);
        setVisible(true);
    }//GEN-LAST:event_getMilestonesComponentHidden

    private void createTaskComponentHidden(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_createTaskComponentHidden
        taskName.setText(null);
        taskProject.removeAllItems();
        taskProject.addItem("For project...");
        taskParent.removeAllItems();
        taskParent.addItem("Task parent...");
        taskStatus.setSelectedIndex(0);
        taskType.setSelectedIndex(0);
        taskWorker.removeAllItems();
        taskWorker.addItem("Worker...");
        taskPriority.setValue(0);
        taskSD.setValue(1);
        taskComments.setText("Comments...");
        
        setEnabled(true);
        setVisible(true);
    }//GEN-LAST:event_createTaskComponentHidden

    private void createTaskComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_createTaskComponentShown
        setEnabled(false);
        try {
            Project [] projects = func.getProjects(form.getLogin());
            for (Project p: projects) {
                taskProject.addItem(p.getProjectName());
            }
            Task [] tasks = func.getTasks(null, null, false);
            for (Task t: tasks) {
                taskParent.addItem(t.getTaskName());
            }
            taskParent.addItem("<Without parent>");
            Worker [] workers = func.getWorkers();
            for (Worker w: workers) {
                taskWorker.addItem(w.getLogin());
            }
            taskWorker.addItem(".:Me:.");
            taskWorker.addItem("<Free>");
            taskStart.setDate(new Date());
            SpinnerNumberModel modelP = new SpinnerNumberModel(0, 0, 1000, 1);
            SpinnerNumberModel model = new SpinnerNumberModel(new Float(1.00), new Float(0.01), new Float(365), new Float(0.01));
            taskSD.setModel(model);
            taskPriority.setModel(modelP);
            JSpinner.NumberEditor editor = (JSpinner.NumberEditor)taskSD.getEditor();  
            DecimalFormat format = editor.getFormat();
            format.setMinimumFractionDigits(2);
        } catch (IOException | JSONException | ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_createTaskComponentShown

    private void createTMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_createTMouseClicked
        if (taskName.getText().isEmpty() | (taskParent.getSelectedIndex() <= 0 & taskParent.isEnabled()) | 
                taskProject.getSelectedIndex() <= 0 | (taskStatus.getSelectedIndex() <= 0 & taskStatus.isEnabled()) | 
                (taskWorker.getSelectedIndex() <= 0 & taskWorker.isEnabled()) | taskType.getSelectedIndex() <= 0) {
           JOptionPane.showMessageDialog(null, "Wrong options", "Create task", JOptionPane.ERROR_MESSAGE);
           return;
        }
        String nameS = taskName.getText().trim();
        String projectS = taskProject.getSelectedItem().toString();
        String parentS = taskParent.getSelectedItem().toString();
        if (taskParent.getSelectedIndex() == taskParent.getItemCount() - 1 | !taskParent.isEnabled()) {
            parentS = null;
        }
        int statuS = taskStatus.getSelectedIndex() - 1;
        if (statuS == -1) {
            statuS = 1;
        }
        String workerS = taskWorker.getSelectedItem().toString();
        if (taskWorker.getSelectedIndex() == taskWorker.getItemCount() - 2) {
            workerS = form.getLogin();
        }
        if (taskWorker.getSelectedIndex() == taskWorker.getItemCount() - 1 | !taskWorker.isEnabled()) {
            workerS = null;
        }
        Date startS = taskStart.getDate();
        int Priority = (int) taskPriority.getValue();
        float SD = (float)taskSD.getValue();
        String typeS = taskType.getSelectedItem().toString();
        String commentsS = taskComments.getText();
        try {
            if (func.createTask(workerS, parentS, projectS, nameS, Priority, statuS, startS, SD, typeS, commentsS)) {
                update();
                createTask.hide();
            } else {
                return;
            }
        } catch (IOException | JSONException | ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_createTMouseClicked

    private void taskTypeItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_taskTypeItemStateChanged
        if (taskType.getSelectedItem().equals("Fixed")) {
            taskParent.setEnabled(false);
            taskStatus.setEnabled(false);
            taskWorker.setEnabled(true);
            taskWorker.removeItem("<Free>");
        } else {
            taskParent.setEnabled(true);
            taskStatus.setEnabled(true);
            if (!taskWorker.getItemAt(taskWorker.getItemCount() - 1).equals("<Free>")) {
                taskWorker.addItem("<Free>");
            }
        }
        if (taskType.getSelectedItem().equals("Dynamic")) {
            taskWorker.setEnabled(false);
            taskStatus.setEnabled(false);
            taskSD.setEnabled(false);
            taskPriority.setEnabled(false);
        } else {
            taskWorker.setEnabled(true);
            taskStatus.setEnabled(true);
            taskSD.setEnabled(true);
            taskPriority.setEnabled(true);
        }
    }//GEN-LAST:event_taskTypeItemStateChanged

    private void createProjectComponentHidden(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_createProjectComponentHidden
        projectName.setText(null);
        projectPriority.setValue(0);
        projectComments.setText("Comments...");
        
        setEnabled(true);
        setVisible(true);
    }//GEN-LAST:event_createProjectComponentHidden

    private void createProjectComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_createProjectComponentShown
        setEnabled(false);
        projectDL.setDate(new Date());
        SpinnerNumberModel model = new SpinnerNumberModel(0, 0, 1000, 1);
        projectPriority.setModel(model);
    }//GEN-LAST:event_createProjectComponentShown

    private void createPMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_createPMouseClicked
        if (!projectName.getText().isEmpty()) {
            try {
                if (func.createProject(projectName.getText().trim(), projectDL.getDate(), (int)projectPriority.getValue(), projectComments.getText())) {
                    createProject.hide();
                    update();
                } else {
                    return;
                }
            } catch (IOException | JSONException | ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
           JOptionPane.showMessageDialog(null, "Wrong options", "Create propject", JOptionPane.ERROR_MESSAGE);
           return;
        }
    }//GEN-LAST:event_createPMouseClicked

    private void changePassMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_changePassMouseClicked
        JPasswordField pass = new JPasswordField();
        JPasswordField pass2 = new JPasswordField();
        JPasswordField pass3 = new JPasswordField();
        Object[] obj = {"Please enter password:\n\n", "Your password:", pass, "New password:",pass2, "Retry new password:",pass3};
        Object stringArray[] = {"OK","Cancel"};
        boolean b = false;
        while (!b) {
            if (JOptionPane.showOptionDialog(null, obj, "Change password", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, stringArray, obj) == JOptionPane.YES_OPTION) {
                if (!pass.getText().isEmpty() && !pass2.getText().isEmpty() && !pass3.getText().isEmpty()) {
                    if (pass2.getText().equals(pass3.getText())) {
                        b = true;
                    } else {
                        JOptionPane.showMessageDialog(null, "Passwords are different", "Change password", JOptionPane.ERROR_MESSAGE);
                        pass2.setText(null);
                        pass3.setText(null);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Wrong password", "Change password", JOptionPane.ERROR_MESSAGE);
                    pass.setText(null);
                    pass2.setText(null);
                    pass3.setText(null);
                }
            } else {
                return;
            }
        }
        try {
            func.changePassword(form.getLogin(), pass.getText(), pass2.getText(), pass3.getText());
        } catch ( IOException | JSONException ex) {
            Logger.getLogger(Signin.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_changePassMouseClicked

    private void logoutMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_logoutMouseClicked
        table.removeAll();
        if (JOptionPane.showConfirmDialog(null, "Are you shure want to log out?", "Logout", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            hide();
            form.setEnabled(true);
        } else {
            return;
        }
    }//GEN-LAST:event_logoutMouseClicked

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        if (JOptionPane.showConfirmDialog(null, "Are you shure want to exit?", "Exit", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            removeTrayIcon();
            System.exit(0);
        } else {
            return;
        }
    }//GEN-LAST:event_formWindowClosing

    private void createMMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_createMMouseClicked
        if (milestoneWorker.getSelectedIndex() <= 0 | milestoneName.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Wrong options", "Create milestone", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String [] tasks = new String[names.size()];
        for (int i=0; i<table.getColumnCount(); i++) {
            if (table.getColumnName(i).equals("Task name")) {
                for (int j=0; j<names.size(); j++) {
                    tasks[j] = table.getValueAt((int)names.get(j), i).toString();
                }
            }
        }
        try {
            func.createMilestone(milestoneWorker.getSelectedItem().toString(), mName.getText().trim(), new Date(), tasks);
            update();
            createMilestone.hide();
        } catch (IOException | JSONException | ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_createMMouseClicked

    private void createMilestoneComponentHidden(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_createMilestoneComponentHidden
        milestoneName.setText(null);
        milestoneWorker.removeAllItems();
        milestoneWorker.addItem("Worker...");
        
        setEnabled(true);
        setVisible(true);
    }//GEN-LAST:event_createMilestoneComponentHidden

    private void createMilestoneComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_createMilestoneComponentShown
        setEnabled(false);

        Worker [] workers = null;
        try {
            workers = func.getWorkers();
        } catch (IOException | JSONException | ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        for (Worker w: workers) {
            milestoneWorker.addItem(w.getLogin());
        }
        milestoneWorker.addItem(".:Me:.");
    }//GEN-LAST:event_createMilestoneComponentShown

    private void changePMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_changePMouseClicked
        if (parentL.getSelectedIndex() <= 0) {
            JOptionPane.showMessageDialog(null, "Wrong options", "Change parent", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String projectN = null;
        for (int i=0; i<table.getColumnCount(); i++) {
            if (table.getColumnName(i).equals("Project name")) {
                projectN = (String) table.getValueAt(table.getSelectedRow(), i);
                break;
            }
        }
        try {        
            func.changeParent(parentL.getSelectedItem().toString(), projectN);
            update();
            changeParent.hide();
        } catch (IOException | JSONException | ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_changePMouseClicked

    private void changeParentComponentHidden(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_changeParentComponentHidden
        parentL.removeAllItems();
        parentL.addItem("Task...");
        
        setEnabled(true);
        setVisible(true);
    }//GEN-LAST:event_changeParentComponentHidden

    private void changeParentComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_changeParentComponentShown
        setEnabled(false);
        String projectN = null;
        for (int i=0; i<table.getColumnCount(); i++) {
            if (table.getColumnName(i).equals("Project name")) {
                projectN = (String) table.getValueAt(table.getSelectedRow(), i);
                break;
            }
        }
        Task [] tasks = null;
        try {
            tasks = func.getTasks(null, projectN, false);
        } catch (IOException | JSONException | ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        for (Task t: tasks) {
            parentL.addItem(t.getTaskName());
        }
    }//GEN-LAST:event_changeParentComponentShown

    private void setLoadMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_setLoadMouseClicked
        String userN = null;
        for (int i=0; i<table.getColumnCount(); i++) {
            if (table.getColumnName(i).equals("Login")) {
                userN = (String) table.getValueAt(table.getSelectedRow(), i);
                break;
            }
        }
        try {
            func.setLoadFactor(userN, (Float)setLF.getValue());
            update();
            setLoadFactor.hide();
        } catch (IOException | JSONException | ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_setLoadMouseClicked

    private void setLoadFactorComponentHidden(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_setLoadFactorComponentHidden
        setLF.setValue(0.001);
        
        setEnabled(true);
        setVisible(true);
    }//GEN-LAST:event_setLoadFactorComponentHidden

    private void setLoadFactorComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_setLoadFactorComponentShown
        setEnabled(false);
        SpinnerNumberModel model = new SpinnerNumberModel(new Float(0.001), new Float(0.001), new Float(1), new Float(0.001));
        setLF.setModel(model);
    }//GEN-LAST:event_setLoadFactorComponentShown

    private void workersMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_workersMouseClicked
        try {
            Worker [] workers = func.getWorkers();
            workersToTable(workers);
            Vector v = new Vector(Arrays.asList(3));
            func.setVector(v);
        } catch (IOException | JSONException | ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_workersMouseClicked

    private void formWindowStateChanged(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowStateChanged
        if (this.getState() == JFrame.ICONIFIED) {
            removeFromTaskBar(getWindowHandle(this));    
        } else {
            addToTaskBar(getWindowHandle(this));
        }
    }//GEN-LAST:event_formWindowStateChanged

    private void changePrMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_changePrMouseClicked
        String task = (String) table.getValueAt(table.getSelectedRow(), table.getColumnModel().getColumnIndex("Task name"));
        try {
            func.changePriority(task, (int)priorityC.getValue());
            update();
            changePriority.hide();
        } catch (IOException | JSONException | ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_changePrMouseClicked

    private void changePriorityComponentHidden(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_changePriorityComponentHidden
        priorityC.setValue(1);
        
        setEnabled(true);
        setVisible(true);
    }//GEN-LAST:event_changePriorityComponentHidden

    private void changePriorityComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_changePriorityComponentShown
        setEnabled(false);
        SpinnerNumberModel model = new SpinnerNumberModel(1, 0, 100, 1);
        priorityC.setModel(model);
    }//GEN-LAST:event_changePriorityComponentShown

    private void setTrayIcon() {
        if (SystemTray.isSupported()) {
            tray = SystemTray.getSystemTray();
            Image image = Toolkit.getDefaultToolkit().getImage("images/tray.png");
            ActionListener exitListener = new ActionListener() {
 
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (JOptionPane.showConfirmDialog(null, "Are you shure want to exit?", "Exit", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                        System.exit(0);
                    } else {
                        return;
                    }
                }
            };
 
            ActionListener showListener = new ActionListener() {
 
                @Override
                public void actionPerformed(ActionEvent e) {
                    showF();
                }
            }; 
            
            ActionListener logoutListener = new ActionListener() {
 
                @Override
                public void actionPerformed(ActionEvent e) {
                    table.removeAll();
                    if (JOptionPane.showConfirmDialog(null, "Are you shure want to log out?", "Logout", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                        hide();
                        form.setEnabled(true);
                    } else {
                        return;
                    }
                }
            }; 

            final JPopupMenu popup = new JPopupMenu();
            JMenuItem showmenuItem = new JMenuItem("Show");
            JMenuItem logoutItem = new JMenuItem("Logout");
            JMenuItem defaultItem = new JMenuItem("Exit");
            showmenuItem.addActionListener(showListener);
            logoutItem.addActionListener(logoutListener);
            defaultItem.addActionListener(exitListener);
            popup.add(showmenuItem);
            popup.addSeparator();
            popup.add(logoutItem);
            popup.addSeparator();
            popup.add(defaultItem);
 
            trayIcon = new TrayIcon(image, "ProjectL");
 
            ActionListener actionListener = new ActionListener() {
 
                @Override
                public void actionPerformed(ActionEvent e) {
                    showF();
                }
            };
 
            trayIcon.setImageAutoSize(true);
            trayIcon.addActionListener(actionListener);
            trayIcon.addMouseListener(new MouseAdapter() {
 
                @Override
                public void mouseReleased(MouseEvent e) {
                    if (e.isPopupTrigger()) {
                        popup.setLocation(e.getX(), e.getY());
                        popup.setInvoker(popup);
                        popup.setVisible(true);
                    }
                }
            });
 
            try {
                tray.add(trayIcon);
            } catch (AWTException e) {
                System.err.println("TrayIcon could not be added.");
            }
 
        } else {
            //  System Tray is not supported
        }
    }

    private void removeTrayIcon() {
        if (SystemTray.isSupported()) {
            tray = SystemTray.getSystemTray();
            tray.remove(trayIcon);
        }
    }
    
    private void projectsToTable(Project[] projs) {
        Vector res = new Vector();
        if (!projectMap.isEmpty()) {
            projectMap.clear();
        }
        if (null == projs | projs.length == 0) {
            return;
        }
        for (Project p : projs) {
            res.add(p.getVector());
            projectMap.put(p.getProjectName(), p);
        }
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setDataVector(res, projectCols);
        table.getColumnModel().getColumn(0).setResizable(true);
        for (int i=0; i<projectCols.size(); i++) {
            checkFilter.addItem(projectCols.get(i));
        }
        setMainBts(false, false, false);
        subLabel.setText("Projects:");
        subLabel.setVisible(false);
        subtable.setVisible(false);
        subscroll.hide();
    }
    
    private void tasksToTable(Task[] tasks) {
        Vector res = new Vector();
        if (!taskMap.isEmpty()) {
            taskMap.clear();
        }
        if (null == tasks | tasks.length == 0) {
            return;
        }
        for (Task t : tasks) {
            res.add(t.getVector());
            taskMap.put(t.getTaskName(), t);
        }
        Model model = (Model) table.getModel();
        model.setDataVector(res, taskCols);
        for (int i=1; i<taskCols.size(); i++) {
            checkFilter.addItem(taskCols.get(i));
        }
        mins = new HashMap();
        boolean check = true;
        for (int i=0; i<tasks.length; i++) {
            if (!tasks[i].getType().equals("Static") | !tasks[i].getUserName().equals("NONE")) {
                model.setCellEditable(i, 0, false);
                check = false;
            }
            if (tasks[i].getType().equals("Static") & tasks[i].getCompleteness() < 100 & tasks[i].getUserName().equals(form.getLogin())) {
                mins.put(i, tasks[i].getCompleteness());
            } else {
                model.setCellEditable(i, 4, false);
            }
        }
        table.setModel(model);
        table.getColumnModel().getColumn(4).setCellEditor(new SpinnerEditor(mins));
        table.getColumnModel().getColumn(0).setHeaderRenderer(new HeaderRenderer(table.getTableHeader(), check));
        subLabel.setText("Periods:");
        subLabel.setVisible(true);
        subtable.setVisible(true);
        subscroll.show();
        setMainBts(true, true, false);
        table.getColumnModel().getColumn(0).setPreferredWidth(30);
        if (!form.isSuper()) {
            table.getColumnModel().removeColumn(table.getColumnModel().getColumn(0));
        }
        Model model2 = new Model(periodCols, 0);
        subtable.setModel(model2);
   }
    
    private void milestonesToTable(Milestone[] miles) {
        Vector res = new Vector();
        if (!milestoneMap.isEmpty()) {
            milestoneMap.clear();
        }
        if (null == miles | miles.length == 0) {
            return;
        }
        for (Milestone m : miles) {
            res.add(m.getVector());
            milestoneMap.put(m.getName(), m);
        }
        Model model = (Model) table.getModel();
        model.setDataVector(res, milestoneCols);
        table.getColumnModel().getColumn(0).setResizable(true);
        for (int i=0; i<milestoneCols.size(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(new TableCellRenderer());
            checkFilter.addItem(milestoneCols.get(i));
        }
        subLabel.setText("Tasks:");
        subLabel.setVisible(true);
        subtable.setVisible(true);
        subscroll.show();
        setMainBts(false, false, false);
        Model model2 = new Model(milestoneTaskCols, 0);
        subtable.setModel(model2);
    }
    
    private void workersToTable(Worker[] workers) {
        Vector res = new Vector();
        if (!workerMap.isEmpty()) {
            workerMap.clear();
        }
        if (null == workers | workers.length == 0) {
            return;
        }
        for (Worker w : workers) {
            res.add(w.getVector());
            workerMap.put(w.getLogin(), w);
        }
        Model model = (Model) table.getModel();
        model.setDataVector(res, workerCols);
        table.getColumnModel().getColumn(0).setResizable(true);
        for (int i=0; i<workerCols.size(); i++) {
            checkFilter.addItem(workerCols.get(i));
        }
        setMainBts(false, true, false);
        subLabel.setText("Workers:");
        subLabel.setVisible(false);
        subtable.setVisible(false);
        subscroll.hide();
    }
    
    private void periodsToTable(Period[] periods) {
        Model model1 = new Model(periodCols, 0);
        subtable.setModel(model1);
        if (null == periods | periods.length == 0) {
            return;
        }
        Vector res = new Vector();
        for (Period p : periods) {
            res.add(p.getVector());
        }
        DefaultTableModel model = (DefaultTableModel) subtable.getModel();
        model.setDataVector(res, periodCols);
    }
    
    private void miletasksToTable(MilestoneTask[] mts) {
        Model model1 = new Model(milestoneTaskCols, 0);
        subtable.setModel(model1);
        if (null == mts | mts.length == 0) {
            return;
        }
        Vector res = new Vector();
        for (MilestoneTask mt : mts) {
            res.add(mt.getVector());
        }
        Model model = (Model) subtable.getModel();
        model.setDataVector(res, milestoneTaskCols);
        for (int i=0; i<milestoneTaskCols.size(); i++) {
            subtable.getColumnModel().getColumn(i).setCellRenderer(new TableCellRenderer());
        }
    }
    
    public void Visible(boolean isSuper) {
        this.setVisible(true);
        pCreate.setVisible(isSuper);
        tCreate.setVisible(isSuper);
        mCreate.setVisible(isSuper);
        func.freeVector();
        setMainBts(false, false, true);
        requestFocus();
    }
    
    private void showF() {
        this.setState(JFrame.NORMAL);
        this.setVisible(true);
    }
    
    public void showSignup() {
        signup.setLocationRelativeTo(null);
        signup.setSize(400, 360);
        signup.show();
    }

    private void defaultVs() {
        login.setText(null);
        password.setText(null);
        passwordC.setText(null);
        mail.setText(null);
        fName.setText(null);
        lName.setText(null);
        mName.setText(null);
        check.setSelected(false);
    }
    
    private void setMainBts(boolean set, boolean yes, boolean start) {
        parent.setEnabled(form.isSuper() & set & yes & table.getSelectedRow() != -1);
        worker.setEnabled(form.isSuper() & set & yes & table.getSelectedRow() != -1);
        manager.setEnabled(form.isSuper() & !set & yes & table.getSelectedRow() != -1);
        load.setEnabled(form.isSuper() & !set & yes & table.getSelectedRow() != -1);
        priority.setEnabled(form.isSuper() & set & yes & table.getSelectedRow() != -1);
        
        subcom.setVisible(set & table.getSelectedRow() != -1);
        subcomt.setVisible(set & table.getSelectedRow() != -1);
        subLabel2.setVisible(set & table.getSelectedRow() != -1);
        separator.setVisible(set & table.getSelectedRow() != -1);
        gant.setEnabled(set & taskMap.size() > 0);
        
        scroll.setVisible(!start);
        table.setVisible(!start);
        subscroll.setVisible(!start);
        subtable.setVisible(!start);
        subLabel.setVisible(!start);
        add.setVisible(form.isSuper() & !set & yes & table.getSelectedRow() != -1 & !start);
        
        parent.setVisible(form.isSuper());
        worker.setVisible(form.isSuper());
        manager.setVisible(form.isSuper());
        load.setVisible(form.isSuper());
        
        changes.clear();
    }
    
    private void setGetTasksButtons() {
        getTasks.pack();
        getTasks.setLocationRelativeTo(null);
        
        tasksG.add(notFree);
        tasksG.add(free);
        tasksG.add(allT);
        
        projectsG.add(projectT);
        projectsG.add(allP);
        
        notFree.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                loginT.setEnabled(notFree.isSelected());
            }
        });
        
        projectT.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                project.setEnabled(projectT.isSelected());
            }
        });        
    }
    
    private void setGetMilestonesButtons() {
        getMilestones.pack();
        getMilestones.setLocationRelativeTo(null);
        
        milestoneG.add(loginMB);
        milestoneG.add(loginMBAll);
        
        loginMB.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                loginM.setEnabled(loginMB.isSelected());
            }
        });        
    }
    
    private void setCreateTaskButtons() {
        createTask.pack();        
        createTask.setLocationRelativeTo(null);
    }
    
    private void setCreateProjectButtons() {
        createProject.pack();        
        createProject.setLocationRelativeTo(null);
    }

    private void setCreateMilestoneButtons() {
        createMilestone.pack();
        createMilestone.setLocationRelativeTo(null);
    }
    
    private void setChangeParentButtons() {
        changeParent.pack();
        changeParent.setLocationRelativeTo(null);
    }
    
    private void setChangeLFButtons() {
        setLoadFactor.pack();
        setLoadFactor.setLocationRelativeTo(null);
    }
    
    private void setChangePriorityBts() {
        changePriority.pack();
        changePriority.setLocationRelativeTo(null);
    }

    private void setGantChart() {
        gantChart.setSize(new Dimension(978, 688));
        gantChart.setLocationRelativeTo(null);
    }
    
    private void changeCompleteness() {
        JPanel [] panes = new JPanel[changes.size()];
        JTextArea [] areas = new JTextArea[changes.size()];
        Vector vec = new Vector();
        Set<Map.Entry<Object, Vector>> entries = changes.entrySet();
        for (Map.Entry<Object, Vector> entry:entries) {
            vec.add((Vector)entry.getValue());
        }
        for (int i=0; i<vec.size(); i++) {
            try {
                func.updateCompleteness((Float)(((Vector)vec.get(i)).get(3)), ((Task)((Vector)vec.get(i)).get(1)), (i == vec.size() - 1) ? null : ((Task)(((Vector)(vec.get(i + 1))).get(1))).getTaskName(), (((Vector)vec.get(i)).get(2)).toString());
                update();
            } catch (IOException | JSONException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private void update() {
        Vector v = func.getVector();
        if (v.isEmpty()) {
            return;
        }
        switch((int)v.get(0)){
            case 0: {
                try {
                    Project[] ps = func.getProjects(form.getLogin());
                    projectsToTable(ps);
                } catch (IOException | JSONException | ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
            }
            case 1: {
                try {
                    Task [] tasks = func.getTasks((v.get(1) == null) ? null : v.get(1).toString(), (v.get(2) == null) ? null : v.get(2).toString(), (boolean)v.get(3));
                    tasksToTable(tasks);
                } catch (IOException | JSONException | ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
            }
            case 2: {
                try {
                    Milestone [] miles = func.getMilestones((v.get(1) == null) ? null : v.get(1).toString());
                    milestonesToTable(miles);
                } catch (IOException | JSONException | ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
            }
            case 3: {
                try {
                    Worker [] workers = func.getWorkers();
                    workersToTable(workers);
                } catch (IOException | JSONException | ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
            }
        }
        try {
            ID = func.getLastID();
        } catch (IOException | JSONException | ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private RowFilter newFilter() {
        if (table.getRowCount() == 0) {
            return null;
        }
        return RowFilter.regexFilter(filter.getText(), table.getColumnModel().getColumnIndex(checkFilter.getSelectedItem()));
    }
    
    public static native void addToTaskBar(long WindowHandle);

    public static native void removeFromTaskBar(long WindowHandle);

    public static long getWindowHandle(java.awt.Frame frame) {
        return (Long)invokeMethod(invokeMethod(frame, "getPeer"), "getHWnd");
    }

    protected static Object invokeMethod(Object o, String methodName) {
        Class c = o.getClass();
        for (java.lang.reflect.Method m : c.getMethods()) {
            if (m.getName().equals(methodName)) {
                try {
                    return m.invoke(o);
                } catch (IllegalAccessException | IllegalArgumentException | java.lang.reflect.InvocationTargetException Ex) {
                    Ex.printStackTrace();
                    break;
                }
            }
        }
        return null;
    }
    
    private void tasksToChart(Map map, Vector v, Element script, String name) throws IOException {
        if (v.size() > 0) {
            for (int i=0; i<v.size(); i++) {
                Vector vr = (Vector) map.get(v.get(i));
                tasksToChart(map, (Vector) vr.get(2), script, vr.get(0).toString());
                script.append(name + ".addChildTask(" + vr.get(0) +");");
            }
        }
    }
    
    private void setHTML() {
        setGantChart();
        File f = new File("Gant/index.html");
        Project [] projects;
        Map<String, Project> proj = new HashMap();
        Map<String, String> ps = new HashMap();
        try {
            int i = 0;
            projects = func.getProjects(form.getLogin());
            for (Project p: projects) {
                proj.put(projects[i].getProjectName(), projects[i]);
                i++;
            }
        } catch (IOException | JSONException | ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        Document doc = null;
        try {
            doc = Jsoup.parse(f, "UTF-8", "http://example.com/");
            Element script = doc.select("script").get(2);
            script.append("function createChartControl(htmlDiv1) {");
            int i =0;
            Map result = new HashMap();
            result.putAll(taskMap);
            Map ids = new HashMap();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy,MM,dd");
            Set<Map.Entry<Object, Task>> tasks = taskMap.entrySet();
            for (Map.Entry<Object, Task> task:tasks) {
                Task t = task.getValue();
                t.getProjectName();
                if (proj.containsKey(t.getProjectName())) {
                    script.append("var project" + i + " = new GanttProjectInfo(" + i + ",'" + t.getProjectName() + "', new Date(" + dateFormat.format(proj.get(t.getProjectName()).getStart()) + "));");
                    proj.remove(t.getProjectName());
                    ps.put(t.getProjectName(), "project" + i);
                }
                script.append("var task" + i + " = new GanttTaskInfo(" + i + ",'" + t.getTaskName() + "', new Date(" + dateFormat.format(t.getStart()) + "), " + t.getDuration() * 8 + ", " + t.getCompleteness() + ");");
                if (ids.containsKey(t.getTaskName())) {
                    Vector v = (Vector) ids.get(t.getTaskName());
                    ids.remove(t.getTaskName());
                    ids.put(t.getTaskName(),new Vector(Arrays.asList("task" + i, ps.get(t.getProjectName()), v.get(2))));
                } else {
                    ids.put(t.getTaskName(), new Vector(Arrays.asList("task" + i, ps.get(t.getProjectName()), new Vector())));
                }
                if (result.containsKey(t.getParentTask())) {
                    if (ids.containsKey(t.getParentTask())) {
                        Vector v = (Vector) ids.get(t.getParentTask());
                        ((Vector) v.get(2)).add(t.getTaskName());
                        ids.remove(t.getParentTask());
                        ids.put(t.getParentTask(), v);
                    } else {
                        Vector v = new Vector();
                        v.add(t.getTaskName());
                        ids.put(t.getParentTask(), new Vector(Arrays.asList("", "", v)));
                    }
                    result.remove(t.getTaskName());
                }
                i++;
            }
            Set<Map.Entry<String, String>> resultT = result.entrySet();
            for (Map.Entry<String, String> r:resultT) {
                Vector rID = (Vector) ids.get(r.getKey());
                tasksToChart(ids, (Vector)rID.get(2), script, rID.get(0).toString());
                script.append(rID.get(1) + ".addTask(" + rID.get(0) +");");
            }
            script.append("var ganttChartControl = new GanttChart();");
            script.append("ganttChartControl.setImagePath('codebase/imgs/');");
            script.append("ganttChartControl.setEditable(false);");
            Set<Map.Entry<String, String>> pnames = ps.entrySet();
            for (Map.Entry<String, String> p:pnames) {
                script.append("ganttChartControl.addProject(" + p.getValue() + ");");
            }
            script.append("ganttChartControl.create(htmlDiv1);");
            script.append("}");
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        File out = new File("Gant/out.html");
        BufferedWriter htmlWriter;
        try {
            try {
                htmlWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(out), "windows-1251"));
                try {
                    htmlWriter.write(doc.toString());
                    htmlWriter.flush();
                } catch (IOException ex) {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        webBrowser.navigate("file:///" + out.getAbsolutePath());
    }
    
    private void intiButtons() {
        pGet = new JButton();
        pCreate = new JButton();
        tGet = new JButton();
        tCreate = new JButton();
        mGet = new JButton();
        mCreate = new JButton();
        
        projectMap = new HashMap();
        taskMap = new HashMap();
        milestoneMap = new HashMap();
        workerMap = new HashMap();
        
        names = new Vector();
        changes = new HashMap();
        
        webBrowser = new JWebBrowser();
        webBrowser.setMenuBarVisible(false);
        webBrowser.setBarsVisible(false);
        webBrowser.setSize(new Dimension(980, 650));
        gantChart.add(webBrowser);
    
        Model m = (Model) table.getModel();
        m.addTableModelListener(new TableModelListener() {

            @Override
            public void tableChanged(TableModelEvent e) {
                sorter = new TableRowSorter<Model>((Model)table.getModel());
                if (table.getSelectedRow() == -1 | table.getSelectedColumn() == -1) {
                    return;
                } else {
                    if (table.getColumnName(table.getSelectedColumn()).toString().equals("")) {
                        int selected = 0;
                        boolean check = true;
                        names.clear();
                        for (int i=0; i<table.getRowCount(); i++) {
                            check = (boolean) table.getValueAt(i, table.getSelectedColumn());
                            if (check) {
                                names.add(i);
                                selected++;
                            }
                        }
                        if (selected == 0) {
                            mCreate.setEnabled(false);
                        } else {
                            mCreate.setEnabled(true);
                        }
                    }
                    if (table.getColumnName(table.getSelectedColumn()).toString().equals("Completeness")) {
                        if ((Float)table.getValueAt(table.getSelectedRow(), table.getSelectedColumn()) > (Float)mins.get(table.getSelectedRow())) {
                            String name = null;
                            for (int i=0; i<table.getColumnCount(); i++) {
                                if (table.getColumnName(i).equals("Task name")) {
                                    name = (String) table.getValueAt(table.getSelectedRow(), i);
                                    break;
                                }
                            }
                            changes.put(table.getSelectedRow(), new Vector(Arrays.asList(table.getSelectedRow(), taskMap.get(name), "", table.getValueAt(table.getSelectedRow(), table.getSelectedColumn()))));
                        } else {
                            changes.remove(table.getSelectedRow());
                        }
                        if (changes.size() == 0) {
                            complete.setEnabled(false);
                        } else {
                            complete.setEnabled(true);
                        }
                    }
                }
            }
        });
        table.setModel(m);
        
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (table.getSelectedRow() != -1) {
                    if (subLabel.getText().toString().equals("Periods:")) {
                        for (int i=0; i<table.getColumnCount(); i++) {
                            if (table.getColumnName(i).equals("Task name")) {
                                Task t = (Task) taskMap.get(table.getValueAt(table.getSelectedRow(), i));
                                periodsToTable(t.getPeriods());
                                if (form.isSuper()) {
                                    if (t.getParentTask().equals("NONE") & !t.getType().equals("Fixed")) {
                                        parent.setEnabled(true);
                                    } else {
                                        parent.setEnabled(false);
                                    }
                                    if (t.getType().equals("Satic") & t.getUserName().equals("NONE")) {
                                        worker.setEnabled(true);
                                    } else {
                                        worker.setEnabled(false);
                                    }
                                    if (t.getType().equals("Dynamic")) {
                                        priority.setEnabled(false);
                                    } else {
                                        priority.setEnabled(true);
                                    }
                                }
                                if (changes.get(table.getSelectedRow()) != null) {
                                    subcom.setVisible(true);
                                    subcomt.setVisible(true);
                                    subLabel2.setVisible(true);
                                    separator.setVisible(true);
                                    add.setVisible(true);
                                    subcomt.setText(((Vector)changes.get(table.getSelectedRow())).get(2).toString());
                                    subcomt.requestFocus();
                                } else {
                                    subcom.hide();
                                    subcomt.setVisible(false);
                                    subLabel2.setVisible(false);
                                    separator.setVisible(false);
                                    add.setVisible(false);
                                }
                            }
                        }
                    }
                    if (subLabel.getText().toString().equals("Tasks:")) {
                        for (int i=0; i<table.getColumnCount(); i++) {
                            if (table.getColumnName(i).equals("Milestone name")) {
                                Milestone m = (Milestone) milestoneMap.get(table.getValueAt(table.getSelectedRow(), i));
                                miletasksToTable(m.getTasks());
                            }
                        }
                    }
                    if (subLabel.getText().equals("Workers:")) {
                        load.setEnabled(true);
                        manager.setEnabled(true);
                    }
                }
            }
        });
        
        filter.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                TableRowSorter<Model> sorter = (TableRowSorter<Model>) table.getRowSorter();
                sorter.setRowFilter(newFilter());
                table.setRowSorter(sorter);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                TableRowSorter<Model> sorter = (TableRowSorter<Model>) table.getRowSorter();
                sorter.setRowFilter(newFilter());
                table.setRowSorter(sorter);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                TableRowSorter<Model> sorter = (TableRowSorter<Model>) table.getRowSorter();
                sorter.setRowFilter(newFilter());
                table.setRowSorter(sorter);
            }
        });
        
        checkFilter.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                if (checkFilter.getSelectedItem().equals("<None filter>")) {
                    filter.setEnabled(false);
                    filter.setText(null);
                    return;
                } else {
                    filter.setEnabled(true);
                }
            }
        });
        
        List projectCs = Arrays.asList("Project name", "Start", "Deadline", "Completeness", "Priority", "Comments", "You participate");
        projectCols = new Vector(projectCs);

        List taskCs = Arrays.asList("", "Task name", "Project name", "Worker", "Completeness", "Priority", "Start", "Scheduled Duration", "Duration", "Type", "Parent task", "Status", "Comments");
        taskCols = new Vector(taskCs);
        
        List milestoneCs = Arrays.asList("Milestone name", "Worker", "Deadline", "Status");
        milestoneCols = new Vector(milestoneCs);

        List workerCs = Arrays.asList("Login", "First name", "Last name", "Middle name", "Load factor", "Mail");
        workerCols = new Vector(workerCs);

        List periodCs = Arrays.asList("Start period", "End period", "Completeness part", "Work time", "Comments");
        periodCols = new Vector(periodCs);

        List milestoneTaskCs = Arrays.asList("Task name", "Status");
        milestoneTaskCols = new Vector(milestoneTaskCs);

        mCreate.setEnabled(false);
        
        pGet.setIcon(new ImageIcon("images/getP.png"));
        pGet.setRolloverIcon(new ImageIcon("images/getP_D.png"));
        pGet.setSelectedIcon(new ImageIcon("images/getP_G.png"));

        pCreate.setIcon(new ImageIcon("images/createP.png"));
        pCreate.setRolloverIcon(new ImageIcon("images/createP_D.png"));
        pCreate.setSelectedIcon(new ImageIcon("images/createP_G.png"));

        tGet.setIcon(new ImageIcon("images/getT.png"));
        tGet.setRolloverIcon(new ImageIcon("images/getT_D.png"));
        tGet.setSelectedIcon(new ImageIcon("images/getT_G.png"));

        tCreate.setIcon(new ImageIcon("images/createT.png"));
        tCreate.setRolloverIcon(new ImageIcon("images/createT_D.png"));
        tCreate.setSelectedIcon(new ImageIcon("images/createT_G.png"));

        mGet.setIcon(new ImageIcon("images/getM.png"));
        mGet.setRolloverIcon(new ImageIcon("images/getM_D.png"));
        mGet.setSelectedIcon(new ImageIcon("images/getM_G.png"));

        mCreate.setIcon(new ImageIcon("images/createM.png"));
        mCreate.setRolloverIcon(new ImageIcon("images/createM_D.png"));
        mCreate.setSelectedIcon(new ImageIcon("images/createM_G.png"));

        complete.setIcon(new ImageIcon("images/complete.png"));
        parent.setIcon(new ImageIcon("images/parent.png"));
        gant.setIcon(new ImageIcon("images/gant.png"));
        workers.setIcon(new ImageIcon("images/workers.png"));
        worker.setIcon(new ImageIcon("images/worker.png"));
        load.setIcon(new ImageIcon("images/load.png"));
        manager.setIcon(new ImageIcon("images/manager.png"));
        update.setIcon(new ImageIcon("images/update.png"));
        changePass.setIcon(new ImageIcon("images/changepass.png"));
        logout.setIcon(new ImageIcon("images/logout.png"));
        priority.setIcon(new ImageIcon("images/priority.png"));
        
        complete.setRolloverIcon(new ImageIcon("images/complete_D.png"));
        parent.setRolloverIcon(new ImageIcon("images/parent_D.png"));
        gant.setRolloverIcon(new ImageIcon("images/gant_D.png"));
        workers.setRolloverIcon(new ImageIcon("images/workers_D.png"));
        worker.setRolloverIcon(new ImageIcon("images/worker_D.png"));
        load.setRolloverIcon(new ImageIcon("images/load_D.png"));
        manager.setRolloverIcon(new ImageIcon("images/manager_D.png"));
        update.setRolloverIcon(new ImageIcon("images/update_D.png"));
        changePass.setRolloverIcon(new ImageIcon("images/changepass_D.png"));
        logout.setRolloverIcon(new ImageIcon("images/logout_D.png"));
        priority.setRolloverIcon(new ImageIcon("images/priority_D.png"));
        

        complete.setSelectedIcon(new ImageIcon("images/complete_G.png"));
        parent.setSelectedIcon(new ImageIcon("images/parent_G.png"));
        gant.setSelectedIcon(new ImageIcon("images/gant_G.png"));
        workers.setSelectedIcon(new ImageIcon("images/workers_G.png"));
        worker.setSelectedIcon(new ImageIcon("images/worker_G.png"));
        load.setSelectedIcon(new ImageIcon("images/load_G.png"));
        manager.setSelectedIcon(new ImageIcon("images/manager_G.png"));
        update.setSelectedIcon(new ImageIcon("images/update_G.png"));
        changePass.setSelectedIcon(new ImageIcon("images/changepass_G.png"));
        logout.setSelectedIcon(new ImageIcon("images/logout_G.png"));
        priority.setSelectedIcon(new ImageIcon("images/priority_G.png"));

        complete.setToolTipText("Change tasks completeness in the table and push on it");
        parent.setToolTipText("Select task and push the button to set parent task");
        gant.setToolTipText("Show Gantt Chart");
        workers.setToolTipText("Show workers list");
        worker.setToolTipText("Select task and push the button to set worker");
        load.setToolTipText("Select user and push the button to change load factor of the selected user");
        manager.setToolTipText("Select user and push the button to set manager selected user");
        update.setToolTipText("Update data");
        changePass.setToolTipText("Change password");
        logout.setToolTipText("Log out");
        priority.setToolTipText("Select task and click the button to change task priority");
        
        logo.setIcon(new ImageIcon("images/logo.png"));
        
        pGet.setOpaque(false);
        pGet.setContentAreaFilled(false);
        pGet.setBorderPainted(false);
        pGet.setFocusPainted(false);
        pGet.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        pGet.setToolTipText("Push on it to get all projects");

        pCreate.setOpaque(false);
        pCreate.setContentAreaFilled(false);
        pCreate.setBorderPainted(false);
        pCreate.setFocusPainted(false);
        pCreate.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        pCreate.setToolTipText("Push on it to create new project");
        
        tGet.setOpaque(false);
        tGet.setContentAreaFilled(false);
        tGet.setBorderPainted(false);
        tGet.setFocusPainted(false);
        tGet.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        tGet.setToolTipText("Push on it to get tasks");

        tCreate.setOpaque(false);
        tCreate.setContentAreaFilled(false);
        tCreate.setBorderPainted(false);
        tCreate.setFocusPainted(false);
        tCreate.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        tCreate.setToolTipText("Push on it to create new task");

        mGet.setOpaque(false);
        mGet.setContentAreaFilled(false);
        mGet.setBorderPainted(false);
        mGet.setFocusPainted(false);
        mGet.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        mGet.setToolTipText("Push on it to get milestones");

        mCreate.setOpaque(false);
        mCreate.setContentAreaFilled(false);
        mCreate.setBorderPainted(false);
        mCreate.setFocusPainted(false);
        mCreate.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        mCreate.setToolTipText("Choose tasks and push on it to create milestone");

        projectPane.add(pGet);
        projectPane.add(pCreate);
        taskPane.add(tGet);
        taskPane.add(tCreate);
        milestonePane.add(mGet);
        milestonePane.add(mCreate);
        
        pGet.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Project [] projects = null;
                try {
                    projects = func.getProjects(form.getLogin());
                } catch (IOException | JSONException | ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                }
                Vector v = new Vector(Arrays.asList(0));
                func.setVector(v);
                projectsToTable(projects);
            }
        });

        tGet.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                getTasks.show();
            }
        });
        
        mGet.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                getMilestones.show();
            }
        });
        
        pCreate.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                
            }
        });

        pCreate.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                createProject.show();
            }
        });
        
        tCreate.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                createTask.show();
            }
        });        

        mCreate.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                createMilestone.show();
            }
        });      
        
        parent.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                changeParent.show();
            }
        });

        complete.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                changeCompleteness();
            }
        });

        load.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                setLoadFactor.show();
            }
        });
        
        manager.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (JOptionPane.showConfirmDialog(null, "Are you really want make this user Manager?", "Set manager", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    int i;
                    for (i=0; i<table.getColumnCount(); i++) {
                        if (table.getColumnName(i).equals("Login")) {
                            break;
                        }
                    }
                    try {
                        func.setManager(table.getValueAt(table.getSelectedRow(), i).toString());
                    } catch (IOException | JSONException | ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                        Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        
        add.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (subcomt.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Empty comments", "Error", JOptionPane.ERROR_MESSAGE);
                    subcomt.requestFocus();
                } else {
                    Vector v = (Vector) changes.get(table.getSelectedRow());
                    v.remove(2);
                    v.insertElementAt(subcomt.getText(), 2);
                    changes.remove(table.getSelectedRow());
                    changes.put(table.getSelectedRow(), v);
                    JOptionPane.showMessageDialog(null, "OK");
                }
            }
        });
        
        update.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                update();
            }
        });
        
        gant.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                setHTML();
                gantChart.show();
            }
        });
        
        priority.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                changePriority.show();
            }
        });
        popup.pack();
        popup.setVisible(true);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton add;
    private javax.swing.JRadioButton allP;
    private javax.swing.JRadioButton allT;
    private javax.swing.JToolBar barSetting;
    private javax.swing.JToolBar barTasks;
    private javax.swing.JToolBar barUsers;
    private javax.swing.JButton cancel;
    private javax.swing.JButton changeP;
    private javax.swing.JDialog changeParent;
    private javax.swing.JLabel changeParentL;
    private javax.swing.JButton changePass;
    private javax.swing.JButton changePr;
    private javax.swing.JDialog changePriority;
    private javax.swing.JLabel changePriorityL;
    private javax.swing.JCheckBox check;
    private javax.swing.JComboBox checkFilter;
    private javax.swing.JButton complete;
    private org.jdesktop.swingx.JXTaskPaneContainer container;
    private javax.swing.JButton createM;
    private javax.swing.JDialog createMilestone;
    private javax.swing.JLabel createMilestoneL;
    private javax.swing.JButton createP;
    private javax.swing.JDialog createProject;
    private javax.swing.JLabel createProjectL;
    private javax.swing.JButton createT;
    private javax.swing.JDialog createTask;
    private javax.swing.JLabel createTaskL;
    private javax.swing.JTextField fName;
    private javax.swing.JLabel fNameL;
    private javax.swing.JTextField filter;
    private javax.swing.JRadioButton free;
    private javax.swing.JButton gant;
    private javax.swing.JDialog gantChart;
    private javax.swing.JButton getM;
    private javax.swing.JDialog getMilestones;
    private javax.swing.JLabel getMilestonesL;
    private javax.swing.JButton getT;
    private javax.swing.JDialog getTasks;
    private javax.swing.JLabel getTasksL;
    private javax.swing.JTextField lName;
    private javax.swing.JLabel lNameL;
    private javax.swing.JButton load;
    private javax.swing.JLabel loadFL;
    private javax.swing.JTextField login;
    private javax.swing.JLabel loginL;
    private javax.swing.JTextField loginM;
    private javax.swing.JRadioButton loginMB;
    private javax.swing.JRadioButton loginMBAll;
    private javax.swing.JTextField loginT;
    private javax.swing.JLabel logo;
    private javax.swing.JButton logout;
    private javax.swing.JTextField mName;
    private javax.swing.JLabel mNameL;
    private javax.swing.JTextField mail;
    private javax.swing.JLabel mailL;
    private javax.swing.JButton manager;
    private javax.swing.ButtonGroup milestoneG;
    private javax.swing.JTextField milestoneName;
    private javax.swing.JLabel milestoneNameL;
    private org.jdesktop.swingx.JXTaskPane milestonePane;
    private javax.swing.JComboBox milestoneWorker;
    private javax.swing.JLabel milestoneWorkerL;
    private javax.swing.JRadioButton notFree;
    private javax.swing.JButton ok;
    private javax.swing.JButton parent;
    private javax.swing.JComboBox parentL;
    private javax.swing.JLabel parentLL;
    private javax.swing.JPasswordField password;
    private javax.swing.JPasswordField passwordC;
    private javax.swing.JLabel passwordL;
    private javax.swing.JLabel passwordLC;
    private javax.swing.JDialog popup;
    private javax.swing.JButton priority;
    private javax.swing.JSpinner priorityC;
    private javax.swing.JLabel priorityL;
    private javax.swing.JTextField project;
    private javax.swing.JTextArea projectComments;
    private com.toedter.calendar.JDateChooser projectDL;
    private javax.swing.JLabel projectDLL;
    private javax.swing.JTextField projectName;
    private javax.swing.JLabel projectNameL;
    private org.jdesktop.swingx.JXTaskPane projectPane;
    private javax.swing.JSpinner projectPriority;
    private javax.swing.JLabel projectPriorityL;
    private javax.swing.JScrollPane projectScroll;
    private javax.swing.JRadioButton projectT;
    private javax.swing.ButtonGroup projectsG;
    private javax.swing.JScrollPane scroll;
    private javax.swing.JSeparator separator;
    private javax.swing.JSpinner setLF;
    private javax.swing.JButton setLoad;
    private javax.swing.JDialog setLoadFactor;
    private javax.swing.JLabel setLoadFactorL;
    private javax.swing.JDialog signup;
    private javax.swing.JLabel subLabel;
    private javax.swing.JLabel subLabel2;
    private javax.swing.JScrollPane subcom;
    private javax.swing.JTextArea subcomt;
    private javax.swing.JScrollPane subscroll;
    private javax.swing.JTable subtable;
    private javax.swing.JTable table;
    private javax.swing.JTextArea taskComments;
    private javax.swing.JLabel taskDaysL;
    private javax.swing.JTextField taskName;
    private javax.swing.JLabel taskNameL;
    private org.jdesktop.swingx.JXTaskPane taskPane;
    private javax.swing.JComboBox taskParent;
    private javax.swing.JLabel taskParentL;
    private javax.swing.JSpinner taskPriority;
    private javax.swing.JLabel taskPriorityL;
    private javax.swing.JComboBox taskProject;
    private javax.swing.JLabel taskProjectL;
    private javax.swing.JSpinner taskSD;
    private javax.swing.JLabel taskSDL;
    private javax.swing.JScrollPane taskScroll;
    private com.toedter.calendar.JDateChooser taskStart;
    private javax.swing.JLabel taskStartL;
    private javax.swing.JComboBox taskStatus;
    private javax.swing.JLabel taskStatusL;
    private javax.swing.JComboBox taskType;
    private javax.swing.JLabel taskTypeL;
    private javax.swing.JComboBox taskWorker;
    private javax.swing.JLabel taskWorkerL;
    private javax.swing.ButtonGroup tasksG;
    private javax.swing.JLabel title;
    private javax.swing.JButton update;
    private javax.swing.JButton worker;
    private javax.swing.JButton workers;
    // End of variables declaration//GEN-END:variables
}
