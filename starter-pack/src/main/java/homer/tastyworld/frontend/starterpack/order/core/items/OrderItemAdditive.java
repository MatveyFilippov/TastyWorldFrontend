package homer.tastyworld.frontend.starterpack.order.core.items;

import homer.tastyworld.frontend.starterpack.entity.misc.ProductPieceType;
import java.math.BigDecimal;

public record OrderItemAdditive(
        long id, long orderItemID, String productAdditiveName, Long productAdditiveID,
        int pieceQTY, int productAdditiveDefaultPieceQTY, BigDecimal pricePerPiece, ProductPieceType pieceType
) {}
