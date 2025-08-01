import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;

public class StaffManager extends JPanel {
    private DefaultTableModel staffModel;
    private ArrayList<Staff> staffList = new ArrayList<>();

    public StaffManager() {
        setLayout(new BorderLayout());
        JTextField nameField = new JTextField(10);
        JTextField roleField = new JTextField(10);
        JButton addBtn = new JButton("Add Staff");
        JButton deleteBtn = new JButton("Delete");

        staffModel = new DefaultTableModel(new String[]{"Name", "Role"}, 0);
        JTable table = new JTable(staffModel);

        JPanel top = new JPanel();
        top.add(new JLabel("Name:"));
        top.add(nameField);
        top.add(new JLabel("Role:"));
        top.add(roleField);
        top.add(addBtn);
        top.add(deleteBtn);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        loadStaffFromFile();

        addBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String role = roleField.getText().trim();

            if (name.isEmpty() || role.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields.");
                return;
            }

            Staff newStaff = new Staff(name, role);
            staffList.add(newStaff);
            staffModel.addRow(new Object[]{name, role});
            saveStaffToFile();

            nameField.setText("");
            roleField.setText("");
        });

        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                staffModel.removeRow(row);
                staffList.remove(row);
                saveStaffToFile();
            }
        });
    }

    private void saveStaffToFile() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("staff.dat"))) {
            out.writeObject(staffList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private void loadStaffFromFile() {
        File file = new File("staff.dat");
        if (!file.exists()) return;

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            staffList = (ArrayList<Staff>) in.readObject();
            for (Staff s : staffList) {
                staffModel.addRow(new Object[]{s.getName(), s.getRole()});
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Staff> getStaffList() {
        return staffList;
    }
}
