package homer.tastyworld.frontend.pos.creator.core.cache;

import homer.tastyworld.frontend.starterpack.api.Request;
import homer.tastyworld.frontend.starterpack.base.utils.managers.cache.CacheProcessor;
import org.apache.hc.core5.http.Method;
import java.util.Map;

public class MenuCache extends CacheProcessor<Long, Map<String, Object>> {

    public static final CacheProcessor<Long, Map<String, Object>> impl = new MenuCache();
    private static final Request request = new Request("/menu/read", Method.GET);

    @Override
    protected Map<String, Object> compute(Long menuID) {
        request.putInBody("id", menuID);
        return request.request().getResultAsJSON();
    }

}
