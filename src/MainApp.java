// MainApp.java
import javax.swing.*;

public class MainApp extends JFrame {
    public MainApp() {
        setTitle("🏋️ Gym Management System");
        setSize(900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Register/View Members", new MemberManager());
        tabs.addTab("Attendance", new AttendanceTracker());
        tabs.addTab("Staff Management", new StaffManager());

        add(tabs);
        setVisible(true);
    }

    public static void main(String[] args) {
        new LoginScreen();
    }
}