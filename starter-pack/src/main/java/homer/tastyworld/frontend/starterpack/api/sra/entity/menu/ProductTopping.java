package homer.tastyworld.frontend.starterpack.api.sra.entity.menu;

import homer.tastyworld.frontend.starterpack.api.engine.Request;
import homer.tastyworld.frontend.starterpack.api.engine.Requester;
import homer.tastyworld.frontend.starterpack.api.sra.tools.RequestUtils;
import homer.tastyworld.frontend.starterpack.api.sra.entity.misc.MenuQuantitativeMeasure;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.apache.hc.core5.http.Method;
import org.jetbrains.annotations.Nullable;
import java.math.BigDecimal;

@Getter
@EqualsAndHashCode(of = {"productToppingID"})
public final class ProductTopping {

    record GetProductToppingResponseBody(
            Long product_topping_id,
            Long client_point_id,
            Long product_id,
            String name,
            Boolean is_active,
            MenuQuantitativeMeasure qty_measure,
            Integer qty_min,
            Integer qty_max,
            Integer qty_default,
            BigDecimal unit_price
    ) {}

    private record CreateProductToppingRequestBody(
            Long product_id,
            String name,
            MenuQuantitativeMeasure qty_measure,
            Integer qty_min,
            Integer qty_max,
            Integer qty_default,
            BigDecimal unit_price,
            @Nullable Boolean is_active
    ) {}

    private record EditProductToppingRequestBody(
            @Nullable String name,
            @Nullable Integer qty_min,
            @Nullable Integer qty_max,
            @Nullable Integer qty_default,
            @Nullable BigDecimal unit_price,
            @Nullable Boolean is_active
    ) {}

    private final long productToppingID, productID;
    private String name;
    private Boolean isActive;
    private MenuQuantitativeMeasure qtyMeasure;
    private Integer qtyMin, qtyMax, qtyDefault;
    private BigDecimal unitPrice;

    private ProductTopping(long productToppingID, long productID) {
        this.productToppingID = productToppingID;
        this.productID = productID;
    }

    private void set(GetProductToppingResponseBody getProductToppingResponseBody) {
        this.name = getProductToppingResponseBody.name();
        this.isActive = getProductToppingResponseBody.is_active();
        this.qtyMeasure = getProductToppingResponseBody.qty_measure();
        this.qtyMin = getProductToppingResponseBody.qty_min();
        this.qtyMax = getProductToppingResponseBody.qty_max();
        this.qtyDefault = getProductToppingResponseBody.qty_default();
        this.unitPrice = getProductToppingResponseBody.unit_price();
    }

    private void edit(EditProductToppingRequestBody editProductToppingRequestBody) {
        Request editProductToppingRequest = Request.of(
                RequestUtils.getURI("/menu/product_toppings/"), Method.PATCH, RequestUtils.getBearerAuthorization(),
                editProductToppingRequestBody
        );
        GetProductToppingResponseBody editProductToppingResponseBody = Requester.exchange(
                editProductToppingRequest,
                response -> RequestUtils.processEntityResponse(response, 200, GetProductToppingResponseBody.class)
        );
        set(editProductToppingResponseBody);
    }

    public void setIsActive(boolean isActive) {
        EditProductToppingRequestBody editProductToppingRequestBody = new EditProductToppingRequestBody(
                null, null, null, null, null, isActive
        );
        edit(editProductToppingRequestBody);
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        EditProductToppingRequestBody editProductToppingRequestBody = new EditProductToppingRequestBody(
                null, null, null, null, unitPrice, null
        );
        edit(editProductToppingRequestBody);
    }

    static ProductTopping of(GetProductToppingResponseBody getProductToppingResponseBody) {
        ProductTopping result = new ProductTopping(
                getProductToppingResponseBody.product_topping_id(), getProductToppingResponseBody.product_id()
        );
        result.set(getProductToppingResponseBody);
        return result;
    }

    public static ProductTopping get(long productToppingID) {
        Request getProductToppingRequest = Request.of(
                RequestUtils.getURI("/menu/product_toppings/" + productToppingID), Method.GET, RequestUtils.getBearerAuthorization()
        );
        GetProductToppingResponseBody getProductToppingResponseBody = Requester.exchange(
                getProductToppingRequest,
                response -> RequestUtils.processEntityResponse(response, 200, GetProductToppingResponseBody.class)
        );
        return of(getProductToppingResponseBody);
    }

    public static ProductTopping create(Long productID, String name, MenuQuantitativeMeasure qtyMeasure, Integer qtyMin, Integer qtyMax, Integer qtyDefault, BigDecimal unitPrice, @Nullable Boolean isActive) {
        CreateProductToppingRequestBody createProductToppingRequestBody = new CreateProductToppingRequestBody(
                productID, name, qtyMeasure, qtyMin, qtyMax, qtyDefault, unitPrice, isActive
        );
        Request createProductToppingRequest = Request.of(
                RequestUtils.getURI("/menu/product_toppings/"), Method.POST, RequestUtils.getBearerAuthorization(),
                createProductToppingRequestBody
        );
        GetProductToppingResponseBody createProductToppingResponseBody = Requester.exchange(
                createProductToppingRequest,
                response -> RequestUtils.processEntityResponse(response, 201, GetProductToppingResponseBody.class)
        );
        return of(createProductToppingResponseBody);
    }

}
