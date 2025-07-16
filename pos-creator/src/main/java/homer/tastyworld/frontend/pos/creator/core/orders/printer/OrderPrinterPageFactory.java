package homer.tastyworld.frontend.pos.creator.core.orders.printer;

import homer.tastyworld.frontend.starterpack.base.AppDateTime;
import homer.tastyworld.frontend.starterpack.base.utils.managers.printer.PrinterPageFactory;
import homer.tastyworld.frontend.starterpack.entity.current.ClientPoint;
import homer.tastyworld.frontend.starterpack.entity.misc.ProductPieceType;
import homer.tastyworld.frontend.starterpack.order.Order;
import homer.tastyworld.frontend.starterpack.order.core.items.OrderItem;
import homer.tastyworld.frontend.starterpack.order.core.items.OrderItemAdditive;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;

public class OrderPrinterPageFactory extends PrinterPageFactory {

    private final Order toPrint;

    public OrderPrinterPageFactory(Order toPrint) {
        this.toPrint = toPrint;
    }

    public static OrderPrinterPageFactory getFor(long orderID) {
        return new OrderPrinterPageFactory(Order.get(orderID));
    }

    private void setItemAdditive(OrderItemAdditive additive) throws IOException {
        addFullLine(
                " * Добавка %s %s %s".formatted(additive.productAdditiveName(), additive.pieceQTY(), additive.pieceType().shortName),
                '.',
                additive.pricePerPiece().multiply(BigDecimal.valueOf(additive.pieceQTY())) + " р"
        );
    }

    private void setPieceItem(OrderItem item) throws IOException {
        OrderItemAdditive[] notDefaultAdditive = item.getNotDefaultAdditives();
        if (notDefaultAdditive.length == 0) {
            addFullLine(
                    "%s %s %s".formatted(item.productName(), item.pieceQTY(), item.pieceType().shortName),
                    '.',
                    item.pricePerPiece().multiply(BigDecimal.valueOf(item.pieceQTY())) + " р"
            );
        } else {
            for (int i = 0; i < item.pieceQTY(); i++) {
                addFullLine(
                        "%s 1 %s".formatted(item.productName(), item.pieceType().shortName),
                        '.',
                        item.pricePerPiece() + " р"
                );
                for (OrderItemAdditive additive : notDefaultAdditive) {
                    setItemAdditive(additive);
                }
            }
        }
    }

    private void setWeightItem(OrderItem item) throws IOException {
        addFullLine(
                "%s %s %s".formatted(item.productName(), item.pieceQTY(), item.pieceType().shortName),
                '.',
                item.pricePerPiece().multiply(BigDecimal.valueOf(item.pieceQTY())) + " р"
        );
        for (OrderItemAdditive additive : item.getNotDefaultAdditives()) {
            setItemAdditive(additive);
        }
    }

    private void setItems() throws IOException {
        for (OrderItem item : toPrint.getItems()) {
            if (item.pieceType() == ProductPieceType.PIECES) {
                setPieceItem(item);
            } else {
                setWeightItem(item);
            }
        }
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
        setItems();
        addDivider('=');
        addLineRight("ИТОГО: %s р".formatted(toPrint.getTotalPrice().toString()));
        addDivider('~');
        addLineCenter("СПАСИБО ЗА ВИЗИТ!");
        addEmptyLines(4);
    }

}
