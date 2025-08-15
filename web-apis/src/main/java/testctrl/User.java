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
    public String role;

    /**
     * Checks a user's password by generating the MD5 hash of the provided password
     * and comparing it to the stored password hash.
     * @param pwd The password to check.
     * @throws NoSuchAlgorithmException
     */
    void checkPwd(String pwd) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] md5Digest = md.digest(pwd.getBytes(StandardCharsets.UTF_8));
        String pwdHash = new BigInteger(1, md5Digest).toString(16);
        Servlet.checkTrue(pwd_hash.equals(pwdHash), "Invalid password for restricted access!");
    }
}