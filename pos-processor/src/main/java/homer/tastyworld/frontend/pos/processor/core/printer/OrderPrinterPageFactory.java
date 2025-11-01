package homer.tastyworld.frontend.pos.processor.core.printer;

import homer.tastyworld.frontend.starterpack.api.sra.entity.order.Order;
import homer.tastyworld.frontend.starterpack.api.sra.entity.order.OrderItem;
import homer.tastyworld.frontend.starterpack.api.sra.entity.order.OrderItemModifier;
import homer.tastyworld.frontend.starterpack.api.sra.entity.order.OrderUtils;
import homer.tastyworld.frontend.starterpack.utils.managers.external.printer.PrinterPageFactory;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class OrderPrinterPageFactory extends PrinterPageFactory {

    private static final DateTimeFormatter toPrintDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private final Order toPrint;

    public OrderPrinterPageFactory(Order toPrint) {
        this.toPrint = toPrint;
    }

    public static OrderPrinterPageFactory getFor(long orderID) {
        return new OrderPrinterPageFactory(OrderUtils.getOrCreateInstance(orderID));
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
        addLineLeft(item.name());
        addLineLeft("Кол-во: " + item.quantity() + " " + item.qtyMeasure().shortName);
        addNotDefaultModifiersLines(item.notDefaultModifiers());
    }

    private void addNotDefaultModifiersLines(OrderItemModifier[] modifiers) throws IOException {
        if (modifiers.length == 0) {
            return;
        }
        addLineLeft("Изменено:");
        dropFontStyle();
        for (OrderItemModifier modifier : modifiers) {
            addLineLeft(
                    " - " + modifier.name() + " " + modifier.quantity() + " " + modifier.qtyMeasure().shortName
            );
        }
        setFontStyle(new byte[] {0x1B, 0x21, 0x20}, 24);  // 2x high + bold
    }

    @Override
    protected void setContent() throws IOException {
        setFontStyle(new byte[] {0x1D, 0x21, 0x11});  // 4x high + 2x width
        addLineCenter(toPrint.getName());
        addEmptyLines(1);
        dropFontStyle();
        addLineCenter(toPrint.getDraftAt().format(toPrintDateTimeFormatter));
        setFontStyle(new byte[] {0x1B, 0x21, 0x20}, 24);  // 2x high + bold
        addDivider('=');
        setItems(toPrint.getItems());
        addDivider('=');
        addEmptyLines(1);
        dropFontStyle();
        if (toPrint.getDeliveryInfo() != null) {
            addLineRight("Доставка: " + toPrint.getDeliveryInfo());
        }
        addEmptyLines(5);
    }

}
