package homer.tastyworld.frontend.starterpack.api;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public record PhotoResponse(String abstractPath, String hashSHA256, byte[] imageBytes) {

    public InputStream getPhoto() {
        return new ByteArrayInputStream(imageBytes);
    }

}
