package homer.tastyworld.frontend.starterpack.utils.managers.cache;

import homer.tastyworld.frontend.starterpack.base.config.AppConfig;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public abstract class CacheableFunction<T, R> implements CacheManager.ManageableCacheProcessor, Function<T, R> {

    protected final Map<T, R> cache = new ConcurrentHashMap<>();

    public final boolean isCached(T t) {
        return cache.containsKey(t);
    }

    public final R applyWithCache(T t) {
        return AppConfig.isAppCacheAvailable() ? cache.computeIfAbsent(t, this::apply) : apply(t);
    }

    public final void removeIfCached(T t) {
        cache.remove(t);
    }

    public final void cache(T t, R r) {
        if (AppConfig.isAppCacheAvailable()) {
            cache.putIfAbsent(t, r);
        }
    }

    public final void cache(T t) {
        if (AppConfig.isAppCacheAvailable()) {
            cache.computeIfAbsent(t, this::apply);
        }
    }

    @Override
    public final void clean() {
        cache.clear();
    }

}
