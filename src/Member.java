import java.io.Serializable;

public class Member implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name, contact, email, gender, plan;
    private int age;

    public Member(String name, int age, String contact, String email, String gender, String plan) {
        this.name = name;
        this.age = age;
        this.contact = contact;
        this.email = email;
        this.gender = gender;
        this.plan = plan;
    }

    public String getName() { return name; }
    public int getAge() { return age; }
    public String getContact() { return contact; }
    public String getEmail() { return email; }
    public String getGender() { return gender; }
    public String getPlan() { return plan; }
}
