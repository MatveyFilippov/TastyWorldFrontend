package homer.tastyworld.frontend.starterpack.api.sra.entity.order.names;

import homer.tastyworld.frontend.starterpack.utils.managers.cache.CacheManager;
import homer.tastyworld.frontend.starterpack.utils.managers.cache.CacheableFunction;
import java.util.Optional;

public class NumericalNameController extends NameController {

    private final CacheableFunction<Integer, String> cacheableGetCurrentName = CacheManager.getForFunction(String::valueOf);
    public final int min;
    public final int max;
    private int current;

    public NumericalNameController(int min, int max) {
        this.min = min;
        this.max = max;
        current = max;
    }

    private String getCurrentName() {
        return cacheableGetCurrentName.applyWithCache(current);
    }

    private void next() {
        current++;
        if (current > max) {
            current = min;
        }
    }

    @Override
    public void backIfCurrent(String name) {
        if (getCurrentName().equals(name)) {
            current--;
            if (current < min) {
                current = max;
            }
        }
    }

    @Override
    protected Optional<String> tryToFindNextName() {
        for (int i = min; i <= max; i++) {
            next();
            String result = getCurrentName();
            if (isAvailable(result)) {
                return Optional.of(result);
            }
        }
        return Optional.empty();
    }

}
