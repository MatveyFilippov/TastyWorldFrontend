package homer.tastyworld.frontend.starterpack.order.core.items;

import homer.tastyworld.frontend.starterpack.api.Request;
import homer.tastyworld.frontend.starterpack.api.Response;
import homer.tastyworld.frontend.starterpack.base.utils.misc.TypeChanger;
import homer.tastyworld.frontend.starterpack.entity.misc.ProductPieceType;
import org.apache.hc.core5.http.Method;
import java.util.Arrays;
import java.util.Map;

public class OrderItemFetcher {

    private static final Request READ_ADDITIVE = new Request("/order/read_item_additive", Method.GET);
    private static final Request READ_ITEM = new Request("/order/read_item", Method.GET);

    private static Map<String, Object> readAdditive(long id) {
        READ_ADDITIVE.putInBody("id", id);
        Response response = READ_ADDITIVE.request();
        return response.getResultAsJSON();
    }

    public static OrderItemAdditive getAdditive(long id) {
        Map<String, Object> info = readAdditive(id);
        String srtAdditiveID = String.valueOf(info.get("PRODUCT_ADDITIVE_ID"));
        Long additiveID = srtAdditiveID.equals("THE PRODUCT ADDITIVE HAS BEEN DELETED") ? null : TypeChanger.toLong(srtAdditiveID);
        return new OrderItemAdditive(
                id,
                TypeChanger.toLong(info.get("ORDER_ITEM_ID")),
                (String) info.get("PRODUCT_ADDITIVE_NAME"),
                additiveID,
                TypeChanger.toInt(info.get("PEACE_QTY")),
                TypeChanger.toInt(info.get("PRODUCT_ADDITIVE_DEFAULT_PIECE_QTY")),
                TypeChanger.toBigDecimal(info.get("PRICE_PEER_PEACE")),
                ProductPieceType.valueOf((String) info.get("PEACE_TYPE"))
        );
    }

    private static Map<String, Object> readItem(long id) {
        READ_ITEM.putInBody("id", id);
        Response response = READ_ITEM.request();
        return response.getResultAsJSON();
    }

    public static OrderItem getItem(long id) {
        Map<String, Object> info = readItem(id);
        long[] additiveIDs = TypeChanger.toSortedPrimitiveLongArray(info.get("ITEM_ADDITIVE_IDs"));
        String srtProductID = String.valueOf(info.get("PRODUCT_ID"));
        Long productID = srtProductID.equals("THE PRODUCT HAS BEEN DELETED") ? null : TypeChanger.toLong(srtProductID);
        return new OrderItem(
                id,
                TypeChanger.toLong(info.get("ORDER_ID")),
                (String) info.get("PRODUCT_NAME"),
                Arrays.stream(additiveIDs).mapToObj(OrderItemFetcher::getAdditive).toArray(OrderItemAdditive[]::new),
                productID,
                TypeChanger.toInt(info.get("PEACE_QTY")),
                TypeChanger.toBigDecimal(info.get("PRICE_PEER_PEACE")),
                ProductPieceType.valueOf((String) info.get("PEACE_TYPE"))
        );
    }

}
