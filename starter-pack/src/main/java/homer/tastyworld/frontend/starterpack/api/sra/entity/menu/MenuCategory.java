package homer.tastyworld.frontend.starterpack.api.sra.entity.menu;

import homer.tastyworld.frontend.starterpack.api.engine.Request;
import homer.tastyworld.frontend.starterpack.api.engine.Requester;
import homer.tastyworld.frontend.starterpack.api.sra.tools.RequestUtils;
import homer.tastyworld.frontend.starterpack.utils.managers.cache.CacheManager;
import homer.tastyworld.frontend.starterpack.utils.managers.cache.CacheableFunction;
import homer.tastyworld.frontend.starterpack.utils.managers.cache.ImageResponseContentCacheProcessor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.apache.hc.core5.http.Method;
import org.jetbrains.annotations.Nullable;
import java.io.InputStream;

@Getter
@EqualsAndHashCode(of = {"categoryID"})
public final class MenuCategory {

    record GetCategoryResponseBody(
            Long menu_category_id,
            Long client_point_id,
            String name,
            Boolean is_active
    ) {}

    private record CreateCategoryRequestBody(
            String name,
            @Nullable Boolean is_active
    ) {}

    private record EditCategoryRequestBody(
            @Nullable String name,
            @Nullable Boolean is_active
    ) {}

    private static final CacheableFunction<Long, MenuCategory> cacheableGetCategory = CacheManager.getForFunction(MenuCategory::get);
    private static final ImageResponseContentCacheProcessor imageCache = CacheManager.getForImageResponseContent();

    private final long categoryID;
    private String name;
    private Boolean isActive;

    private MenuCategory(long categoryID) {
        this.categoryID = categoryID;
    }

    private void set(GetCategoryResponseBody getCategoryResponseBody) {
        this.name = getCategoryResponseBody.name();
        this.isActive = getCategoryResponseBody.is_active();
    }

    public InputStream getImage() {
        return imageCache.get("/menu/categories/%s/image/info".formatted(categoryID), "/menu/categories/%s/image".formatted(categoryID));
    }

    private void edit(EditCategoryRequestBody editCategoryRequestBody) {
        Request editOrderRequest = Request.of(
                RequestUtils.getURI("/menu/categories/" + categoryID), Method.PATCH, RequestUtils.getBearerAuthorization(),
                editCategoryRequestBody
        );
        GetCategoryResponseBody editOrderResponseBody = Requester.exchange(
                editOrderRequest,
                response -> RequestUtils.processEntityResponse(response, 200, GetCategoryResponseBody.class)
        );
        set(editOrderResponseBody);
    }

    public void setName(String name) {
        EditCategoryRequestBody editCategoryRequestBody = new EditCategoryRequestBody(
                name, null
        );
        edit(editCategoryRequestBody);
    }

    public void setIsActive(boolean isActive) {
        EditCategoryRequestBody editCategoryRequestBody = new EditCategoryRequestBody(
                null, isActive
        );
        edit(editCategoryRequestBody);
    }

    static MenuCategory of(GetCategoryResponseBody getCategoryResponseBody) {
        MenuCategory result = new MenuCategory(getCategoryResponseBody.menu_category_id());
        result.set(getCategoryResponseBody);
        return result;
    }

    public static MenuCategory get(long categoryID) {
        Request getCategoryRequest = Request.of(
                RequestUtils.getURI("/menu/categories/" + categoryID), Method.GET, RequestUtils.getBearerAuthorization()
        );
        GetCategoryResponseBody getCategoryResponseBody = Requester.exchange(
                getCategoryRequest,
                response -> RequestUtils.processEntityResponse(response, 200, GetCategoryResponseBody.class)
        );
        return of(getCategoryResponseBody);
    }

    public static MenuCategory create(String name, @Nullable Boolean isActive) {
        CreateCategoryRequestBody createCategoryRequestBody = new CreateCategoryRequestBody(
                name, isActive
        );
        Request createCategoryRequest = Request.of(
                RequestUtils.getURI("/menu/categories/"), Method.POST, RequestUtils.getBearerAuthorization(), createCategoryRequestBody
        );
        GetCategoryResponseBody createCategoryResponseBody = Requester.exchange(
                createCategoryRequest,
                response -> RequestUtils.processEntityResponse(response, 201, GetCategoryResponseBody.class)
        );
        return of(createCategoryResponseBody);
    }

}
