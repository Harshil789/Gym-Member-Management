

import java.io.Serializable;
import java.time.LocalDate;

public class Member implements Serializable {
    private static final long serialVersionUID = 2L;

    private static int counter = 1;

    private int id;
    private String name, contact, email, gender, plan;
    private int age;
    private LocalDate joinDate;
    private LocalDate expiryDate;
    private boolean feePaid;

    public Member(String name, int age, String contact, String email, String gender, String plan) {
        this.id = counter++;
        this.name = name;
        this.age = age;
        this.contact = contact;
        this.email = email;
        this.gender = gender;
        this.plan = plan;
        this.joinDate = LocalDate.now();
        this.expiryDate = calculateExpiry(plan);
        this.feePaid = false;
    }

    private LocalDate calculateExpiry(String plan) {
        return switch (plan) {
            case "Monthly"   -> joinDate.plusMonths(1);
            case "Quarterly" -> joinDate.plusMonths(3);
            case "Yearly"    -> joinDate.plusYears(1);
            default          -> joinDate.plusMonths(1);
        };
    }

    public boolean isExpired() {
        return LocalDate.now().isAfter(expiryDate);
    }

    public boolean isExpiringSoon() {
        return !isExpired() && LocalDate.now().plusDays(7).isAfter(expiryDate);
    }

    // Getters
    public int getId()            { return id; }
    public String getName()       { return name; }
    public int getAge()           { return age; }
    public String getContact()    { return contact; }
    public String getEmail()      { return email; }
    public String getGender()     { return gender; }
    public String getPlan()       { return plan; }
    public LocalDate getJoinDate()   { return joinDate; }
    public LocalDate getExpiryDate() { return expiryDate; }
    public boolean isFeePaid()    { return feePaid; }

    // Setters for editing
    public void setName(String name)       { this.name = name; }
    public void setAge(int age)            { this.age = age; }
    public void setContact(String contact) { this.contact = contact; }
    public void setEmail(String email)     { this.email = email; }
    public void setGender(String gender)   { this.gender = gender; }
    public void setPlan(String plan) {
        this.plan = plan;
        this.expiryDate = calculateExpiry(plan);
    }
    public void setFeePaid(boolean feePaid) { this.feePaid = feePaid; }

    public static void setCounter(int value) { counter = value; }

    @Override
    public String toString() {
        return "[" + id + "] " + name + " | " + plan + " | Expires: " + expiryDate;
    }
}
