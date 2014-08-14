package connection;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.*;
import org.json.JSONException;


public class Signin {

    private JButton mSignin;
    private JButton mSignup;
    private JTextField login;
    private JPasswordField password;
    private Functions func;
    private Boolean Super;
    private Client client;
    private JCheckBox auto;
    private Preferences pref;
    private String UserName;
    private final JFrame f = new JFrame ("ProjectL");
    
    public Signin(Functions func, Client client) {
        this.func = func;
        this.client = client;
        pref = Preferences.userRoot().node("autoLogin");
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    createUI();
                } catch (BackingStoreException ex) {
                    Logger.getLogger(Signin.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    public static boolean checkLogin(String login) {
        Pattern pattern = Pattern.compile("^([a-zA-Z0-9])(\\w|-|_)+([a-z0-9])$");
        Matcher matcher = pattern.matcher(login);
        return matcher.matches(); 
    }
    
    public static boolean checkMail(String mail) {
        Pattern pattern = Pattern.compile("^([a-z0-9])(\\w|[.]|-|_)+([a-z0-9])@([a-z0-9])([a-z0-9.-]*)([a-z0-9])([.]{1})([a-z]{2,4})$");
        Matcher matcher = pattern.matcher(mail);
        return matcher.matches(); 
    }
    
    public static boolean checkNumber(String numb) {
        Pattern pattern = Pattern.compile("^[0-9]*$");
        Matcher matcher = pattern.matcher(numb);
        return matcher.matches(); 
    }
    
    public static boolean checkDecimal(String numb) {
        Pattern pattern = Pattern.compile("^[+\\-]?\\d+(?:\\.\\d+)?$");
        Matcher matcher = pattern.matcher(numb);
        return matcher.matches(); 
    }

    private void createUI() throws BackingStoreException {
    
        f.setIconImage(Toolkit.getDefaultToolkit().getImage("images/icon.png"));
        final WaitLayerUI layerUI = new WaitLayerUI();
        JPanel panel = createPanel();
        JLayer<JPanel> jlayer = new JLayer<JPanel>(panel, layerUI);
    
        final Timer stopper = new Timer(2000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (Super == null) {
                    JOptionPane.showMessageDialog(null, "Sign in error", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    UserName = login.getText();
                    f.hide();
                    client.Visible(Super);
                }
                layerUI.stop();
                f.setEnabled(true);
            }
        });
        stopper.setRepeats(false);

        f.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {
                f.requestFocus();
            }

            @Override
            public void mousePressed(MouseEvent e) {}

            @Override
            public void mouseReleased(MouseEvent e) {}

            @Override
            public void mouseEntered(MouseEvent e) {}

            @Override
            public void mouseExited(MouseEvent e) {}
        });
        
        mSignin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (!checkLogin(login.getText())) {
                    JOptionPane.showMessageDialog(null, "Incorrect login", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (password.getText().equals("Password...")) {
                    JOptionPane.showMessageDialog(null, "Empty password", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (auto.isSelected()) {
                    pref.putBoolean("Auto", true);
                    pref.put("Login", login.getText());
                    pref.put("Password", password.getText());
                } else {
                    pref.putBoolean("Auto", false);
                }
                
                try {
                    Super = func.signin(login.getText(), password.getText());
                } catch (IOException | JSONException | ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                    Logger.getLogger(Signin.class.getName()).log(Level.SEVERE, null, ex);
                }
                f.setEnabled(false);
                layerUI.start();
                if (!stopper.isRunning()) {
                    stopper.start();
                }
            }
        });

        login.addFocusListener(new FocusListener() {

            @Override
            public void focusGained(FocusEvent e) {
                if (((JTextField)(e.getSource())).getText().equals("Login...")) {
                    ((JTextField)(e.getSource())).setText(null);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (((JTextField)(e.getSource())).getText().trim().length() == 0) {
                    ((JTextField)(e.getSource())).setText("Login...");
                }
            }
        });

        mSignup.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {
                client.showSignup();
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                mSignup.setBorderPainted(true);
                mSignup.setFocusPainted(true);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                mSignup.setBorderPainted(false);
                mSignup.setFocusPainted(false);
            }
        });

