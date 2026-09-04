package testctrl;

import testctrl.testmgmt.UHeader;

/**
 * Class representing a user in the system.
 */
public class User implements Comparable<User> {
    public String username;
    public String first_name;
    public String last_name;
    public String aka_name;
    public String roles;
    public String tags;

    /**
     * Checks if a user has a specific role.
     * @return true if the user has the role, false otherwise.
     */
    boolean hasRole(String... uRoles) {
        for (String role : uRoles) {
            if (roles.contains(role)) {
                return true;
            }
        }
        return false;
    }

    public UHeader getUHeader() {
        return new UHeader(username, first_name, last_name, roles, tags);
    }

    // #region: Comparable overrides
    @Override
    public int compareTo(User o) {
        return username.compareTo(o.username);
    }

    @Override
    public boolean equals(Object obj) {
        return (this == obj || obj instanceof User && this.compareTo((User) obj) == 0);
    }

    @Override
    public int hashCode() {
        return username.hashCode();
    }
    // #endregion: comparable overrides
}