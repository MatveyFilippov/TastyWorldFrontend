package homer.tastyworld.frontend.starterpack.entity;

import homer.tastyworld.frontend.starterpack.api.Request;
import homer.tastyworld.frontend.starterpack.entity.misc.ProductPieceType;
import homer.tastyworld.frontend.starterpack.base.utils.managers.cache.CacheManager;
import homer.tastyworld.frontend.starterpack.base.utils.managers.cache.CacheProcessor;
import homer.tastyworld.frontend.starterpack.base.utils.misc.TypeChanger;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.apache.hc.core5.http.Method;
import java.math.BigDecimal;
import java.util.Map;

@Getter
@EqualsAndHashCode(of = {"id"})
@Builder(builderMethodName = "internalBuilder", access = AccessLevel.PRIVATE)
public class ProductAdditive {

    private static final Request ENTITY_REQUEST = new Request("/product/read_additive", Method.GET);
    public static final CacheProcessor<Long, ProductAdditive> CACHE = CacheManager.register(ProductAdditive::readFromBackend);

    private final long id;
    private final String name;
    private final boolean isActive;
    private final ProductPieceType pieceType;
    private final BigDecimal pricePerPiece;
    private final int minPieceQTY, maxPieceQTY, defaultPieceQTY;
    private final long productID;

    public static ProductAdditive readFromBackend(long id) {
        ENTITY_REQUEST.putInBody("id", id);
        Map<String, Object> additiveInfo =  ENTITY_REQUEST.request().getResultAsJSON();
        return ProductAdditive
                .internalBuilder()
                .id(id)
                .name((String) additiveInfo.get("NAME"))
                .isActive(TypeChanger.toBool(additiveInfo.get("IS_ACTIVE")))
                .pieceType(ProductPieceType.valueOf((String) additiveInfo.get("PIECE_TYPE")))
                .pricePerPiece(TypeChanger.toBigDecimal(additiveInfo.get("PRICE_PEER_PEACE")))
                .minPieceQTY(TypeChanger.toInt(additiveInfo.get("MIN_PEACE_QTY")))
                .maxPieceQTY(TypeChanger.toInt(additiveInfo.get("MAX_PEACE_QTY")))
                .defaultPieceQTY(TypeChanger.toInt(additiveInfo.get("DEFAULT_PEACE_QTY")))
                .productID(TypeChanger.toLong(additiveInfo.get("PRODUCT_ID")))
                .build();
    }

    public static ProductAdditive get(long id) {
        return CACHE.get(id);
    }

}
