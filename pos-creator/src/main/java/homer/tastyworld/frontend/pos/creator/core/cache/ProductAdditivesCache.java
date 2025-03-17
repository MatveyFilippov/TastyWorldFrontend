package homer.tastyworld.frontend.pos.creator.core.cache;

import homer.tastyworld.frontend.starterpack.base.utils.managers.cache.CacheProcessor;
import homer.tastyworld.frontend.starterpack.base.utils.misc.TypeChanger;
import java.util.Map;

public class ProductAdditivesCache extends CacheProcessor<Long, Long[]> {

    public static final CacheProcessor<Long, Long[]> impl = new ProductAdditivesCache();

    @Override
    protected Long[] compute(Long productID) {
        Map<String, Object> productInfo = ProductsCache.impl.get(productID);
        return TypeChanger.toSortedLongArray(productInfo.get("ADDITIVE_IDs"));
    }

}
