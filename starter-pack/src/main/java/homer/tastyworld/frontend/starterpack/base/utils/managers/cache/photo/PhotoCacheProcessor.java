package homer.tastyworld.frontend.starterpack.base.utils.managers.cache.photo;

import homer.tastyworld.frontend.starterpack.base.AppLogger;
import homer.tastyworld.frontend.starterpack.base.config.AppConfig;
import homer.tastyworld.frontend.starterpack.base.utils.managers.cache.CacheManager;
import homer.tastyworld.frontend.starterpack.base.utils.misc.FileDirectories;
import homer.tastyworld.frontend.starterpack.base.utils.misc.SHA256;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

class PhotoCacheProcessor extends CacheManager.CleanableCacheProcessor {

    private static final AppLogger logger = AppLogger.getFor(PhotoCacheProcessor.class);
    protected static final File CACHED_PHOTOS_DIR = new File(AppConfig.APP_DATA_DIR, "CachedPhotos");

    static {
        if (CacheManager.isCacheAvailable()) {
            FileDirectories.create(CACHED_PHOTOS_DIR);
        }
    }

    protected static File getFileIfExistsElseThrowException(String abstractPath) throws FileNotFoundException {
        if (CacheManager.isCacheAvailable()) {
            File file = new File(CACHED_PHOTOS_DIR, abstractPath);
            if (file.isFile()) {
                return file;
            }
        }
        throw new FileNotFoundException(
                "The photo with abstract path '%s' is not present in the cached photo directory".formatted(abstractPath)
        );
    }

    public void savePhoto(InputStream photo, String abstractPath) {
        if (!CacheManager.isCacheAvailable()) {
            return;
        }
        File file = new File(CACHED_PHOTOS_DIR, abstractPath);
        FileDirectories.create(file.getParentFile());
        try {
            Files.copy(photo, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            logger.errorOnlyServerNotify("Can't cache (save) photo", ex);
        }
    }

    public InputStream getPhoto(String abstractPath) throws FileNotFoundException {
        return new FileInputStream(getFileIfExistsElseThrowException(abstractPath));
    }

    public String getSHA256Hash(String abstractPath) throws FileNotFoundException {
        File file = getFileIfExistsElseThrowException(abstractPath);
        try {
            return SHA256.hash(file);
        } catch (IOException ex) {
            logger.errorOnlyServerNotify("Can't get SHA256 hash from cached photo", ex);
        }
        throw new FileNotFoundException("Can't get SHA256 hash from cached photo");
    }

    @Override
    public void clean() {
        FileDirectories.clean(CACHED_PHOTOS_DIR);
    }

}
