package homer.tastyworld.frontend.starterpack.base.entity.implementations;

import homer.tastyworld.frontend.starterpack.api.Request;
import homer.tastyworld.frontend.starterpack.base.entity.EntityWithPhoto;
import homer.tastyworld.frontend.starterpack.base.utils.managers.cache.CacheManager;
import homer.tastyworld.frontend.starterpack.base.utils.managers.cache.SimpleCacheProcessor;
import homer.tastyworld.frontend.starterpack.base.utils.misc.TypeChanger;
import org.apache.hc.core5.http.Method;
import java.util.Map;

public class Menu extends EntityWithPhoto {

    private static final Request REQUEST = new Request("/menu/read", Method.GET);
    public static final SimpleCacheProcessor<Long, Menu> CACHE = CacheManager.register(Menu::readFromBackend);

    public final long id;
    public final String name;
    public final boolean isActive;
    public final long[] productIDs;

    Menu(long id, String name, boolean isActive, long[] productIDs) {
        this.id = id;
        this.name = name;
        this.isActive = isActive;
        this.productIDs = productIDs;
    }

    public static Menu readFromBackend(long id) {
        REQUEST.putInBody("id", id);
        Map<String, Object> productInfo = REQUEST.request().getResultAsJSON();
        return new Menu(
                TypeChanger.toLong(productInfo.get("ID")),
                (String) productInfo.get("NAME"),
                TypeChanger.toBool(productInfo.get("IS_ACTIVE")),
                TypeChanger.toPrimitiveLongArray(productInfo.get("PRODUCT_IDs"))
        );
    }

    public static Menu get(long id) {
        return CACHE.get(id);
    }

}
