package homer.tastyworld.frontend.starterpack.api.sra.entity.menu;

import homer.tastyworld.frontend.starterpack.api.engine.Request;
import homer.tastyworld.frontend.starterpack.api.engine.Requester;
import homer.tastyworld.frontend.starterpack.api.sra.entity.misc.ProductTax;
import homer.tastyworld.frontend.starterpack.api.sra.entity.misc.ProductType;
import homer.tastyworld.frontend.starterpack.api.sra.tools.RequestUtils;
import homer.tastyworld.frontend.starterpack.utils.managers.cache.FileResponseContentCacheProcessor;
import homer.tastyworld.frontend.starterpack.api.sra.entity.misc.MenuQuantitativeMeasure;
import homer.tastyworld.frontend.starterpack.utils.managers.cache.CacheManager;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.apache.hc.core5.http.Method;
import org.jetbrains.annotations.Nullable;
import java.io.InputStream;
import java.math.BigDecimal;

@Getter
@EqualsAndHashCode(of = {"productID"})
public final class Product {

    record GetProductResponseBody(
            Long product_id,
            Long client_point_id,
            String name,
            ProductType type,
            ProductTax tax,
            Boolean is_active,
            MenuQuantitativeMeasure qty_measure,
            Integer qty_min,
            Integer qty_max,
            Integer qty_default,
            BigDecimal unit_price
    ) {}

    private record CreateProductRequestBody(
            String name,
            ProductType type,
            ProductTax tax,
            MenuQuantitativeMeasure qty_measure,
            Integer qty_min,
            Integer qty_max,
            Integer qty_default,
            BigDecimal unit_price,
            @Nullable Boolean is_active
    ) {}

    private record EditProductRequestBody(
            @Nullable String name,
            @Nullable ProductType type,
            @Nullable ProductTax tax,
            @Nullable Integer qty_min,
            @Nullable Integer qty_max,
            @Nullable Integer qty_default,
            @Nullable BigDecimal unit_price,
            @Nullable Boolean is_active
    ) {}

    private static final FileResponseContentCacheProcessor imageCache = CacheManager.getForFileResponseContent();

    private final long productID;
    private String name;
    private ProductType type;
    private ProductTax tax;
    private Boolean isActive;
    private MenuQuantitativeMeasure qtyMeasure;
    private Integer qtyMin, qtyMax, qtyDefault;
    private BigDecimal unitPrice;

    private Product(long productID) {
        this.productID = productID;
    }

    private void set(GetProductResponseBody getProductResponseBody) {
        this.name = getProductResponseBody.name();
        this.type = getProductResponseBody.type();
        this.tax = getProductResponseBody.tax();
        this.isActive = getProductResponseBody.is_active();
        this.qtyMeasure = getProductResponseBody.qty_measure();
        this.qtyMin = getProductResponseBody.qty_min();
        this.qtyMax = getProductResponseBody.qty_max();
        this.qtyDefault = getProductResponseBody.qty_default();
        this.unitPrice = getProductResponseBody.unit_price();
    }

    public InputStream getImage() {
        return imageCache.get("/menu/products/%s/image/info".formatted(productID), "/menu/products/%s/image".formatted(productID));
    }

    private void edit(EditProductRequestBody editProductRequestBody) {
        Request editProductRequest = Request.of(
                RequestUtils.getURI("/menu/products/"), Method.PATCH, RequestUtils.getBearerAuthorization(), editProductRequestBody
        );
        GetProductResponseBody editProductResponseBody = Requester.exchange(
                editProductRequest,
                response -> RequestUtils.processEntityResponse(response, 200, GetProductResponseBody.class)
        );
        set(editProductResponseBody);
    }

    public void setIsActive(boolean isActive) {
        EditProductRequestBody editProductRequestBody = new EditProductRequestBody(
                null, null, null, null, null, null, null, isActive
        );
        edit(editProductRequestBody);
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        EditProductRequestBody editProductRequestBody = new EditProductRequestBody(
                null, null, null, null, null, null, unitPrice, null
        );
        edit(editProductRequestBody);
    }

    static Product of(GetProductResponseBody getProductResponseBody) {
        Product result = new Product(getProductResponseBody.product_id());
        result.set(getProductResponseBody);
        return result;
    }

    public static Product get(long productID) {
        Request getProductRequest = Request.of(
                RequestUtils.getURI("/menu/products/" + productID), Method.GET, RequestUtils.getBearerAuthorization()
        );
        GetProductResponseBody getProductResponseBody = Requester.exchange(
                getProductRequest,
                response -> RequestUtils.processEntityResponse(response, 200, GetProductResponseBody.class)
        );
        return of(getProductResponseBody);
    }

    public static Product create(String name, ProductType type, ProductTax tax, MenuQuantitativeMeasure qtyMeasure, Integer qtyMin, Integer qtyMax, Integer qtyDefault, BigDecimal unitPrice, @Nullable Boolean isActive) {
        CreateProductRequestBody createProductRequestBody = new CreateProductRequestBody(
                name, type, tax, qtyMeasure, qtyMin, qtyMax, qtyDefault, unitPrice, isActive
        );
        Request createProductRequest = Request.of(
                RequestUtils.getURI("/menu/products/"), Method.POST, RequestUtils.getBearerAuthorization(), createProductRequestBody
        );
        GetProductResponseBody createProductResponseBody = Requester.exchange(
                createProductRequest,
                response -> RequestUtils.processEntityResponse(response, 201, GetProductResponseBody.class)
        );
        return of(createProductResponseBody);
    }

}
