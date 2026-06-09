import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

public class AttendanceTracker extends JPanel {
    private DefaultTableModel model;
    private JTextField nameField;
    private static final String ATTENDANCE_FILE = "attendance.csv";
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private HashMap<String, LocalDateTime> activeCheckIns = new HashMap<>();
    private final DataStore store = DataStore.getInstance();

    public AttendanceTracker() {
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Attendance"));
        nameField = new JTextField(15);
        JButton checkInBtn  = new JButton("Check In");
        JButton checkOutBtn = new JButton("Check Out");
        checkInBtn.setBackground(new Color(46,139,87));   checkInBtn.setForeground(Color.WHITE);
        checkOutBtn.setBackground(new Color(200,100,30)); checkOutBtn.setForeground(Color.WHITE);
        inputPanel.add(new JLabel("Name:")); inputPanel.add(nameField);
        inputPanel.add(checkInBtn); inputPanel.add(checkOutBtn);

        model = new DefaultTableModel(
                new String[]{"Name","Check-In Time","Check-Out Time","Duration"}, 0
        ) { @Override public boolean isCellEditable(int r,int c){return false;} };
        JTable table = new JTable(model);

        add(inputPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        loadPreviousAttendance();

        checkInBtn.addActionListener(e -> {
            String name=nameField.getText().trim();
            if(name.isEmpty()){JOptionPane.showMessageDialog(this,"Enter a name."); return;}
            String nl=name.toLowerCase();
            boolean valid = store.getMembers().stream().anyMatch(m->m.getName().equalsIgnoreCase(name))
                    || store.getStaff().stream().anyMatch(s->s.getName().equalsIgnoreCase(name));
            if(!valid){JOptionPane.showMessageDialog(this,"Name not found in member or staff list."); return;}
            if(activeCheckIns.containsKey(nl)){JOptionPane.showMessageDialog(this,name+" is already checked in!"); return;}
            LocalDateTime now=LocalDateTime.now();
            activeCheckIns.put(nl,now);
            model.addRow(new Object[]{name,now.format(FMT),"—","In progress"});
            appendToCSV(name,now.format(FMT),"","");
            nameField.setText("");
        });

        checkOutBtn.addActionListener(e -> {
            String name=nameField.getText().trim();
            if(name.isEmpty()){JOptionPane.showMessageDialog(this,"Enter a name."); return;}
            String nl=name.toLowerCase();
            if(!activeCheckIns.containsKey(nl)){JOptionPane.showMessageDialog(this,name+" has not checked in."); return;}
            LocalDateTime checkIn=activeCheckIns.remove(nl);
            LocalDateTime checkOut=LocalDateTime.now();
            String duration=Duration.between(checkIn,checkOut).toMinutes()+" min";
            for(int i=model.getRowCount()-1;i>=0;i--){
                if(model.getValueAt(i,0).toString().equalsIgnoreCase(name)&&model.getValueAt(i,2).toString().equals("—")){
                    model.setValueAt(checkOut.format(FMT),i,2);
                    model.setValueAt(duration,i,3); break;
                }
            }
            nameField.setText("");
            JOptionPane.showMessageDialog(this,name+" checked out. Duration: "+duration);
        });
    }

    private void appendToCSV(String name,String ci,String co,String dur){
        try(FileWriter w=new FileWriter(ATTENDANCE_FILE,true)){w.write(name+","+ci+","+co+","+dur+"\n");}
        catch(IOException e){e.printStackTrace();}
    }

    private void loadPreviousAttendance(){
        File f=new File(ATTENDANCE_FILE);
        if(!f.exists())return;
        try(BufferedReader r=new BufferedReader(new FileReader(f))){
            String line;
            while((line=r.readLine())!=null){
                String[]p=line.split(",",4);
                if(p.length>=2) model.addRow(new Object[]{p[0],p[1],p.length>2?p[2]:"—",p.length>3?p[3]:"—"});
            }
        }catch(IOException e){e.printStackTrace();}
    }
}
