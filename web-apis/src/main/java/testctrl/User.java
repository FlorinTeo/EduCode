package testctrl;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Class representing a user in the system.
 */
public class User {
    public String name;
    public String pwd_hash;
    public String roles;

    /**
     * Checks a user's password by generating the MD5 hash of the provided password
     * and comparing it to the stored password hash.
     * @param pwd The password to check.
     * @throws NoSuchAlgorithmException
     */
    boolean matchesPwd(String pwd) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] md5Digest = md.digest(pwd.getBytes(StandardCharsets.UTF_8));
        String pwdHash = new BigInteger(1, md5Digest).toString(16);
        return pwd_hash.equals(pwdHash);
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
}