package homer.tastyworld.frontend.starterpack.base.utils.managers.cache;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class CacheProcessor<K, V> extends CacheManager.CleanableCacheProcessor {

    protected final Map<K, V> cache = new ConcurrentHashMap<>();

    protected abstract V compute(K key);

    public V get(K key) {
        return CacheManager.isCacheAvailable() ? cache.computeIfAbsent(key, this::compute) : compute(key);
    }

    public void removeIfExists(K key) {
        cache.remove(key);
    }

    public void cache(K key) {
        if (CacheManager.isCacheAvailable()) {
            cache.put(key, compute(key));
        }
    }

    public void cacheIfAbsent(K key) {
        if (!cache.containsKey(key)) {
            cache(key);
        }
    }

    public void cache(K[] keys) {
        Arrays.stream(keys).forEach(this::cache);
    }

    public void cacheIfAbsent(K[] keys) {
        Arrays.stream(keys).forEach(this::cacheIfAbsent);
    }

    @Override
    public void clean() {
        cache.clear();
    }

}
