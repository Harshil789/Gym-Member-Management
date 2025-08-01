import javax.swing.*;
import java.awt.*;

public class LoginScreen extends JFrame {
    public LoginScreen() {
        setTitle("Admin Login");
        setSize(400, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setBounds(50, 50, 100, 30);
        JTextField userField = new JTextField();
        userField.setBounds(150, 50, 180, 30);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setBounds(50, 100, 100, 30);
        JPasswordField passField = new JPasswordField();
        passField.setBounds(150, 100, 180, 30);

        JButton loginBtn = new JButton("Login");
        loginBtn.setBounds(130, 150, 120, 40);
        loginBtn.setBackground(new Color(46, 139, 87));
        loginBtn.setForeground(Color.WHITE);

        loginBtn.addActionListener(e -> {
            String user = userField.getText();
            String pass = new String(passField.getPassword());
            if (user.equals("admin") && pass.equals("admin123")) {
                dispose();
                new MainApp();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials");
            }
        });

        add(userLabel); add(userField);
        add(passLabel); add(passField);
        add(loginBtn);
        setVisible(true);
    }
}
