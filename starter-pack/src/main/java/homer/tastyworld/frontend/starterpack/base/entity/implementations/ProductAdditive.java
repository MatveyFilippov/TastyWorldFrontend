package homer.tastyworld.frontend.starterpack.base.entity.implementations;

import homer.tastyworld.frontend.starterpack.api.Request;
import homer.tastyworld.frontend.starterpack.base.entity.Entity;
import homer.tastyworld.frontend.starterpack.base.entity.misc.ProductPieceType;
import homer.tastyworld.frontend.starterpack.base.utils.managers.cache.CacheManager;
import homer.tastyworld.frontend.starterpack.base.utils.managers.cache.SimpleCacheProcessor;
import homer.tastyworld.frontend.starterpack.base.utils.misc.TypeChanger;
import org.apache.hc.core5.http.Method;
import java.math.BigDecimal;
import java.util.Map;

public class ProductAdditive extends Entity {

    private static final Request REQUEST = new Request("/product/read_additive", Method.GET);
    public static final SimpleCacheProcessor<Long, ProductAdditive> CACHE = CacheManager.register(ProductAdditive::readFromBackend);

    public final long id;
    public final String name;
    public final boolean isActive;
    public final ProductPieceType pieceType;
    public final BigDecimal pricePerPiece;
    public final int minPieceQTY, maxPieceQTY, defaultPieceQTY;
    public final long productID;

    ProductAdditive(
            long id, String name, boolean isActive,
            ProductPieceType pieceType, BigDecimal pricePerPiece,
            int minPieceQTY, int maxPieceQTY, int defaultPieceQTY,
            long productID
    ) {
        this.id = id;
        this.name = name;
        this.isActive = isActive;
        this.pieceType = pieceType;
        this.pricePerPiece = pricePerPiece;
        this.minPieceQTY = minPieceQTY;
        this.maxPieceQTY = maxPieceQTY;
        this.defaultPieceQTY = defaultPieceQTY;
        this.productID = productID;
    }

    public static ProductAdditive readFromBackend(long id) {
        REQUEST.putInBody("id", id);
        Map<String, Object> additiveInfo =  REQUEST.request().getResultAsJSON();
        return new ProductAdditive(
                TypeChanger.toLong(additiveInfo.get("ID")),
                (String) additiveInfo.get("NAME"),
                TypeChanger.toBool(additiveInfo.get("IS_ACTIVE")),
                ProductPieceType.valueOf((String) additiveInfo.get("PIECE_TYPE")),
                TypeChanger.toBigDecimal(additiveInfo.get("PIECE_TYPE")),
                TypeChanger.toInt(additiveInfo.get("MIN_PEACE_QTY")),
                TypeChanger.toInt(additiveInfo.get("MAX_PEACE_QTY")),
                TypeChanger.toInt(additiveInfo.get("DEFAULT_PEACE_QTY")),
                TypeChanger.toLong(additiveInfo.get("PRODUCT_ID"))
        );
    }

    public static ProductAdditive get(long id) {
        return CACHE.get(id);
    }

}
