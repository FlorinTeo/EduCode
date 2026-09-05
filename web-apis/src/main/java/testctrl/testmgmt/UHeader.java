package testctrl.testmgmt;

public class UHeader {
    public String display_name;
    public String[] roles;
    public String[] tags;
    
    public UHeader(String first_name, String last_name, String aka_name, String roles, String tags) {
        display_name = (aka_name == null || aka_name.isEmpty())
            ? aka_name
            : String.format("%s %s", first_name, last_name);
        this.roles = roles.split("\\|");
        this.tags = (tags != null) ?  tags.split("\\|") : new String[0];
    }
}
