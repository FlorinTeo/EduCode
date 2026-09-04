package testctrl.testmgmt;

public class UHeader {
    public String username;
    public String first_name;
    public String last_name;
    public String roles;
    public String tags;
    
    public UHeader(String username, String first_name, String last_name, String roles, String tags) {
        this.username = username;
        this.first_name = first_name;
        this.last_name = last_name;
        this.roles = roles;
        this.tags = tags;
    }
}
