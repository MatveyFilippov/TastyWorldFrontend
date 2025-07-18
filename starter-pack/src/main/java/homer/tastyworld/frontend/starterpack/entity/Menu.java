package homer.tastyworld.frontend.starterpack.entity;

import homer.tastyworld.frontend.starterpack.api.PhotoRequest;
import homer.tastyworld.frontend.starterpack.api.Request;
import homer.tastyworld.frontend.starterpack.base.utils.managers.cache.CacheManager;
import homer.tastyworld.frontend.starterpack.base.utils.managers.cache.CacheProcessor;
import homer.tastyworld.frontend.starterpack.base.utils.misc.TypeChanger;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.apache.hc.core5.http.Method;
import java.io.InputStream;
import java.util.Map;

@Getter
@EqualsAndHashCode(of = {"id"})
@Builder(builderMethodName = "internalBuilder", access = AccessLevel.PRIVATE)
public class Menu {

    private static final Request ENTITY_REQUEST = new Request("/menu/read", Method.GET);
    private static final PhotoRequest PHOTO_REQUEST = new PhotoRequest("/menu/get_photo");
    public static final CacheProcessor<Long, Menu> CACHE = CacheManager.register(Menu::readFromBackend);

    private final long id;
    private final String name;
    private final boolean isActive;
    private final long[] productIDs;

    public InputStream getPhoto() {
        PHOTO_REQUEST.putInBody("id", id);
        return PHOTO_REQUEST.read();
    }

    public static Menu readFromBackend(long id) {
        ENTITY_REQUEST.putInBody("id", id);
        Map<String, Object> productInfo = ENTITY_REQUEST.request().getResultAsJSON();
        return Menu
                .internalBuilder()
                .id(id)
                .name((String) productInfo.get("NAME"))
                .isActive(TypeChanger.toBool(productInfo.get("IS_ACTIVE")))
                .productIDs(TypeChanger.toSortedPrimitiveLongArray(productInfo.get("PRODUCT_IDs")))
                .build();
    }

    public static Menu get(long id) {
        return CACHE.get(id);
    }

}
