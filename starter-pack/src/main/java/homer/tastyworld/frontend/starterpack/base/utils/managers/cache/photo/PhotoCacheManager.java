package homer.tastyworld.frontend.starterpack.base.utils.managers.cache.photo;

import homer.tastyworld.frontend.starterpack.api.PhotoRequest;
import homer.tastyworld.frontend.starterpack.api.PhotoResponse;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class PhotoCacheManager {

    private static final PhotoCacheProcessor PHOTO_CACHE_PROCESSOR = new PhotoCacheProcessor();

    private static boolean isCached(String remoteSHA256Hash, String abstractPath) {
        String localSHA256Hash = null;
        try {
            localSHA256Hash = PHOTO_CACHE_PROCESSOR.getSHA256Hash(abstractPath);
        } catch (FileNotFoundException ignored) {}
        return remoteSHA256Hash.equals(localSHA256Hash);
    }

    private static InputStream get(String remoteSHA256Hash, String abstractPath) {
        if (isCached(remoteSHA256Hash, abstractPath)) {
            try {
                return PHOTO_CACHE_PROCESSOR.getPhoto(abstractPath);
            } catch (FileNotFoundException ignored) {}
        }
        return null;
    }

    public static InputStream readAndSave(PhotoRequest request) {
        PhotoResponse response = request.request();
        PHOTO_CACHE_PROCESSOR.savePhoto(response.getPhoto(), response.abstractPath());
        return response.getPhoto();
    }

    public static InputStream get(String originSHA256Hash, String abstractPath, PhotoRequest request) {
        InputStream photo = get(originSHA256Hash, abstractPath);
        if (photo == null) {
            photo = readAndSave(request);
        }
        return photo;
    }

}
