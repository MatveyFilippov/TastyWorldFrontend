package homer.tastyworld.frontend.starterpack.api.sra.entity.order;

import homer.tastyworld.frontend.starterpack.api.sra.entity.misc.MenuQuantitativeMeasure;
import java.math.BigDecimal;

public record OrderItemModifier(
        long itemID,
        String name,
        MenuQuantitativeMeasure qtyMeasure,
        int qtyDefault,
        int quantity,
        BigDecimal unitPrice
) {}
