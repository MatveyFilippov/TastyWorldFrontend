package homer.tastyworld.frontend.pos.creator.core.orders.printer;

import homer.tastyworld.frontend.starterpack.api.sra.entity.current.ClientPoint;
import homer.tastyworld.frontend.starterpack.api.sra.entity.misc.MenuQuantitativeMeasure;
import homer.tastyworld.frontend.starterpack.api.sra.entity.order.Order;
import homer.tastyworld.frontend.starterpack.api.sra.entity.order.OrderItem;
import homer.tastyworld.frontend.starterpack.api.sra.entity.order.OrderItemModifier;
import homer.tastyworld.frontend.starterpack.api.sra.entity.order.OrderUtils;
import homer.tastyworld.frontend.starterpack.utils.managers.external.printer.PrinterPageFactory;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;

public class OrderWithItemsPrinterPageFactory extends PrinterPageFactory {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final DecimalFormat priceFormatter = new DecimalFormat("#0.00 р");
    private final Order toPrint;

    public OrderWithItemsPrinterPageFactory(Order toPrint) {
        this.toPrint = toPrint;
    }

    public static OrderWithItemsPrinterPageFactory getFor(long orderID) {
        return new OrderWithItemsPrinterPageFactory(OrderUtils.getOrCreateInstance(orderID));
    }

    private void setItemModifier(OrderItemModifier modifier) throws IOException {
        if (modifier.quantity() != modifier.qtyDefault()) {
            addFullLine(
                    " * %s %s %s".formatted(modifier.name(), modifier.quantity(), modifier.qtyMeasure().shortName),
                    '.',
                    priceFormatter.format(modifier.unitPrice().multiply(BigDecimal.valueOf(Math.max(modifier.quantity() - modifier.qtyDefault(), 0))))
            );
        }
    }

    private void setPieceItem(OrderItem item) throws IOException {
        if (item.notDefaultModifiers().length == 0) {
            addFullLine(
                    "%s %s %s".formatted(item.name(), item.quantity(), item.qtyMeasure().shortName),
                    '.',
                    priceFormatter.format(item.unitPrice().multiply(BigDecimal.valueOf(item.quantity())))
            );
        } else {
            for (int i = 0; i < item.quantity(); i++) {
                addFullLine(
                        "%s 1 %s".formatted(item.name(), item.qtyMeasure().shortName),
                        '.',
                        priceFormatter.format(item.unitPrice())
                );
                for (OrderItemModifier modifier : item.notDefaultModifiers()) {
                    setItemModifier(modifier);
                }
            }
        }
    }

    private void setWeightItem(OrderItem item) throws IOException {
        addFullLine(
                "%s %s %s".formatted(item.name(), item.quantity(), item.qtyMeasure().shortName),
                '.',
                priceFormatter.format(item.unitPrice().multiply(BigDecimal.valueOf(item.quantity())))
        );
        for (OrderItemModifier modifier : item.notDefaultModifiers()) {
            setItemModifier(modifier);
        }
    }

    private void setItems() throws IOException {
        for (OrderItem item : toPrint.getItems()) {
            if (item.qtyMeasure() == MenuQuantitativeMeasure.PIECES) {
                setPieceItem(item);
            } else {
                setWeightItem(item);
            }
        }
    }

    @Override
    protected void setContent() throws IOException {
        setFontStyle(new byte[] {0x1B, 0x21, 0x38});  // 4x high + 2x width
        addLineCenter(toPrint.getName());
        dropFontStyle();
        addEmptyLines(2);
        addLineCenter("ТОВАРНЫЙ ЧЕК");
        addLineCenter(ClientPoint.name);
        addDivider('~');
        addLineCenter(toPrint.getPaidAt().format(dateTimeFormatter));
        addDivider('=');
        setItems();
        addDivider('=');
        addLineRight("ИТОГО: " + priceFormatter.format(toPrint.getTotalAmount()));
        addDivider('~');
        addLineCenter("СПАСИБО ЗА ВИЗИТ!");
        addEmptyLines(4);
    }

}
