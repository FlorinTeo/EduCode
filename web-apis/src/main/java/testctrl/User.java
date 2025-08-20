package testctrl;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Class representing a user in the system.
 */
public class User implements Comparable<User> {
    public String name;
    public String pwd_hash;
    public String roles;

    /**
     * Creates a MD5 hash of the given password.
     * @param pwd The password to hash.
     * @return The MD5 hash of the password.
     * @throws NoSuchAlgorithmException
     */
    private String getPwdHash(String pwd) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] md5Digest = md.digest(pwd.getBytes(StandardCharsets.UTF_8));
        return new BigInteger(1, md5Digest).toString(16);
    }

    /**
     * Checks a user's password by generating the MD5 hash of the provided password
     * and comparing it to the stored password hash.
     * @param pwd The password to check.
     * @throws NoSuchAlgorithmException
     */
    boolean matchesPwd(String pwd) throws NoSuchAlgorithmException {
        return pwd_hash.equals(getPwdHash(pwd));
    }

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

    /**
     * Sets the password for the user.
     * @param pwd The new password.
     * @return always true.
     * @throws NoSuchAlgorithmException
     */
    boolean setPwd(String pwd) throws NoSuchAlgorithmException {
        pwd_hash = getPwdHash(pwd);
        return true; // Password changed
    }

    // #region: Comparable overrides
    @Override
    public int compareTo(User o) {
        return name.compareTo(o.name);
    }

    @Override
    public boolean equals(Object obj) {
        return (this == obj || obj instanceof User && this.compareTo((User) obj) == 0);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
    // #endregion: comparable overrides
}