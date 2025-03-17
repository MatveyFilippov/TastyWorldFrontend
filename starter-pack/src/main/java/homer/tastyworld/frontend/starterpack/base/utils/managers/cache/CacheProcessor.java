package homer.tastyworld.frontend.starterpack.base.utils.managers.cache;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class CacheProcessor<K, V> {

    protected final Map<K, V> cache = new ConcurrentHashMap<>();

    protected CacheProcessor() {
        CacheManager.register(this);
    }

    protected abstract V compute(K key);

    public V get(K key) {
        return cache.computeIfAbsent(key, this::compute);
    }

    public void cache(K key) {
        cache.put(key, compute(key));
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

    public void clean() {
        cache.clear();
    }

}
