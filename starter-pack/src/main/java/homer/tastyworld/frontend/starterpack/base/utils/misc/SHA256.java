package homer.tastyworld.frontend.starterpack.base.utils.misc;

import homer.tastyworld.frontend.starterpack.base.exceptions.starterpackonly.init.CantInitSHA256;
import homer.tastyworld.frontend.starterpack.base.utils.managers.cache.CacheManager;
import homer.tastyworld.frontend.starterpack.base.utils.managers.cache.CacheProcessor;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class SHA256 {

    private static final MessageDigest DIGEST;
    private static final CacheProcessor<String, String> cache = CacheManager.register(
            decrypted -> bytes2string(hashBytes(string2bites(decrypted)))
    );

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
        return cache.get(input);
    }

    public static String hash(File file) throws IOException {
        DIGEST.reset();
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                DIGEST.update(buffer, 0, bytesRead);
            }
        }
        StringBuilder hash = new StringBuilder();
        for (byte b : DIGEST.digest()) {
            hash.append(String.format("%02x", b));
        }
        return hash.toString();
    }

}
