package homer.tastyworld.frontend.starterpack.base.utils.managers.cache;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public class CacheManager {

    private static final Set<CacheProcessor<?, ?>> caches = new HashSet<>();
    private static boolean isCacheAvailable = true;

    public static void setIsCacheAvailable(boolean isCacheAvailable) {
        CacheManager.isCacheAvailable = isCacheAvailable;
    }

    public static boolean isCacheAvailable() {
        return isCacheAvailable;
    }

    public static <K, V> void register(CacheProcessor<K, V> cache) {
        caches.add(cache);
    }

    public static <K, V> CacheProcessor<K, V> register(Function<? super K, ? extends V> mappingFunction) {
        CacheProcessor<K, V> cache = new CacheProcessor<K, V>() {

            @Override
            protected V compute(K key) {
                return mappingFunction.apply(key);
            }

        };
        register(cache);
        return cache;
    }

    private static void cleanAll() {
        caches.forEach(CacheProcessor::clean);
    }

}
