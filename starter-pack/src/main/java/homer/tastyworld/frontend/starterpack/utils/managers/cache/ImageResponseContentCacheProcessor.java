package homer.tastyworld.frontend.starterpack.utils.managers.cache;

import homer.tastyworld.frontend.starterpack.api.sra.ImageResponse;
import homer.tastyworld.frontend.starterpack.base.AppLogger;
import homer.tastyworld.frontend.starterpack.base.config.AppConfig;
import homer.tastyworld.frontend.starterpack.utils.misc.FileDirectories;
import homer.tastyworld.frontend.starterpack.utils.misc.SHA256;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

public final class ImageResponseContentCacheProcessor implements CacheManager.ManageableCacheProcessor {

    private static final AppLogger logger = AppLogger.getFor(ImageResponseContentCacheProcessor.class);
    private static final File cachedImagesDir = new File(AppConfig.APP_DATA_DIR, "CachedPhotos");

    ImageResponseContentCacheProcessor() {}

    private Optional<InputStream> getContentIfCached(String abstractPath, String expectedHashSHA256) {
        if (!AppConfig.isAppCacheAvailable()) {
            return Optional.empty();
        }

        File actualFile = new File(cachedImagesDir, abstractPath);
        if (!actualFile.isFile()) {
            return Optional.empty();
        }

        String actualHashSHA256 = null;
        try {
            actualHashSHA256 = SHA256.hash(actualFile);
        } catch (Exception ex) {
            logger.warn("Can't get SHA-256 hash from cached image", ex);
        }

        if (expectedHashSHA256.equals(actualHashSHA256)) {
            try {
                return Optional.of(new FileInputStream(actualFile));
            } catch (Exception ex) {
                logger.warn("Can't get content from cached image", ex);
            }
        }

        return Optional.empty();
    }

    public void cache(InputStream content, String abstractPath) {
        if (!AppConfig.isAppCacheAvailable()) {
            return;
        }

        File file = new File(cachedImagesDir, abstractPath);
        FileDirectories.createDir(file.getParentFile());
        try {
            Files.copy(content, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception ex) {
            logger.warn("Can't cache (save) image content", ex);
        }
    }

    public InputStream cacheByRequest(String imageEndpoint) {
        ImageResponse imageResponse = ImageResponse.request(imageEndpoint);

        if (AppConfig.isAppCacheAvailable()) {
            cache(imageResponse.getContent(), imageResponse.info.abstractPath());
        }

        return imageResponse.getContent();
    }

    public InputStream get(String abstractPath, String expectedHashSHA256, String imageEndpoint) {
        return getContentIfCached(abstractPath, expectedHashSHA256).orElseGet(() -> cacheByRequest(imageEndpoint));
    }

    public InputStream get(String infoEndpoint, String imageEndpoint) {
        ImageResponse.Info info = ImageResponse.requestInfo(infoEndpoint);
        return get(info.abstractPath(), info.hashSHA256(), imageEndpoint);
    }

    @Override
    public void clean() {
        FileDirectories.cleanDir(cachedImagesDir);
    }

}
