import java.io.Serializable;

public class Staff implements Serializable {
    private static final long serialVersionUID = 2L;

    private static int counter = 1;

    private int id;
    private String name;
    private String role;
    private String contact;
    private String email;
    private double salary;

    public Staff(String name, String role, String contact, String email, double salary) {
        this.id      = counter++;
        this.name    = name;
        this.role    = role;
        this.contact = contact;
        this.email   = email;
        this.salary  = salary;
    }

    // Getters
    public int getId()          { return id; }
    public String getName()     { return name; }
    public String getRole()     { return role; }
    public String getContact()  { return contact; }
    public String getEmail()    { return email; }
    public double getSalary()   { return salary; }

    // Setters for editing
    public void setName(String name)       { this.name = name; }
    public void setRole(String role)       { this.role = role; }
    public void setContact(String contact) { this.contact = contact; }
    public void setEmail(String email)     { this.email = email; }
    public void setSalary(double salary)   { this.salary = salary; }

    public static void setCounter(int value) { counter = value; }

    @Override
    public String toString() {
        return "[" + id + "] " + name + " (" + role + ")";
    }
