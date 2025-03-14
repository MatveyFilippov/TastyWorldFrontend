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
    private static final Map<Long, Map<String, Object>> additivesCache = new ConcurrentHashMap<>();
    private final OrderToPrint orderToPrint;

    private OrderPrinterPageFactory(OrderToPrint orderToPrint) {
        super(24);
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
                (String) productInfo.get("NAME"),
                '.',
                TypeChanger.toBigDecimal(itemInfo.get("PEACE_QTY")) + " " + (productInfo.get("PIECE_TYPE").equals("ONE_HUNDRED_GRAMS") ? "Гр" : "Шт")
        );
        addAdditiveLines(TypeChanger.toMap(itemInfo.get("NOT_DEFAULT_ADDITIVES"), Long.class, Integer.class));
        addEmptyLines(1);
    }

    private void addAdditiveLines(Map<Long, Integer> additives) throws IOException {
        dropFontStyle();
        for (Map.Entry<Long, Integer> additive : additives.entrySet()) {
            final Map<String, Object> additiveInfo = additivesCache.computeIfAbsent(
                    additive.getKey(), id -> {
                        Request request = new Request("/product/read_additive", Method.GET);
                        request.putInBody("id", id);
                        return request.request().getResultAsJSON();
                    }
            );
            addLineLeft(
                    additiveInfo.get("NAME") + " " + additive.getValue() + " " + (additiveInfo.get("PIECE_TYPE").equals("ONE_HUNDRED_GRAMS") ? "Гр" : "Шт")
            );
        }
        setFontStyle(new byte[] {0x1B, 0x21, 0x30});  // 2x high + bold
    }

    @Override
    protected void setContent() throws IOException {
        setFontStyle(new byte[] {0x1D, 0x21, 0x11});  // 4x high + 2x width
        addLineCenter(orderToPrint.name);
        setFontStyle(new byte[] {0x1B, 0x21, 0x30});  // 2x high + bold
        addEmptyLines(2);
        addDivider('=');
        addLineCenter(orderToPrint.createdAt.format(AppDateTime.DATETIME_FORMAT));
        addDivider('-');
        setItems(orderToPrint.itemIDs);
        addDivider('=');
        addEmptyLines(4);
    }

}
