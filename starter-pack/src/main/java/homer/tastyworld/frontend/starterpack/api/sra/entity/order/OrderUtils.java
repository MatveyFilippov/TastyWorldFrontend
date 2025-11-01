package homer.tastyworld.frontend.starterpack.api.sra.entity.order;

import homer.tastyworld.frontend.starterpack.api.engine.Request;
import homer.tastyworld.frontend.starterpack.api.engine.Requester;
import homer.tastyworld.frontend.starterpack.api.sra.tools.RequestUtils;
import homer.tastyworld.frontend.starterpack.base.AppDateTime;
import org.apache.hc.core5.http.Method;
import org.jetbrains.annotations.Nullable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class OrderUtils {

    private static Order lruOrder;

    public static Order[] getAllNotCompletedOrders() {
        Request getAllNotCompletedOrdersRequest = Request.of(
                RequestUtils.getURI("/orders/not_completed"), Method.GET, RequestUtils.getBearerAuthorization()
        );
        List<Order.GetOrderResponseBody> getAllNotCompletedOrdersResponseBodies = Requester.exchange(
                getAllNotCompletedOrdersRequest,
                response -> RequestUtils.processEntityArrayResponse(response, 200, Order.GetOrderResponseBody.class)
        );
        return getAllNotCompletedOrdersResponseBodies.stream()
                                                     .map(Order::of)
                                                     .toArray(Order[]::new);
    }

    public static Order[] getCompletedOrdersForPeriod(LocalDateTime from, @Nullable LocalDateTime till) {
        String endpoint = "/orders/completed?from=" + AppDateTime.toLocal(from).format(DateTimeFormatter.ISO_DATE_TIME);
        if (till != null) {
            endpoint += "&till=" + AppDateTime.toLocal(till).format(DateTimeFormatter.ISO_DATE_TIME);
        }
        Request getCompletedOrdersForPeriodRequest = Request.of(
                RequestUtils.getURI(endpoint), Method.GET, RequestUtils.getBearerAuthorization()
        );
        List<Order.GetOrderResponseBody> getCompletedOrdersForPeriodResponseBodies = Requester.exchange(
                getCompletedOrdersForPeriodRequest,
                response -> RequestUtils.processEntityArrayResponse(response, 200, Order.GetOrderResponseBody.class)
        );
        return getCompletedOrdersForPeriodResponseBodies.stream()
                                                        .map(Order::of)
                                                        .toArray(Order[]::new);
    }

    public static Order getOrCreateInstance(long orderID) {
        if (lruOrder == null || lruOrder.getOrderID() != orderID) {
            lruOrder = Order.get(orderID);
        }
        return lruOrder;
    }

    public static OrderItem getItem(long itemID) {
        Request getOrderItemRequest = Request.of(
                RequestUtils.getURI("/orders/items/" + itemID), Method.GET, RequestUtils.getBearerAuthorization()
        );
        Order.GetOrderItemResponseBody getOrderItemResponseBody = Requester.exchange(
                getOrderItemRequest,
                response -> RequestUtils.processEntityResponse(response, 200, Order.GetOrderItemResponseBody.class)
        );
        return Order.getItem(getOrderItemResponseBody);
    }

}
