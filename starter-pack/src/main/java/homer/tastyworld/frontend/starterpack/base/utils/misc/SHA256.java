package homer.tastyworld.frontend.starterpack.base.utils.misc;

import homer.tastyworld.frontend.starterpack.base.exceptions.starterpackonly.init.CantInitSHA256;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class SHA256 {

    private static final MessageDigest DIGEST;
    private static final Map<String, String> cache = new HashMap<>();

    static {
        try {
            DIGEST = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException ex) {
            throw new CantInitSHA256(ex);
        }
    }

    private static byte[] string2bites(String input) {
        return input.getBytes();
    }

    private static byte[] hashBytes(byte[] input) {
        return DIGEST.digest(input);
    }

    private static String bytes2string(byte[] input) {
        StringBuilder output = new StringBuilder();
        for (byte b : input) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                output.append('0');
            }
            output.append(hex);
        }
        return output.toString();
    }

    public static String hash(String input) {
        return cache.computeIfAbsent(input, decrypted -> bytes2string(hashBytes(string2bites(decrypted))));
    }

}
