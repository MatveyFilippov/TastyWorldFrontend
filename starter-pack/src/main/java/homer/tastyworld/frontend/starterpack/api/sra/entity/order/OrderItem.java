package homer.tastyworld.frontend.starterpack.api.sra.entity.order;

import homer.tastyworld.frontend.starterpack.api.sra.entity.misc.MenuQuantitativeMeasure;
import homer.tastyworld.frontend.starterpack.api.sra.entity.misc.ProductTax;
import homer.tastyworld.frontend.starterpack.api.sra.entity.misc.ProductType;
import org.jetbrains.annotations.Nullable;
import java.math.BigDecimal;
import java.util.Arrays;

public record OrderItem(
        long itemID,
        long orderID,
        String name,
        ProductType type,
        ProductTax tax,
        @Nullable String mark,
        MenuQuantitativeMeasure qtyMeasure,
        int qtyMin,
        int qtyMax,
        int quantity,
        BigDecimal unitPrice,
        OrderItemModifier[] modifiers
) {

    public OrderItemModifier[] notDefaultModifiers() {
        return Arrays.stream(modifiers)
                     .filter(modifier -> modifier.quantity() != modifier.qtyDefault())
                     .toArray(OrderItemModifier[]::new);
    }

    public BigDecimal totalPrice() {
        BigDecimal itemQTY = BigDecimal.valueOf(quantity);
        BigDecimal total = unitPrice.multiply(itemQTY);
        BigDecimal modifierMultiplier = qtyMeasure == MenuQuantitativeMeasure.PIECES ? itemQTY : BigDecimal.ONE;
        for (OrderItemModifier modifier : notDefaultModifiers()) {
            if (modifier.quantity() > modifier.qtyDefault()) {
                BigDecimal modifierOverQTY = BigDecimal.valueOf(modifier.quantity() - modifier.qtyDefault());
                BigDecimal modifierTotal = modifier.unitPrice().multiply(modifierOverQTY).multiply(modifierMultiplier);
                total = total.add(modifierTotal);
            }
        }
        return total;
    }

    public OrderItem copyWithMark(String mark) {
        return new OrderItem(
                this.itemID,
                this.orderID,
                this.name,
                this.type,
                this.tax,
                mark,
                this.qtyMeasure,
                this.qtyMin,
                this.qtyMax,
                this.quantity,
                this.unitPrice,
                this.modifiers
        );
    }

    public OrderItem copyWithQuantity(int quantity) {
        return new OrderItem(
                this.itemID,
                this.orderID,
                this.name,
                this.type,
                this.tax,
                this.mark,
                this.qtyMeasure,
                this.qtyMin,
                this.qtyMax,
                quantity,
                this.unitPrice,
                this.modifiers
        );
    }

}
