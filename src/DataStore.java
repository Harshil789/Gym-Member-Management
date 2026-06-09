import java.io.*;
import java.util.ArrayList;

/**
 * Singleton DataStore — single source of truth for all data.
 * Every panel reads and writes through here, so changes are
 * instantly visible across the whole application.
 */
public class DataStore {

    private static DataStore instance;

    private ArrayList<Member>  members  = new ArrayList<>();
    private ArrayList<Staff>   staff    = new ArrayList<>();
    private ArrayList<Payment> payments = new ArrayList<>();

    private static final String MEMBERS_FILE  = "members.dat";
    private static final String STAFF_FILE    = "staff.dat";
    private static final String PAYMENTS_FILE = "payments.dat";

    private DataStore() {
        loadAll();
    }

    public static DataStore getInstance() {
        if (instance == null) instance = new DataStore();
        return instance;
    }

    // ─────────────────────────────────────────
    //  MEMBERS
    // ─────────────────────────────────────────

    public ArrayList<Member> getMembers() { return members; }

    public void addMember(Member m) {
        members.add(m);
        saveMembers();
    }

    public void updateMember(Member m) {
        // Find the payment record for this member and sync fee status
        for (Payment p : payments) {
            if (p.getMemberId() == m.getId()) {
                p.setStatus(m.isFeePaid() ? "Paid" : (m.isExpired() ? "Overdue" : "Pending"));
            }
        }
        saveMembers();
        savePayments();
    }

    public void deleteMember(int index) {
        members.remove(index);
        saveMembers();
    }

    // ─────────────────────────────────────────
    //  STAFF
    // ─────────────────────────────────────────

    public ArrayList<Staff> getStaff() { return staff; }

    public void addStaff(Staff s) {
        staff.add(s);
        saveStaff();
    }

    public void updateStaff() { saveStaff(); }

    public void deleteStaff(int index) {
        staff.remove(index);
        saveStaff();
    }

    // ─────────────────────────────────────────
    //  PAYMENTS
    // ─────────────────────────────────────────

    public ArrayList<Payment> getPayments() { return payments; }

    public void addPayment(Payment p) {
        payments.add(p);
        savePayments();
    }

    /**
     * Update payment status AND sync back to the matching Member.
     */
    public void setPaymentStatus(int paymentIndex, String status) {
        Payment p = payments.get(paymentIndex);
        p.setStatus(status);

        // Sync back to member
        for (Member m : members) {
            if (m.getId() == p.getMemberId()) {
                m.setFeePaid("Paid".equals(status));
                break;
            }
        }
        savePayments();
        saveMembers();
    }

    /**
     * Create a payment record for every member that doesn't have one yet.
     */
    public void syncPaymentsFromMembers() {
        for (Member m : members) {
            boolean exists = payments.stream().anyMatch(p -> p.getMemberId() == m.getId());
            if (!exists) {
                String status = m.isFeePaid() ? "Paid" : (m.isExpired() ? "Overdue" : "Pending");
                payments.add(new Payment(
                    m.getId(), m.getName(),
                    Payment.getPlanAmount(m.getPlan()),
                    m.getPlan(), status
                ));
            }
        }
        savePayments();
    }

    // ─────────────────────────────────────────
    //  PERSISTENCE
    // ─────────────────────────────────────────

    public void saveMembers() {
        save(MEMBERS_FILE, members);
    }

    public void saveStaff() {
        save(STAFF_FILE, staff);
    }

    public void savePayments() {
        save(PAYMENTS_FILE, payments);
    }

    private void save(String filename, Object data) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename))) {
            out.writeObject(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private void loadAll() {
        // Members
        File mf = new File(MEMBERS_FILE);
        if (mf.exists()) {
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(mf))) {
                members = (ArrayList<Member>) in.readObject();
                int maxId = members.stream().mapToInt(Member::getId).max().orElse(0);
                Member.setCounter(maxId + 1);
            } catch (IOException | ClassNotFoundException e) { e.printStackTrace(); }
        }

        // Staff
        File sf = new File(STAFF_FILE);
        if (sf.exists()) {
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(sf))) {
                staff = (ArrayList<Staff>) in.readObject();
                int maxId = staff.stream().mapToInt(Staff::getId).max().orElse(0);
                Staff.setCounter(maxId + 1);
            } catch (IOException | ClassNotFoundException e) { e.printStackTrace(); }
        }

        // Payments
        File pf = new File(PAYMENTS_FILE);
        if (pf.exists()) {
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(pf))) {
                payments = (ArrayList<Payment>) in.readObject();
                int maxId = payments.stream().mapToInt(Payment::getPaymentId).max().orElse(0);
                Payment.setCounter(maxId + 1);
            } catch (IOException | ClassNotFoundException e) { e.printStackTrace(); }
        }
    }
}
