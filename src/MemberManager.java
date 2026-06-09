import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class MemberManager extends JPanel {
    private JTextField nameField, ageField, contactField, emailField, searchField;
    private JComboBox<String> genderBox, planBox;
    private DefaultTableModel tableModel;
    private JTable table;

    private final DataStore store = DataStore.getInstance();

    public MemberManager() {
        setLayout(new BorderLayout());

        JPanel registerPanel = new JPanel(new GridLayout(8, 2, 5, 5));
        registerPanel.setBorder(BorderFactory.createTitledBorder("Register New Member"));

        nameField    = new JTextField();
        ageField     = new JTextField();
        contactField = new JTextField();
        emailField   = new JTextField();
        genderBox    = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        planBox      = new JComboBox<>(new String[]{"Monthly", "Quarterly", "Yearly"});

        JButton addButton  = new JButton("Add Member");
        JButton editButton = new JButton("Edit Selected");
        addButton.setBackground(new Color(46, 139, 87));  addButton.setForeground(Color.WHITE);
        editButton.setBackground(new Color(30, 100, 200)); editButton.setForeground(Color.WHITE);

        registerPanel.add(new JLabel("Name:"));                registerPanel.add(nameField);
        registerPanel.add(new JLabel("Age:"));                 registerPanel.add(ageField);
        registerPanel.add(new JLabel("Contact (10 digits):")); registerPanel.add(contactField);
        registerPanel.add(new JLabel("Email:"));               registerPanel.add(emailField);
        registerPanel.add(new JLabel("Gender:"));              registerPanel.add(genderBox);
        registerPanel.add(new JLabel("Plan:"));                registerPanel.add(planBox);
        registerPanel.add(addButton);
        registerPanel.add(editButton);

        tableModel = new DefaultTableModel(
                new String[]{"ID","Name","Age","Contact","Email","Gender","Plan","Join Date","Expiry","Fee Paid"}, 0
        ) { @Override public boolean isCellEditable(int r,int c){return false;} };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField(12);
        JButton searchBtn   = new JButton("Search");
        JButton deleteBtn   = new JButton("Delete");
        JButton markPaidBtn = new JButton("Mark Fee Paid");
        JButton clearBtn    = new JButton("Show All");
        deleteBtn.setBackground(new Color(200,50,50));   deleteBtn.setForeground(Color.WHITE);
        markPaidBtn.setBackground(new Color(46,139,87)); markPaidBtn.setForeground(Color.WHITE);

        bottomPanel.add(new JLabel("Search:")); bottomPanel.add(searchField);
        bottomPanel.add(searchBtn); bottomPanel.add(deleteBtn);
        bottomPanel.add(markPaidBtn); bottomPanel.add(clearBtn);

        add(registerPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        refreshTable();

        addButton.addActionListener(e -> {
            String name=nameField.getText().trim(), ageStr=ageField.getText().trim();
            String contact=contactField.getText().trim(), email=emailField.getText().trim();
            String gender=(String)genderBox.getSelectedItem(), plan=(String)planBox.getSelectedItem();
            if(name.isEmpty()||ageStr.isEmpty()||contact.isEmpty()||email.isEmpty()){
                JOptionPane.showMessageDialog(this,"Please fill all fields."); return;}
            if(!contact.matches("\\d{10}")){
                JOptionPane.showMessageDialog(this,"Contact must be 10 digits."); return;}
            if(!email.matches("^[\\w.-]+@[\\w.-]+\\.[A-Za-z]{2,6}$")){
                JOptionPane.showMessageDialog(this,"Invalid email."); return;}
            for(Member m:store.getMembers())
                if(m.getName().equalsIgnoreCase(name)&&m.getContact().equals(contact)){
                    JOptionPane.showMessageDialog(this,"Member already exists!"); return;}
            try {
                int age=Integer.parseInt(ageStr);
                if(age<5||age>100){JOptionPane.showMessageDialog(this,"Age must be 5-100."); return;}
                Member nm=new Member(name,age,contact,email,gender,plan);
                store.addMember(nm);
                store.addPayment(new Payment(nm.getId(),nm.getName(),Payment.getPlanAmount(plan),plan,"Pending"));
                refreshTable(); clearForm();
                JOptionPane.showMessageDialog(this,"Member added! ID: "+nm.getId());
            } catch(NumberFormatException ex){JOptionPane.showMessageDialog(this,"Age must be a number.");}
        });

        editButton.addActionListener(e -> {
            int row=table.getSelectedRow();
            if(row<0){JOptionPane.showMessageDialog(this,"Select a member."); return;}
            Member m=store.getMembers().get(row);
            nameField.setText(m.getName()); ageField.setText(String.valueOf(m.getAge()));
            contactField.setText(m.getContact()); emailField.setText(m.getEmail());
            genderBox.setSelectedItem(m.getGender()); planBox.setSelectedItem(m.getPlan());
            int ok=JOptionPane.showConfirmDialog(this,"Edit form above then click OK.","Edit",JOptionPane.OK_CANCEL_OPTION);
            if(ok==JOptionPane.OK_OPTION){
                try{
                    m.setName(nameField.getText().trim()); m.setAge(Integer.parseInt(ageField.getText().trim()));
                    m.setContact(contactField.getText().trim()); m.setEmail(emailField.getText().trim());
                    m.setGender((String)genderBox.getSelectedItem()); m.setPlan((String)planBox.getSelectedItem());
                    store.updateMember(m); refreshTable(); clearForm();
                    JOptionPane.showMessageDialog(this,"Member updated!");
                }catch(NumberFormatException ex){JOptionPane.showMessageDialog(this,"Age must be a number.");}
            }
        });

        searchBtn.addActionListener(e -> {
            String q=searchField.getText().trim().toLowerCase();
            if(q.isEmpty()){refreshTable();return;}
            tableModel.setRowCount(0); boolean found=false;
            for(Member m:store.getMembers()) if(m.getName().toLowerCase().contains(q)){addRowToTable(m);found=true;}
            if(!found) JOptionPane.showMessageDialog(this,"No members found.");
        });

        deleteBtn.addActionListener(e -> {
            int row=table.getSelectedRow();
            if(row<0){JOptionPane.showMessageDialog(this,"Select a member."); return;}
            if(JOptionPane.showConfirmDialog(this,"Delete this member?","Confirm",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
                store.deleteMember(row); refreshTable();}
        });

        markPaidBtn.addActionListener(e -> {
            int row=table.getSelectedRow();
            if(row<0){JOptionPane.showMessageDialog(this,"Select a member."); return;}
            Member m=store.getMembers().get(row);
            m.setFeePaid(true);
            store.updateMember(m);
            refreshTable();
            JOptionPane.showMessageDialog(this,"Fee marked as paid!");
        });

        clearBtn.addActionListener(e->{searchField.setText("");refreshTable();});
    }

    public void refreshTable() {
        tableModel.setRowCount(0);
        for(Member m:store.getMembers()) addRowToTable(m);
    }

    private void addRowToTable(Member m) {
        tableModel.addRow(new Object[]{
                m.getId(),m.getName(),m.getAge(),m.getContact(),m.getEmail(),
                m.getGender(),m.getPlan(),m.getJoinDate(),m.getExpiryDate(),
                m.isFeePaid()?"Yes":"No"});
    }

    private void clearForm(){nameField.setText("");ageField.setText("");contactField.setText("");emailField.setText("");}
    public ArrayList<Member> getMemberList(){return store.getMembers();}
}
