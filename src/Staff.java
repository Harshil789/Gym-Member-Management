import java.io.Serializable;

public class Staff implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private String role;

    public Staff(String name, String role) {
        this.name = name;
        this.role = role;
    }

    public String getName() { return name; }
    public String getRole() { return role; }
}
