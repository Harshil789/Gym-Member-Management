
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.Properties;

public class LoginScreen extends JFrame {
    private static final String CREDS_FILE = "credentials.properties";

    public LoginScreen() {
        setTitle("Gym Management System — Login");
        setSize(420, 280);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);
        getContentPane().setBackground(new Color(30, 30, 40));

        JLabel heading = new JLabel("🏋️ Gym Management System", SwingConstants.CENTER);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 16));
        heading.setForeground(Color.WHITE);
        heading.setBounds(0, 20, 420, 30);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setBounds(60, 70, 100, 30);
        userLabel.setForeground(Color.LIGHT_GRAY);

        JTextField userField = new JTextField();
        userField.setBounds(170, 70, 180, 30);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setBounds(60, 115, 100, 30);
        passLabel.setForeground(Color.LIGHT_GRAY);

        JPasswordField passField = new JPasswordField();
        passField.setBounds(170, 115, 180, 30);

        JButton loginBtn = new JButton("Login");
        loginBtn.setBounds(100, 165, 100, 38);
        loginBtn.setBackground(new Color(46, 139, 87));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        loginBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton changePwdBtn = new JButton("Change Password");
        changePwdBtn.setBounds(215, 165, 150, 38);
        changePwdBtn.setBackground(new Color(60, 60, 80));
        changePwdBtn.setForeground(Color.WHITE);
        changePwdBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel errorLabel = new JLabel("", SwingConstants.CENTER);
        errorLabel.setBounds(0, 210, 420, 25);
        errorLabel.setForeground(new Color(255, 80, 80));

        add(heading); add(userLabel); add(userField);
        add(passLabel); add(passField);
        add(loginBtn); add(changePwdBtn); add(errorLabel);

        ensureDefaultCredentials();

        loginBtn.addActionListener(e -> {
            String user = userField.getText().trim();
            String pass = new String(passField.getPassword());
            if (validateCredentials(user, pass)) {
                dispose();
                new MainApp();
            } else {
                errorLabel.setText("Invalid username or password.");
            }
        });

        // Allow Enter key on password field
        passField.addActionListener(e -> loginBtn.doClick());

        changePwdBtn.addActionListener(e -> showChangePasswordDialog());

        setVisible(true);
    }

    private void ensureDefaultCredentials() {
        File f = new File(CREDS_FILE);
        if (!f.exists()) {
            Properties props = new Properties();
            props.setProperty("username", "admin");
            props.setProperty("password", "admin123");
            try (FileOutputStream out = new FileOutputStream(f)) {
                props.store(out, "Gym Management Credentials");
            } catch (IOException ex) { ex.printStackTrace(); }
        }
    }

    private boolean validateCredentials(String user, String pass) {
        Properties props = new Properties();
        try (FileInputStream in = new FileInputStream(CREDS_FILE)) {
            props.load(in);
            return props.getProperty("username", "admin").equals(user)
                    && props.getProperty("password", "admin123").equals(pass);
        } catch (IOException e) { return user.equals("admin") && pass.equals("admin123"); }
    }

    private void showChangePasswordDialog() {
        JPasswordField oldPass = new JPasswordField();
        JPasswordField newPass = new JPasswordField();
        JPasswordField confirmPass = new JPasswordField();
        Object[] fields = {"Current Password:", oldPass, "New Password:", newPass, "Confirm New Password:", confirmPass};

        int result = JOptionPane.showConfirmDialog(this, fields, "Change Password", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) return;

        Properties props = new Properties();
        try (FileInputStream in = new FileInputStream(CREDS_FILE)) {
            props.load(in);
            String storedPass = props.getProperty("password", "admin123");
            if (!storedPass.equals(new String(oldPass.getPassword()))) {
                JOptionPane.showMessageDialog(this, "Current password is incorrect."); return;
            }
            if (new String(newPass.getPassword()).length() < 6) {
                JOptionPane.showMessageDialog(this, "New password must be at least 6 characters."); return;
            }
            if (!new String(newPass.getPassword()).equals(new String(confirmPass.getPassword()))) {
                JOptionPane.showMessageDialog(this, "Passwords do not match."); return;
            }
            props.setProperty("password", new String(newPass.getPassword()));
            try (FileOutputStream out = new FileOutputStream(CREDS_FILE)) {
                props.store(out, "Gym Management Credentials");
            }
            JOptionPane.showMessageDialog(this, "Password changed successfully!");
        } catch (IOException e) { e.printStackTrace(); }
    }
}
