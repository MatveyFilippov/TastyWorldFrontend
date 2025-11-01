package homer.tastyworld.frontend.starterpack.utils.managers.cache;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public class CacheManager {

    public interface ManageableCacheProcessor {

        void clean();

    }

    private static final Set<ManageableCacheProcessor> caches = new HashSet<>();
    private static ImageResponseContentCacheProcessor imageResponseContentCacheProcessor;

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

    public static ImageResponseContentCacheProcessor getForImageResponseContent() {
        if (imageResponseContentCacheProcessor == null) {
            imageResponseContentCacheProcessor = new ImageResponseContentCacheProcessor();
            register(imageResponseContentCacheProcessor);
        }
        return imageResponseContentCacheProcessor;
    }

    public static void cleanAll() {
        caches.forEach(ManageableCacheProcessor::clean);
    }

}
