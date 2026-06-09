import java.io.Serializable;
import java.time.LocalDate;

public class Payment implements Serializable {
    private static final long serialVersionUID = 1L;

    private static int counter = 1;

    private int paymentId;
    private int memberId;
    private String memberName;
    private double amount;
    private String plan;
    private LocalDate paymentDate;
    private String status; // "Paid", "Pending", "Overdue"

    public Payment(int memberId, String memberName, double amount, String plan, String status) {
        this.paymentId   = counter++;
        this.memberId    = memberId;
        this.memberName  = memberName;
        this.amount      = amount;
        this.plan        = plan;
        this.paymentDate = LocalDate.now();
        this.status      = status;
    }

    public static double getPlanAmount(String plan) {
        return switch (plan) {
            case "Monthly"   -> 999.0;
            case "Quarterly" -> 2499.0;
            case "Yearly"    -> 7999.0;
            default          -> 999.0;
        };
    }

    // Getters
    public int getPaymentId()       { return paymentId; }
    public int getMemberId()        { return memberId; }
    public String getMemberName()   { return memberName; }
    public double getAmount()       { return amount; }
    public String getPlan()         { return plan; }
    public LocalDate getPaymentDate() { return paymentDate; }
    public String getStatus()       { return status; }

    // Setter
    public void setStatus(String status) { this.status = status; }

    public static void setCounter(int value) { counter = value; }

    @Override
    public String toString() {
        return "Payment#" + paymentId + " | " + memberName + " | ₹" + amount + " | " + status;
    }
}
