package homer.tastyworld.frontend.pos.processor.core.cache;

import homer.tastyworld.frontend.starterpack.api.Request;
import homer.tastyworld.frontend.starterpack.base.utils.managers.cache.CacheProcessor;
import org.apache.hc.core5.http.Method;
import java.util.Map;

public class AdditivesCache extends CacheProcessor<Long, Map<String, Object>> {

    private static final Request request = new Request("/product/read_additive", Method.GET);
    public static final CacheProcessor<Long, Map<String, Object>> impl = new AdditivesCache();

    @Override
    protected Map<String, Object> compute(Long additiveID) {
        request.putInBody("id", additiveID);
        return request.request().getResultAsJSON();
    }

}
