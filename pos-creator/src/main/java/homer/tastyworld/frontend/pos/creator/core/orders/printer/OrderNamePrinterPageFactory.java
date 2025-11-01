package homer.tastyworld.frontend.pos.creator.core.orders.printer;

import homer.tastyworld.frontend.starterpack.api.sra.entity.order.Order;
import homer.tastyworld.frontend.starterpack.api.sra.entity.order.OrderUtils;
import homer.tastyworld.frontend.starterpack.utils.managers.external.printer.PrinterPageFactory;
import java.io.IOException;

public class OrderNamePrinterPageFactory extends PrinterPageFactory {

    private final Order toPrint;

    public OrderNamePrinterPageFactory(Order toPrint) {
        this.toPrint = toPrint;
    }

    public static OrderNamePrinterPageFactory getFor(long orderID) {
        return new OrderNamePrinterPageFactory(OrderUtils.getOrCreateInstance(orderID));
    }

    @Override
    protected void setContent() throws IOException {
        setFontStyle(24);
        setFontStyle(new byte[] {0x1B, 0x21, 0x20});  // 2x high + bold
        addLineCenter("Ваш заказ:");
        addEmptyLines(2);
        setFontStyle(new byte[] {0x1D, 0x21, 0x33});  // 8x high + 2x width
        addLineCenter(toPrint.getName());
        setFontStyle(new byte[] {0x1B, 0x21, 0x20});  // 2x high + bold
        addEmptyLines(2);
        addLineCenter("Готовится с любовью!");
        addEmptyLines(6);
    }

}
