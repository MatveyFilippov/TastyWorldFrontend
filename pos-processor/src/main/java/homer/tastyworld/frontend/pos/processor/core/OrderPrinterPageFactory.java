package homer.tastyworld.frontend.pos.processor.core;

import homer.tastyworld.frontend.pos.processor.core.cache.AdditivesCache;
import homer.tastyworld.frontend.pos.processor.core.cache.ProductsCache;
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
        String deliveryAddress;
        LocalDateTime createdAt;
        Long[] itemIDs;

    }

    private static final Request READ_ORDER_REQUEST = new Request("/order/read", Method.GET);
    private static final Request READ_ORDER_ITEM_REQUEST = new Request("/order/read_item", Method.GET);
    private final OrderToPrint orderToPrint;

    private OrderPrinterPageFactory(OrderToPrint orderToPrint) {
        this.orderToPrint = orderToPrint;
    }

    public static OrderPrinterPageFactory getFor(long orderID) {
        READ_ORDER_REQUEST.putInBody("id", orderID);
        Map<String, Object> orderInfo = READ_ORDER_REQUEST.request().getResultAsJSON();
        OrderToPrint orderToPrint = new OrderToPrint();
        orderToPrint.name = (String) orderInfo.get("NAME");
        orderToPrint.createdAt = AppDateTime.backendToLocal(AppDateTime.parseDateTime(
                (String) orderInfo.get("CREATED_AT")
        ));
        orderToPrint.itemIDs = TypeChanger.toSortedLongArray(orderInfo.get("ITEM_IDs"));
        orderToPrint.deliveryAddress = (String) orderInfo.get("DELIVERY_ADDRESS");
        if (orderToPrint.deliveryAddress.equals("NOT FOR DELIVERY")) {
            orderToPrint.deliveryAddress = "НЕТ";
        }
        return new OrderPrinterPageFactory(orderToPrint);
    }

    private void setItems(Long[] itemIDs) throws IOException {
        addEmptyLines(1);
        int lastItemIndex = itemIDs.length - 1;
        for (int i = 0; i < lastItemIndex + 1; i++) {
            READ_ORDER_ITEM_REQUEST.putInBody("id", itemIDs[i]);
            addItemLine(READ_ORDER_ITEM_REQUEST.request().getResultAsJSON());
            addEmptyLines(1);
            if (i < lastItemIndex) {
                addDivider('-');
                addEmptyLines(1);
            }
        }
    }

    private void addItemLine(Map<String, Object> itemInfo) throws IOException {
        final Map<String, Object> productInfo = ProductsCache.impl.get(TypeChanger.toLong(itemInfo.get("PRODUCT_ID")));
        addLineLeft((String) productInfo.get("NAME"));
        addLineLeft("Кол-во: " + TypeChanger.toBigDecimal(itemInfo.get("PEACE_QTY")) + " " + (productInfo.get("PIECE_TYPE").equals("ONE_HUNDRED_GRAMS") ? "Гр" : "Шт"));
        addAdditiveLines(TypeChanger.toMap(itemInfo.get("NOT_DEFAULT_ADDITIVES"), Long.class, Integer.class));
    }

    private void addAdditiveLines(Map<Long, Integer> additives) throws IOException {
        if (additives.isEmpty()) {
            return;
        }
        addLineLeft("Добавки:");
        dropFontStyle();
        for (Map.Entry<Long, Integer> additive : additives.entrySet()) {
            final Map<String, Object> additiveInfo = AdditivesCache.impl.get(additive.getKey());
            addLineLeft(
                    " - " + additiveInfo.get("NAME") + " " + additive.getValue() + " " + (additiveInfo.get("PIECE_TYPE").equals("ONE_HUNDRED_GRAMS") ? "Гр" : "Шт")
            );
        }
        setFontStyle(new byte[] {0x1B, 0x21, 0x20}, 24);  // 2x high + bold
    }

    @Override
    protected void setContent() throws IOException {
        setFontStyle(new byte[] {0x1D, 0x21, 0x11});  // 4x high + 2x width
        addLineCenter(orderToPrint.name);
        addEmptyLines(1);
        dropFontStyle();
        addLineCenter(orderToPrint.createdAt.format(AppDateTime.DATETIME_FORMAT));
        setFontStyle(new byte[] {0x1B, 0x21, 0x20}, 24);  // 2x high + bold
        addDivider('=');
        setItems(orderToPrint.itemIDs);
        addDivider('=');
        addEmptyLines(1);
        dropFontStyle();
        addLineRight("Доставка: " + orderToPrint.deliveryAddress);
        addEmptyLines(5);
    }

}
