package homer.tastyworld.frontend.starterpack.utils.misc;

import homer.tastyworld.frontend.starterpack.base.exceptions.starterpackonly.initialization.CantInitSHA256;
import homer.tastyworld.frontend.starterpack.utils.managers.cache.CacheManager;
import homer.tastyworld.frontend.starterpack.utils.managers.cache.CacheableFunction;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA256 {

    private static final MessageDigest DIGEST;
    private static final CacheableFunction<String, String> cacheableHashStr = CacheManager.getForFunction(SHA256::hashWithoutCache);

    static {
        try {
            DIGEST = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException ex) {
            throw new CantInitSHA256(ex);
        }
    }

    public static byte[] hash(byte[] input) {
        return DIGEST.digest(input);
    }

    private static String hashWithoutCache(String input) {
        byte[] hash = hash(input.getBytes());
        StringBuilder output = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                output.append('0');
            }
            output.append(hex);
        }
        return output.toString();
    }

    public static String hash(String input) {
        return cacheableHashStr.applyWithCache(input);
    }

    public static String hash(File input) throws IOException {
        DIGEST.reset();
        try (FileInputStream fis = new FileInputStream(input)) {
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
