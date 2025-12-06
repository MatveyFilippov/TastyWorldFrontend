package homer.tastyworld.frontend.starterpack.utils.managers.cache;

import homer.tastyworld.frontend.starterpack.api.sra.FileResponse;
import homer.tastyworld.frontend.starterpack.base.AppLogger;
import homer.tastyworld.frontend.starterpack.base.config.AppConfig;
import homer.tastyworld.frontend.starterpack.utils.misc.FileDirectories;
import homer.tastyworld.frontend.starterpack.utils.misc.SHA256;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

public final class FileResponseContentCacheProcessor implements CacheManager.ManageableCacheProcessor {

    private static final AppLogger logger = AppLogger.getFor(FileResponseContentCacheProcessor.class);
    private static final File cachedFilesDir = new File(AppConfig.APP_DATA_DIR, "CachedFiles");

    FileResponseContentCacheProcessor() {}

    private Optional<InputStream> getContentIfCached(String abstractPath, String expectedHashSHA256) {
        if (!AppConfig.isAppCacheAvailable()) {
            return Optional.empty();
        }

        File actualFile = new File(cachedFilesDir, abstractPath);
        if (!actualFile.isFile()) {
            return Optional.empty();
        }

        String actualHashSHA256 = null;
        try {
            actualHashSHA256 = SHA256.hash(actualFile);
        } catch (Exception ex) {
            logger.warn("Can't get SHA-256 hash of cached file", ex);
        }

        if (expectedHashSHA256.equals(actualHashSHA256)) {
            try {
                return Optional.of(new FileInputStream(actualFile));
            } catch (Exception ex) {
                logger.warn("Can't get content of cached file", ex);
            }
        }

        return Optional.empty();
    }

    public void cache(InputStream content, String abstractPath) {
        if (!AppConfig.isAppCacheAvailable()) {
            return;
        }

        File file = new File(cachedFilesDir, abstractPath);
        FileDirectories.createDir(file.getParentFile());
        try {
            Files.copy(content, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception ex) {
            logger.warn("Can't cache (save) file content", ex);
        }
    }

    public InputStream cacheByRequest(String getFileEndpoint) {
        FileResponse fileResponse = FileResponse.request(getFileEndpoint);

        if (AppConfig.isAppCacheAvailable()) {
            cache(fileResponse.getContent(), fileResponse.info.abstractPath());
        }

        return fileResponse.getContent();
    }

    public InputStream get(String abstractPath, String expectedHashSHA256, String getFileEndpoint) {
        return getContentIfCached(abstractPath, expectedHashSHA256).orElseGet(() -> cacheByRequest(getFileEndpoint));
    }

    public InputStream get(String getFileInfoEndpoint, String getFileEndpoint) {
        FileResponse.Info info = FileResponse.requestInfo(getFileInfoEndpoint);
        return get(info.abstractPath(), info.hashSHA256(), getFileEndpoint);
    }

    @Override
    public void clean() {
        FileDirectories.cleanDir(cachedFilesDir);
    }

}
