package homer.tastyworld.frontend.starterpack.base.entity.implementations;

import homer.tastyworld.frontend.starterpack.api.Request;
import homer.tastyworld.frontend.starterpack.base.entity.EntityWithPhoto;
import homer.tastyworld.frontend.starterpack.base.entity.misc.ProductPieceType;
import homer.tastyworld.frontend.starterpack.base.utils.managers.cache.CacheManager;
import homer.tastyworld.frontend.starterpack.base.utils.managers.cache.SimpleCacheProcessor;
import homer.tastyworld.frontend.starterpack.base.utils.misc.TypeChanger;
import org.apache.hc.core5.http.Method;
import java.math.BigDecimal;
import java.util.Map;

public class Product extends EntityWithPhoto {

    private static final Request REQUEST = new Request("/product/read", Method.GET);
    public static final SimpleCacheProcessor<Long, Product> CACHE = CacheManager.register(Product::readFromBackend);

    public final long id;
    public final String name;
    public final boolean isActive;
    public final ProductPieceType pieceType;
    public final BigDecimal pricePerPiece;
    public final int minPieceQTY, maxPieceQTY, defaultPieceQTY;
    public final long menuID;
    public final long[] additiveIDs;

    Product(
            long id, String name, boolean isActive,
            ProductPieceType pieceType, BigDecimal pricePerPiece,
            int minPieceQTY, int maxPieceQTY, int defaultPieceQTY,
            long menuID, long[] additiveIDs
    ) {
        this.id = id;
        this.name = name;
        this.isActive = isActive;
        this.pieceType = pieceType;
        this.pricePerPiece = pricePerPiece;
        this.minPieceQTY = minPieceQTY;
        this.maxPieceQTY = maxPieceQTY;
        this.defaultPieceQTY = defaultPieceQTY;
        this.menuID = menuID;
        this.additiveIDs = additiveIDs;
    }

    public static Product readFromBackend(long id) {
        REQUEST.putInBody("id", id);
        Map<String, Object> productInfo = REQUEST.request().getResultAsJSON();
        return new Product(
                TypeChanger.toLong(productInfo.get("ID")),
                (String) productInfo.get("NAME"),
                TypeChanger.toBool(productInfo.get("IS_ACTIVE")),
                ProductPieceType.valueOf((String) productInfo.get("PIECE_TYPE")),
                TypeChanger.toBigDecimal(productInfo.get("PRICE_PEER_PEACE")),
                TypeChanger.toInt(productInfo.get("MIN_PEACE_QTY")),
                TypeChanger.toInt(productInfo.get("MAX_PEACE_QTY")),
                TypeChanger.toInt(productInfo.get("DEFAULT_PEACE_QTY")),
                TypeChanger.toLong(productInfo.get("MENU_ID")),
                TypeChanger.toPrimitiveLongArray(productInfo.get("ADDITIVE_IDs"))
        );
    }

    public static Product get(long id) {
        return CACHE.get(id);
    }

}
