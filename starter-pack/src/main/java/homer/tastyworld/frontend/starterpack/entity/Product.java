package homer.tastyworld.frontend.starterpack.entity;

import homer.tastyworld.frontend.starterpack.api.PhotoRequest;
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
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Map;

@Getter
@EqualsAndHashCode(of = {"id"})
@Builder(builderMethodName = "internalBuilder", access = AccessLevel.PRIVATE)
public class Product {

    private static final Request ENTITY_REQUEST = new Request("/product/read", Method.GET);
    private static final PhotoRequest PHOTO_REQUEST = new PhotoRequest("/product/get_photo");
    public static final CacheProcessor<Long, Product> CACHE = CacheManager.register(Product::readFromBackend);

    private final long id;
    private final String name;
    private final boolean isActive;
    private final ProductPieceType pieceType;
    private final BigDecimal pricePerPiece;
    private final int minPieceQTY, maxPieceQTY, defaultPieceQTY;
    private final long menuID;
    private final long[] additiveIDs;

    public InputStream getPhoto() {
        PHOTO_REQUEST.putInBody("id", id);
        return PHOTO_REQUEST.read();
    }

    public static Product readFromBackend(long id) {
        ENTITY_REQUEST.putInBody("id", id);
        Map<String, Object> productInfo = ENTITY_REQUEST.request().getResultAsJSON();
        return Product
                .internalBuilder()
                .id(id)
                .name((String) productInfo.get("NAME"))
                .isActive(TypeChanger.toBool(productInfo.get("IS_ACTIVE")))
                .pieceType(ProductPieceType.valueOf((String) productInfo.get("PIECE_TYPE")))
                .pricePerPiece(TypeChanger.toBigDecimal(productInfo.get("PRICE_PEER_PEACE")))
                .minPieceQTY(TypeChanger.toInt(productInfo.get("MIN_PEACE_QTY")))
                .maxPieceQTY(TypeChanger.toInt(productInfo.get("MAX_PEACE_QTY")))
                .defaultPieceQTY(TypeChanger.toInt(productInfo.get("DEFAULT_PEACE_QTY")))
                .menuID(TypeChanger.toLong(productInfo.get("MENU_ID")))
                .additiveIDs(TypeChanger.toSortedPrimitiveLongArray(productInfo.get("ADDITIVE_IDs")))
                .build();
    }

    public static Product get(long id) {
        return CACHE.get(id);
    }

}
