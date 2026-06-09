

import javax.swing.*;
import java.awt.*;

public class MainApp extends JFrame {
    public MainApp() {
        setTitle("🏋️ Gym Management System");
        setSize(1000, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        Dashboard dashboard = new Dashboard();

        tabs.addTab("📊 Dashboard",         dashboard);
        tabs.addTab("👤 Members",           new MemberManager());
        tabs.addTab("✅ Attendance",         new AttendanceTracker());
        tabs.addTab("👥 Staff",             new StaffManager());
        tabs.addTab("💰 Payments",          new PaymentManager());

        // Refresh dashboard whenever its tab is selected
        tabs.addChangeListener(e -> {
            if (tabs.getSelectedComponent() instanceof Dashboard d) {
                d.refresh();
            }
        });

        add(tabs);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginScreen::new);
    }
}
