package homer.tastyworld.frontend.pos.creator.core.orders.printer;

import homer.tastyworld.frontend.pos.creator.core.cache.ProductsCache;
import homer.tastyworld.frontend.starterpack.api.Request;
import homer.tastyworld.frontend.starterpack.api.requests.MyParams;
import homer.tastyworld.frontend.starterpack.base.AppDateTime;
import homer.tastyworld.frontend.starterpack.base.utils.managers.printer.PrinterPageFactory;
import homer.tastyworld.frontend.starterpack.base.utils.misc.TypeChanger;
import org.apache.hc.core5.http.Method;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

public class OrderPageFactory extends PrinterPageFactory {

    private static class OrderToPrint {

        static final String clientPoint = (String) MyParams.getClientPointInfo().get("NAME");
        String name;
        LocalDateTime paidAt;
        BigDecimal totalPrice;
        Long[] itemIDs;

    }

    private static final Request READ_ORDER_REQUEST = new Request("/order/read", Method.GET);
    private static final Request READ_ORDER_ITEM_REQUEST = new Request("/order/read_item", Method.GET);
    private final OrderToPrint orderToPrint;

    private OrderPageFactory(OrderToPrint orderToPrint) {
        this.orderToPrint = orderToPrint;
    }

    public static OrderPageFactory getFor(long orderID) {
        READ_ORDER_REQUEST.putInBody("id", orderID);
        Map<String, Object> orderInfo = READ_ORDER_REQUEST.request().getResultAsJSON();
        OrderToPrint orderToPrint = new OrderToPrint();
        orderToPrint.name = (String) orderInfo.get("NAME");
        orderToPrint.paidAt = AppDateTime.backendToLocal(AppDateTime.parseDateTime(
                (String) orderInfo.get("PAID_AT")
        ));
        orderToPrint.totalPrice = TypeChanger.toBigDecimal(orderInfo.get("TOTAL_PRICE"));
        orderToPrint.itemIDs = TypeChanger.toSortedLongArray(orderInfo.get("ITEM_IDs"));
        return new OrderPageFactory(orderToPrint);
    }

    private void setItems(Long[] itemIDs) throws IOException {
        for (Long itemID : itemIDs) {
            READ_ORDER_ITEM_REQUEST.putInBody("id", itemID);
            addItemLine(READ_ORDER_ITEM_REQUEST.request().getResultAsJSON());
        }
    }

    private void addItemLine(Map<String, Object> itemInfo) throws IOException {
        final Map<String, Object> productInfo = ProductsCache.impl.get(TypeChanger.toLong(itemInfo.get("PRODUCT_ID")));
        addFullLine(
                String.format(
                        "%s %s %s",
                        productInfo.get("NAME"),
                        TypeChanger.toBigDecimal(itemInfo.get("PEACE_QTY")),
                        productInfo.get("PIECE_TYPE").equals("ONE_HUNDRED_GRAMS") ? "Гр" : "Шт"
                ),
                '.',
                TypeChanger.toBigDecimal(itemInfo.get("ITEM_PRICE")) + " р"
        );
    }

    @Override
    protected void setContent() throws IOException {
        setFontStyle(new byte[] {0x1B, 0x21, 0x38});  // 4x high + 2x width
        addLineCenter(orderToPrint.name);
        dropFontStyle();
        addEmptyLines(2);
        addLineCenter("ТОВАРНЫЙ ЧЕК");
        addLineCenter(OrderToPrint.clientPoint);
        addDivider('~');
        addLineCenter(orderToPrint.paidAt.format(AppDateTime.DATETIME_FORMAT));
        addDivider('=');
        setItems(orderToPrint.itemIDs);
        addDivider('=');
        addLineRight("ИТОГО: " + orderToPrint.totalPrice.toString() + " р");
        addDivider('~');
        addLineCenter("СПАСИБО ЗА ВИЗИТ!");
        addEmptyLines(4);
    }

}
