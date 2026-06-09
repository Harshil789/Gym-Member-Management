import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class PaymentManager extends JPanel {
    private DefaultTableModel tableModel;
    private JTable table;
    private final DataStore store = DataStore.getInstance();

    public PaymentManager() {
        setLayout(new BorderLayout());

        tableModel = new DefaultTableModel(
                new String[]{"Payment ID","Member ID","Member Name","Plan","Amount","Date","Status"}, 0
        ) { @Override public boolean isCellEditable(int r,int c){return false;} };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton markPaidBtn    = new JButton("Mark as Paid");
        JButton markPendingBtn = new JButton("Mark as Pending");
        JButton markOverdueBtn = new JButton("Mark as Overdue");
        JButton syncBtn        = new JButton("Sync from Members");
        JButton refreshBtn     = new JButton("Refresh View");

        markPaidBtn.setBackground(new Color(46,139,87));   markPaidBtn.setForeground(Color.WHITE);
        markPendingBtn.setBackground(new Color(200,150,0)); markPendingBtn.setForeground(Color.WHITE);
        markOverdueBtn.setBackground(new Color(200,50,50)); markOverdueBtn.setForeground(Color.WHITE);

        bottomPanel.add(markPaidBtn); bottomPanel.add(markPendingBtn);
        bottomPanel.add(markOverdueBtn); bottomPanel.add(syncBtn); bottomPanel.add(refreshBtn);

        add(new JScrollPane(table), BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        refreshTable();

        markPaidBtn.addActionListener(e -> updateStatus("Paid"));
        markPendingBtn.addActionListener(e -> updateStatus("Pending"));
        markOverdueBtn.addActionListener(e -> updateStatus("Overdue"));

        syncBtn.addActionListener(e -> {
            store.syncPaymentsFromMembers();
            refreshTable();
            JOptionPane.showMessageDialog(this,"Synced payment records from members.");
        });

        refreshBtn.addActionListener(e -> refreshTable());
    }

    private void updateStatus(String status) {
        int row = table.getSelectedRow();
        if(row<0){JOptionPane.showMessageDialog(this,"Select a payment record."); return;}
        // This updates payment AND syncs back to member automatically
        store.setPaymentStatus(row, status);
        refreshTable();
        JOptionPane.showMessageDialog(this,"Status updated to: " + status);
    }

    public void refreshTable() {
        tableModel.setRowCount(0);
        for(Payment p : store.getPayments()) {
            tableModel.addRow(new Object[]{
                    p.getPaymentId(), p.getMemberId(), p.getMemberName(),
                    p.getPlan(), "₹"+p.getAmount(), p.getPaymentDate(), p.getStatus()
            });
        }
    }
}