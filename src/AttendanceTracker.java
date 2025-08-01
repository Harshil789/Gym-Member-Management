import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class AttendanceTracker extends JPanel {
    private DefaultTableModel model;
    private JTextField nameField;
    private ArrayList<String> memberNames = new ArrayList<>();
    private ArrayList<String> staffNames = new ArrayList<>();
    private static final String ATTENDANCE_FILE = "attendance.csv";

    public AttendanceTracker() {
        setLayout(new BorderLayout());
        nameField = new JTextField(15);
        JButton checkInBtn = new JButton("Check In");

        model = new DefaultTableModel(new String[]{"Name", "Check-In Time"}, 0);
        JTable table = new JTable(model);

        JPanel input = new JPanel();
        input.add(new JLabel("Name:"));
        input.add(nameField);
        input.add(checkInBtn);

        checkInBtn.addActionListener(e -> {
            String name = nameField.getText().trim();

            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a name.");
                return;
            }

            if (!isValidName(name)) {
                JOptionPane.showMessageDialog(this, "Name not found in member or staff list.");
                return;
            }

            String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            model.addRow(new Object[]{name, time});
            saveAttendanceToCSV(name, time);
            nameField.setText("");
        });

        add(input, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        loadMembers();
        loadStaff();
        loadPreviousAttendance();
    }

    private void saveAttendanceToCSV(String name, String time) {
        try (FileWriter writer = new FileWriter(ATTENDANCE_FILE, true)) {
            writer.write(name + "," + time + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadPreviousAttendance() {
        File file = new File(ATTENDANCE_FILE);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 2);
                if (parts.length == 2) {
                    model.addRow(new Object[]{parts[0], parts[1]});
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isValidName(String name) {
        return memberNames.contains(name.toLowerCase()) || staffNames.contains(name.toLowerCase());
    }

    private void loadMembers() {
        File file = new File("members.dat");
        if (!file.exists()) return;

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            ArrayList<Member> members = (ArrayList<Member>) in.readObject();
            for (Member m : members) {
                memberNames.add(m.getName().toLowerCase());
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void loadStaff() {
        File file = new File("staff.dat");
        if (!file.exists()) return;

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            ArrayList<Staff> staffList = (ArrayList<Staff>) in.readObject();
            for (Staff s : staffList) {
                staffNames.add(s.getName().toLowerCase());
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
