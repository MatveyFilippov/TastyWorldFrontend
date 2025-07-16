package homer.tastyworld.frontend.starterpack.order.core.items;

import homer.tastyworld.frontend.starterpack.entity.misc.ProductPieceType;
import java.math.BigDecimal;
import java.util.Arrays;

public record OrderItem(
        long id, long orderID, String productName, OrderItemAdditive[] additives, Long productID,
        int pieceQTY, BigDecimal pricePerPiece, ProductPieceType pieceType
) {

    public OrderItemAdditive[] getNotDefaultAdditives() {
        return Arrays.stream(additives)
                     .filter(additive -> additive.pieceQTY() != additive.productAdditiveDefaultPieceQTY())
                     .toArray(OrderItemAdditive[]::new);
    }

}