        password.addFocusListener(new FocusListener() {

            @Override
            public void focusGained(FocusEvent e) {
                if (((JTextField)(e.getSource())).getText().equals("Password...")) {
                    ((JTextField)(e.getSource())).setText(null);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (((JTextField)(e.getSource())).getText().trim().length() == 0) {
                    ((JTextField)(e.getSource())).setText("Password...");
                }
            }
        });

        f.add (jlayer);
        f.setResizable(false);
        f.setSize(200, 400);
        f.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
        f.setLocationRelativeTo(null);
        f.setVisible(true);
        f.requestFocus();
        
        if (pref.getBoolean("Auto", false)) {
            try {
                Super = func.signin(login.getText(), password.getText());
            } catch (IOException | JSONException | ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                Logger.getLogger(Signin.class.getName()).log(Level.SEVERE, null, ex);
            }
            f.setEnabled(false);
            layerUI.start();
            if (!stopper.isRunning()) {
                stopper.start();
            }
        }
    }

    private JPanel createPanel() throws BackingStoreException {
        JPanel p = new JPanel();
        p.setLayout(new GridBagLayout());
    
        GridBagConstraints c = new GridBagConstraints();
        JLabel title = new JLabel();
        login = new JTextField();
        password = new JPasswordField();
        mSignin = new JButton();
        mSignup = new JButton();
        auto = new JCheckBox();

        title.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        title.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        title.setText("Sign in");
        title.setToolTipText("");
        
        if (pref.getBoolean("Auto", false)) {
            login.setText(pref.get("Login", "Login..."));
            password.setText(pref.get("Password", "Password..."));
            auto.setSelected(pref.getBoolean("Auto", false));
        } else {
            login.setText("Login...");
            password.setText("Password...");
        }
        
        login.setFont(new java.awt.Font("Tahoma", 0, 18));
        password.setFont(new java.awt.Font("Tahoma", 0, 18));

        mSignin.setText("Sign in");
        mSignup.setText("Sign up");
        
        mSignup.setBorderPainted(false);
        mSignup.setContentAreaFilled(false);
        mSignup.setFocusPainted(false);
        mSignup.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        auto.setText("Automaticaly sign in");

        
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.PAGE_START;
        c.gridx = 0;
        c.gridy = 0;
        c.insets.top = 10;
        p.add(title, c);
 
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridwidth = 3;
        c.gridx = GridBagConstraints.RELATIVE;
        c.gridy = 1;
        c.insets.top = 100;        
        c.insets.left = 10;// поставить заглушку
        c.insets.right = 10;// поставить заглушку
        
        p.add(login, c);
        
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridwidth = 3;
        c.gridx = GridBagConstraints.RELATIVE;
        c.gridy = 2;
        c.insets.top = 10;      
        p.add(password, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridwidth = 3;
        c.gridx = GridBagConstraints.RELATIVE;
        c.gridy = 3;
        c.insets.top = 10;      
        p.add(auto, c);
        
        c.fill = GridBagConstraints.NONE;
        c.ipady = 0;
        c.ipadx = 0;
        c.weighty = 1.0;
        c.anchor = GridBagConstraints.PAGE_END;
        c.insets.bottom = 10;
        c.insets.left = 90;
        c.insets.right = 0;
        c.gridx = 0;
        c.gridwidth = 1;
        c.gridy = 4;
        p.add(mSignin, c);

        c.fill = GridBagConstraints.NONE;
        c.ipady = 0;
        c.ipadx = 0;
        c.weighty = 1.0;
        c.anchor = GridBagConstraints.PAGE_END;
        c.insets.bottom = 10;
        c.insets.left = 0;
        c.insets.right = 90;
        c.gridx = 0;
        c.gridwidth = 1;
        c.gridy = 4;
        p.add(mSignup, c);
        return p;
    }
    
    public String getLogin() {
        return UserName;
    }

    public boolean isSuper() {
        return Super;
    }
    
    public void setEnabled(boolean en) {
        f.setEnabled(en);
        if (en) {
            f.setVisible(true);
        }
    }
}