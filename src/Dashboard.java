import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class Dashboard extends JPanel {
    private JLabel totalMembersLbl, totalStaffLbl, todayAttendanceLbl,
            expiringLbl, overduePaymentsLbl, activeMembersLbl;

    public Dashboard() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("🏋️ Gym Management — Dashboard", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JPanel cardsPanel = new JPanel(new GridLayout(2, 3, 15, 15));

        totalMembersLbl     = makeCard(cardsPanel, "Total Members",       "0", new Color(30, 120, 200));
        totalStaffLbl       = makeCard(cardsPanel, "Total Staff",         "0", new Color(46, 139, 87));
        todayAttendanceLbl  = makeCard(cardsPanel, "Today's Check-ins",   "0", new Color(180, 100, 20));
        activeMembersLbl    = makeCard(cardsPanel, "Active Members",      "0", new Color(100, 50, 180));
        expiringLbl         = makeCard(cardsPanel, "Expiring This Week",  "0", new Color(200, 150, 0));
        overduePaymentsLbl  = makeCard(cardsPanel, "Overdue Payments",    "0", new Color(200, 50, 50));

        JButton refreshBtn = new JButton("🔄 Refresh Dashboard");
        refreshBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        refreshBtn.setBackground(new Color(30, 120, 200));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshBtn.addActionListener(e -> refresh());

        JPanel south = new JPanel();
        south.add(refreshBtn);

        add(title,      BorderLayout.NORTH);
        add(cardsPanel, BorderLayout.CENTER);
        add(south,      BorderLayout.SOUTH);

        refresh();
    }

    private JLabel makeCard(JPanel parent, String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(color);
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        card.setPreferredSize(new Dimension(180, 100));

        JLabel titleLbl = new JLabel(title, SwingConstants.CENTER);
        titleLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        titleLbl.setForeground(new Color(220, 220, 220));

        JLabel valueLbl = new JLabel(value, SwingConstants.CENTER);
        valueLbl.setFont(new Font("Segoe UI", Font.BOLD, 36));
        valueLbl.setForeground(Color.WHITE);

        card.add(titleLbl, BorderLayout.NORTH);
        card.add(valueLbl, BorderLayout.CENTER);
        parent.add(card);
        return valueLbl;
    }

    public void refresh() {
        int totalMembers    = 0;
        int activeMembers   = 0;
        int expiringCount   = 0;

        File membersFile = new File("members.dat");
        if (membersFile.exists()) {
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(membersFile))) {
                ArrayList<Member> members = (ArrayList<Member>) in.readObject();
                totalMembers = members.size();
                for (Member m : members) {
                    if (!m.isExpired()) activeMembers++;
                    if (m.isExpiringSoon()) expiringCount++;
                }
            } catch (IOException | ClassNotFoundException e) { e.printStackTrace(); }
        }

        int totalStaff = 0;
        File staffFile = new File("staff.dat");
        if (staffFile.exists()) {
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(staffFile))) {
                ArrayList<Staff> staffList = (ArrayList<Staff>) in.readObject();
                totalStaff = staffList.size();
            } catch (IOException | ClassNotFoundException e) { e.printStackTrace(); }
        }

        int todayCount = 0;
        File attendanceFile = new File("attendance.csv");
        if (attendanceFile.exists()) {
            String today = LocalDate.now().toString();
            try (BufferedReader reader = new BufferedReader(new FileReader(attendanceFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.contains(today)) todayCount++;
                }
            } catch (IOException e) { e.printStackTrace(); }
        }

        int overdueCount = 0;
        File paymentsFile = new File("payments.dat");
        if (paymentsFile.exists()) {
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(paymentsFile))) {
                ArrayList<Payment> payments = (ArrayList<Payment>) in.readObject();
                for (Payment p : payments) {
                    if ("Overdue".equals(p.getStatus())) overdueCount++;
                }
            } catch (IOException | ClassNotFoundException e) { e.printStackTrace(); }
        }

        totalMembersLbl.setText(String.valueOf(totalMembers));
        totalStaffLbl.setText(String.valueOf(totalStaff));
        todayAttendanceLbl.setText(String.valueOf(todayCount));
        activeMembersLbl.setText(String.valueOf(activeMembers));
        expiringLbl.setText(String.valueOf(expiringCount));
        overduePaymentsLbl.setText(String.valueOf(overdueCount));
    }
}