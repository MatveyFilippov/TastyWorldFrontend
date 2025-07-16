package homer.tastyworld.frontend.pos.processor.core.printer;

import homer.tastyworld.frontend.starterpack.base.AppDateTime;
import homer.tastyworld.frontend.starterpack.base.utils.managers.printer.PrinterPageFactory;
import homer.tastyworld.frontend.starterpack.order.Order;
import homer.tastyworld.frontend.starterpack.order.core.items.OrderItem;
import homer.tastyworld.frontend.starterpack.order.core.items.OrderItemAdditive;
import java.io.IOException;

public class OrderPrinterPageFactory extends PrinterPageFactory {

    private final Order toPrint;

    public OrderPrinterPageFactory(Order toPrint) {
        this.toPrint = toPrint;
    }

    public static OrderPrinterPageFactory getFor(long orderID) {
        return new OrderPrinterPageFactory(Order.get(orderID));
    }

    private void setItems(OrderItem[] items) throws IOException {
        addEmptyLines(1);
        int lastItemIndex = items.length - 1;
        for (int i = 0; i < items.length; i++) {
            addItemLine(items[i]);
            addEmptyLines(1);
            if (i < lastItemIndex) {
                addDivider('-');
                addEmptyLines(1);
            }
        }
    }

    private void addItemLine(OrderItem item) throws IOException {
        addLineLeft(item.productName());
        addLineLeft("Кол-во: " + item.pieceQTY() + " " + item.pieceType().shortName);
        addNotDefaultAdditiveLines(item.getNotDefaultAdditives());
    }

    private void addNotDefaultAdditiveLines(OrderItemAdditive[] additives) throws IOException {
        if (additives.length == 0) {
            return;
        }
        addLineLeft("Добавки:");
        dropFontStyle();
        for (OrderItemAdditive additive : additives) {
            addLineLeft(
                    " - " + additive.productAdditiveName() + " " + additive.pieceQTY() + " " + additive.pieceType().shortName
            );
        }
        setFontStyle(new byte[] {0x1B, 0x21, 0x20}, 24);  // 2x high + bold
    }

    @Override
    protected void setContent() throws IOException {
        setFontStyle(new byte[] {0x1D, 0x21, 0x11});  // 4x high + 2x width
        addLineCenter(toPrint.name);
        addEmptyLines(1);
        dropFontStyle();
        addLineCenter(toPrint.createdAt.format(AppDateTime.DATETIME_FORMAT));
        setFontStyle(new byte[] {0x1B, 0x21, 0x20}, 24);  // 2x high + bold
        addDivider('=');
        setItems(toPrint.getItems());
        addDivider('=');
        addEmptyLines(1);
        dropFontStyle();
        addLineRight("Доставка: " + (toPrint.getDeliveryInfo() != null ? toPrint.getDeliveryInfo() : "НЕТ"));
        addEmptyLines(5);
    }

}
