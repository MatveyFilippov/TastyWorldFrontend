package homer.tastyworld.frontend.starterpack.order.core.items;

import homer.tastyworld.frontend.starterpack.base.exceptions.response.BadRequestException;
import homer.tastyworld.frontend.starterpack.entity.ProductAdditive;
import homer.tastyworld.frontend.starterpack.entity.misc.ProductPieceType;
import java.math.BigDecimal;
import java.util.Arrays;

public record OrderItem(
        long id, long orderID, String productName, OrderItemAdditive[] additives, Long productID,
        int pieceQTY, BigDecimal pricePerPiece, ProductPieceType pieceType
) {

    public OrderItemAdditive[] getNotDefaultAdditives() {
        return Arrays.stream(additives).filter(additive -> {
            try {
                ProductAdditive origin = ProductAdditive.get(additive.productAdditiveID());
                return origin.getDefaultPieceQTY() != additive.pieceQTY();
            } catch (BadRequestException ignored) {
                return true;
            }
        }).toArray(OrderItemAdditive[]::new);
    }

}
