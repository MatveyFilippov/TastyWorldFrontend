package homer.tastyworld.frontend.starterpack.base.utils.managers.cache;

public abstract class CleanableCacheProcessor {

    public CleanableCacheProcessor() {
        CacheManager.register(this);
    }

    public abstract void clean();

}
