package homer.tastyworld.frontend.pos.creator.core.cache;

import homer.tastyworld.frontend.starterpack.api.Request;
import homer.tastyworld.frontend.starterpack.base.utils.managers.cache.CacheProcessor;
import org.apache.hc.core5.http.Method;
import java.util.Map;

public class ProductsCache extends CacheProcessor<Long, Map<String, Object>> {

    public static final CacheProcessor<Long, Map<String, Object>> impl = new ProductsCache();
    private static final Request request = new Request("/product/read", Method.GET);

    @Override
    protected Map<String, Object> compute(Long productID) {
        request.putInBody("id", productID);
        return request.request().getResultAsJSON();
    }

}
