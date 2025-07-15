package homer.tastyworld.frontend.pos.creator.core.orders.printer;

import homer.tastyworld.frontend.starterpack.base.AppDateTime;
import homer.tastyworld.frontend.starterpack.base.utils.managers.printer.PrinterPageFactory;
import homer.tastyworld.frontend.starterpack.entity.current.ClientPoint;
import homer.tastyworld.frontend.starterpack.order.Order;
import homer.tastyworld.frontend.starterpack.order.core.items.OrderItem;
import java.io.IOException;
import java.math.BigDecimal;

public class OrderPageFactory extends PrinterPageFactory {

    private final Order toPrint;

    public OrderPageFactory(Order toPrint) {
        this.toPrint = toPrint;
    }

    public static OrderPageFactory getFor(long orderID) {
        return new OrderPageFactory(Order.get(orderID));
    }

    @Override
    protected void setContent() throws IOException {
        setFontStyle(new byte[] {0x1B, 0x21, 0x38});  // 4x high + 2x width
        addLineCenter(toPrint.name);
        dropFontStyle();
        addEmptyLines(2);
        addLineCenter("ТОВАРНЫЙ ЧЕК");
        addLineCenter(ClientPoint.name);
        addDivider('~');
        addLineCenter(toPrint.getPaidAt().format(AppDateTime.DATETIME_FORMAT));
        addDivider('=');
        for (OrderItem item : toPrint.getItems()) {
            addFullLine(
                    String.format(
                            "%s %s %s",
                            item.productName(), item.pricePerPiece(), item.pieceType().shortName
                    ),
                    '.',
                    item.pricePerPiece().multiply(BigDecimal.valueOf(item.pieceQTY())) + " р"
            );
        }
        addDivider('=');
        addLineRight("ИТОГО: " + toPrint.getTotalPrice().toString() + " р");
        addDivider('~');
        addLineCenter("СПАСИБО ЗА ВИЗИТ!");
        addEmptyLines(4);
    }

}
