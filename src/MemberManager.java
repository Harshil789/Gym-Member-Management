import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;

public class MemberManager extends JPanel {
    private JTextField nameField, ageField, contactField, emailField, searchField;
    private JComboBox<String> genderBox, planBox;
    private DefaultTableModel tableModel;
    private ArrayList<Member> members = new ArrayList<>();

    public MemberManager() {
        setLayout(new BorderLayout());

        JPanel registerPanel = new JPanel(new GridLayout(7, 2));
        nameField = new JTextField();
        ageField = new JTextField();
        contactField = new JTextField();
        emailField = new JTextField();
        genderBox = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        planBox = new JComboBox<>(new String[]{"Monthly", "Quarterly", "Yearly"});
        JButton addButton = new JButton("Add Member");

        registerPanel.add(new JLabel("Name:")); registerPanel.add(nameField);
        registerPanel.add(new JLabel("Age:")); registerPanel.add(ageField);
        registerPanel.add(new JLabel("Contact (10 digits):")); registerPanel.add(contactField);
        registerPanel.add(new JLabel("Email:")); registerPanel.add(emailField);
        registerPanel.add(new JLabel("Gender:")); registerPanel.add(genderBox);
        registerPanel.add(new JLabel("Plan:")); registerPanel.add(planBox);
        registerPanel.add(addButton);

        tableModel = new DefaultTableModel(new String[]{"Name", "Age", "Contact", "Email", "Gender", "Plan"}, 0);
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        JPanel bottomPanel = new JPanel();
        searchField = new JTextField(10);
        JButton searchBtn = new JButton("Search");
        JButton deleteBtn = new JButton("Delete");
        bottomPanel.add(new JLabel("Search by Name:"));
        bottomPanel.add(searchField);
        bottomPanel.add(searchBtn);
        bottomPanel.add(deleteBtn);

        add(registerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        loadMembersFromFile();

        addButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String ageStr = ageField.getText().trim();
            String contact = contactField.getText().trim();
            String email = emailField.getText().trim();
            String gender = (String) genderBox.getSelectedItem();
            String plan = (String) planBox.getSelectedItem();

            if (name.isEmpty() || ageStr.isEmpty() || contact.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields.");
                return;
            }

            if (!contact.matches("\\d{10}")) {
                JOptionPane.showMessageDialog(this, "Contact number must be exactly 10 digits.");
                return;
            }

            if (!email.matches("^[\\w.-]+@[\\w.-]+\\.[A-Za-z]{2,6}$")) {
                JOptionPane.showMessageDialog(this, "Invalid email format.");
                return;
            }

            try {
                int age = Integer.parseInt(ageStr);
                Member newMember = new Member(name, age, contact, email, gender, plan);
                members.add(newMember);
                tableModel.addRow(new Object[]{name, age, contact, email, gender, plan});
                saveMembersToFile();

                nameField.setText("");
                ageField.setText("");
                contactField.setText("");
                emailField.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Age must be a valid number.");
            }
        });

        searchBtn.addActionListener(e -> {
            String searchName = searchField.getText().toLowerCase();
            for (Member m : members) {
                if (m.getName().toLowerCase().equals(searchName)) {
                    JOptionPane.showMessageDialog(this, "Found: " + m.getName() + " (" + m.getPlan() + ")");
                    return;
                }
            }
            JOptionPane.showMessageDialog(this, "Member not found");
        });

        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                tableModel.removeRow(row);
                members.remove(row);
                saveMembersToFile();
            }
        });
    }

    private void saveMembersToFile() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("members.dat"))) {
            out.writeObject(members);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private void loadMembersFromFile() {
        File file = new File("members.dat");
        if (!file.exists()) return;

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            members = (ArrayList<Member>) in.readObject();
            for (Member m : members) {
                tableModel.addRow(new Object[]{m.getName(), m.getAge(), m.getContact(), m.getEmail(), m.getGender(), m.getPlan()});
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Member> getMemberList() {
        return members;
    }
}
