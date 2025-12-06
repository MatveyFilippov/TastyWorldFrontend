package homer.tastyworld.frontend.starterpack.utils.managers.cache;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public class CacheManager {

    public interface ManageableCacheProcessor {

        void clean();

    }

    private static final Set<ManageableCacheProcessor> caches = new HashSet<>();
    private static FileResponseContentCacheProcessor fileResponseContentCacheProcessor;

    public static void register(ManageableCacheProcessor cache) {
        caches.add(cache);
    }

    public static <T, R> CacheableFunction<T, R> getForFunction(Function<? super T, ? extends R> cacheableFunction) {
        CacheableFunction<T, R> newCacheableFunction = new CacheableFunction<>() {

            @Override
            public R apply(T key) {
                return cacheableFunction.apply(key);
            }

        };
        register(newCacheableFunction);
        return newCacheableFunction;
    }

    public static FileResponseContentCacheProcessor getForFileResponseContent() {
        if (fileResponseContentCacheProcessor == null) {
            fileResponseContentCacheProcessor = new FileResponseContentCacheProcessor();
            register(fileResponseContentCacheProcessor);
        }
        return fileResponseContentCacheProcessor;
    }

    public static void cleanAll() {
        caches.forEach(ManageableCacheProcessor::clean);
    }

}
