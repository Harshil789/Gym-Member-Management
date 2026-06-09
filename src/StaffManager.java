import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class StaffManager extends JPanel {
    private DefaultTableModel staffModel;
    private JTable table;
    private JTextField nameField, roleField, contactField, emailField, salaryField;
    private final DataStore store = DataStore.getInstance();

    public StaffManager() {
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(6,2,5,5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Add / Edit Staff"));
        nameField=new JTextField(); roleField=new JTextField();
        contactField=new JTextField(); emailField=new JTextField(); salaryField=new JTextField();
        JButton addBtn=new JButton("Add Staff"), editBtn=new JButton("Edit Selected");
        addBtn.setBackground(new Color(46,139,87)); addBtn.setForeground(Color.WHITE);
        editBtn.setBackground(new Color(30,100,200)); editBtn.setForeground(Color.WHITE);

        formPanel.add(new JLabel("Name:"));       formPanel.add(nameField);
        formPanel.add(new JLabel("Role:"));       formPanel.add(roleField);
        formPanel.add(new JLabel("Contact:"));    formPanel.add(contactField);
        formPanel.add(new JLabel("Email:"));      formPanel.add(emailField);
        formPanel.add(new JLabel("Salary (₹):")); formPanel.add(salaryField);
        formPanel.add(addBtn); formPanel.add(editBtn);

        staffModel = new DefaultTableModel(
                new String[]{"ID","Name","Role","Contact","Email","Salary"}, 0
        ) { @Override public boolean isCellEditable(int r,int c){return false;} };
        table = new JTable(staffModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton deleteBtn = new JButton("Delete");
        deleteBtn.setBackground(new Color(200,50,50)); deleteBtn.setForeground(Color.WHITE);
        bottom.add(deleteBtn);

        add(formPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        refreshTable();

        addBtn.addActionListener(e -> {
            String name=nameField.getText().trim(), role=roleField.getText().trim();
            String contact=contactField.getText().trim(), email=emailField.getText().trim(), salStr=salaryField.getText().trim();
            if(name.isEmpty()||role.isEmpty()){JOptionPane.showMessageDialog(this,"Name and Role required."); return;}
            if(!contact.isEmpty()&&!contact.matches("\\d{10}")){JOptionPane.showMessageDialog(this,"Contact must be 10 digits."); return;}
            if(!email.isEmpty()&&!email.matches("^[\\w.-]+@[\\w.-]+\\.[A-Za-z]{2,6}$")){JOptionPane.showMessageDialog(this,"Invalid email."); return;}
            for(Staff s:store.getStaff()) if(s.getName().equalsIgnoreCase(name)&&s.getRole().equalsIgnoreCase(role)){
                JOptionPane.showMessageDialog(this,"Staff already exists!"); return;}
            try{
                double salary=salStr.isEmpty()?0:Double.parseDouble(salStr);
                Staff s=new Staff(name,role,contact,email,salary);
                store.addStaff(s); refreshTable(); clearForm();
            }catch(NumberFormatException ex){JOptionPane.showMessageDialog(this,"Salary must be a number.");}
        });

        editBtn.addActionListener(e -> {
            int row=table.getSelectedRow();
            if(row<0){JOptionPane.showMessageDialog(this,"Select a staff member."); return;}
            Staff s=store.getStaff().get(row);
            nameField.setText(s.getName()); roleField.setText(s.getRole());
            contactField.setText(s.getContact()); emailField.setText(s.getEmail());
            salaryField.setText(String.valueOf(s.getSalary()));
            int ok=JOptionPane.showConfirmDialog(this,"Edit form above then click OK.","Edit",JOptionPane.OK_CANCEL_OPTION);
            if(ok==JOptionPane.OK_OPTION){
                try{
                    s.setName(nameField.getText().trim()); s.setRole(roleField.getText().trim());
                    s.setContact(contactField.getText().trim()); s.setEmail(emailField.getText().trim());
                    s.setSalary(Double.parseDouble(salaryField.getText().trim()));
                    store.updateStaff(); refreshTable(); clearForm();
                    JOptionPane.showMessageDialog(this,"Staff updated!");
                }catch(NumberFormatException ex){JOptionPane.showMessageDialog(this,"Salary must be a number.");}
            }
        });

        deleteBtn.addActionListener(e -> {
            int row=table.getSelectedRow();
            if(row<0){JOptionPane.showMessageDialog(this,"Select a staff member."); return;}
            if(JOptionPane.showConfirmDialog(this,"Delete this staff?","Confirm",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
                store.deleteStaff(row); refreshTable();}
        });
    }

    public void refreshTable(){
        staffModel.setRowCount(0);
        for(Staff s:store.getStaff())
            staffModel.addRow(new Object[]{s.getId(),s.getName(),s.getRole(),s.getContact(),s.getEmail(),"₹"+s.getSalary()});
    }

    private void clearForm(){nameField.setText("");roleField.setText("");contactField.setText("");emailField.setText("");salaryField.setText("");}
}
