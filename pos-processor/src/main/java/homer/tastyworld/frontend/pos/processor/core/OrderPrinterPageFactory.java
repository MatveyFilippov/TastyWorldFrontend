package homer.tastyworld.frontend.pos.processor.core;

import homer.tastyworld.frontend.starterpack.api.Request;
import homer.tastyworld.frontend.starterpack.base.AppDateTime;
import homer.tastyworld.frontend.starterpack.base.utils.managers.printer.PrinterPageFactory;
import homer.tastyworld.frontend.starterpack.base.utils.misc.TypeChanger;
import org.apache.hc.core5.http.Method;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class OrderPrinterPageFactory extends PrinterPageFactory {

    private static class OrderToPrint {

        String name;
        LocalDateTime createdAt;
        Long[] itemIDs;

    }

    private static final Map<Long, Map<String, Object>> productCache = new ConcurrentHashMap<>();
    private final OrderToPrint orderToPrint;

    private OrderPrinterPageFactory(OrderToPrint orderToPrint) {
        this.orderToPrint = orderToPrint;
    }

    public static OrderPrinterPageFactory getFor(long orderID) {
        Request request = new Request("/order/read", Method.GET);
        request.putInBody("id", orderID);
        Map<String, Object> orderInfo = request.request().getResultAsJSON();
        OrderToPrint orderToPrint = new OrderToPrint();
        orderToPrint.name = (String) orderInfo.get("NAME");
        orderToPrint.createdAt = AppDateTime.backendToLocal(AppDateTime.parseDateTime(
                (String) orderInfo.get("CREATED_AT")
        ));
        orderToPrint.itemIDs = TypeChanger.toSortedLongArray(orderInfo.get("ITEM_IDs"));
        return new OrderPrinterPageFactory(orderToPrint);
    }

    private void setItems(Long[] itemIDs) throws IOException {
        for (Long itemID : itemIDs) {
            Request request = new Request("/order/read_item", Method.GET);
            request.putInBody("id", itemID);
            addItemLine(request.request().getResultAsJSON());
        }
        addDivider('=');
    }

    private void addItemLine(Map<String, Object> itemInfo) throws IOException {
        final Map<String, Object> productInfo = productCache.computeIfAbsent(
                TypeChanger.toLong(itemInfo.get("PRODUCT_ID")), id -> {
                    Request request = new Request("/product/read", Method.GET);
                    request.putInBody("id", id);
                    return request.request().getResultAsJSON();
                }
        );
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
        output.write(new byte[] {0x1D, 0x21, 0x11});  // 4x high + 2x width
        addLineCenter(orderToPrint.name);
        output.write(new byte[] {0x1B, 0x21, 0x30}); // 2x high + bold

        addEmptyLines(2);
        addDivider('=');
        addLineCenter(orderToPrint.createdAt.format(AppDateTime.DATETIME_FORMAT));
        addDivider('-');

        setItems(orderToPrint.itemIDs);

        addEmptyLines(4);
    }

}
