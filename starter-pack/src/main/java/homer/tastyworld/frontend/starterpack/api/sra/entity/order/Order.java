package homer.tastyworld.frontend.starterpack.api.sra.entity.order;

import homer.tastyworld.frontend.starterpack.api.engine.Request;
import homer.tastyworld.frontend.starterpack.api.engine.Requester;
import homer.tastyworld.frontend.starterpack.api.sra.entity.misc.ProductTax;
import homer.tastyworld.frontend.starterpack.api.sra.entity.misc.ProductType;
import homer.tastyworld.frontend.starterpack.api.sra.tools.RequestUtils;
import homer.tastyworld.frontend.starterpack.api.sra.entity.misc.MenuQuantitativeMeasure;
import homer.tastyworld.frontend.starterpack.api.sra.entity.order.names.NameController;
import homer.tastyworld.frontend.starterpack.base.AppDateTime;
import homer.tastyworld.frontend.starterpack.api.sra.entity.misc.OrderStatus;
import org.apache.hc.core5.http.Method;
import org.jetbrains.annotations.Nullable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class Order {

    record GetOrderResponseBody(
            Long order_id,
            Long client_point_id,
            String name,
            OrderStatus status,
            OffsetDateTime draft_at,
            @Nullable OffsetDateTime completed_at,
            BigDecimal discount,
            Boolean is_paid,
            @Nullable OffsetDateTime paid_at,
            Boolean is_takeaway_pack,
            @Nullable String delivery_info
    ) {}

    private record DraftOrderRequestBody(
            String name,
            @Nullable BigDecimal discount,
            @Nullable Boolean is_takeaway_pack,
            @Nullable String delivery_info
    ) {}

    private record NotDefaultProductToppingToCreateOrderItemRequestBody(
            Long product_topping_id,
            Integer quantity
    ) {}

    private record GetOrderItemModifierResponseBody(
            String name,
            MenuQuantitativeMeasure qty_measure,
            Integer qty_default,
            Integer quantity,
            BigDecimal unit_price
    ) {}

    record GetOrderItemResponseBody(
            Long order_item_id,
            Long client_point_id,
            Long order_id,
            String name,
            ProductType type,
            ProductTax tax,
            @Nullable String mark,
            MenuQuantitativeMeasure qty_measure,
            Integer qty_min,
            Integer qty_max,
            Integer quantity,
            BigDecimal unit_price,
            List<GetOrderItemModifierResponseBody> modifiers
    ) {}

    private record CreateOrderItemRequestBody(
            Long order_id,
            Long product_id,
            @Nullable String mark,
            @Nullable Integer quantity,
            @Nullable List<NotDefaultProductToppingToCreateOrderItemRequestBody> not_default_product_toppings
    ) {}

    private record SetOrderItemMarkRequestBody(
            @Nullable String mark
    ) {}

    private record SetOrderItemQuantityRequestBody(
            Integer quantity
    ) {}

    private record GetOrderStatusResponseBody(
            Long order_id,
            OrderStatus status
    ) {}

    private record UpgradeOrderStatusRequestBody(
            OrderStatus new_status
    ) {}

    private record GetOrderTotalAmountResponseBody(
            Long order_id,
            BigDecimal total_amount
    ) {}

    private record GetOrderIsPaidResponseBody(
            Long order_id,
            Boolean is_paid
    ) {}

    private record EditOrderRequestBody(
            @Nullable BigDecimal discount,
            @Nullable Boolean is_takeaway_pack,
            @Nullable String delivery_info
    ) {}

    private final long orderID;
    private final String name;
    private final LocalDateTime draftAt;
    private boolean isCompleted = false;
    private LocalDateTime completedAt;
    private BigDecimal discount;
    private BigDecimal totalAmount;
    private boolean isPaid = false;
    private LocalDateTime paidAt;
    private Boolean isTakeawayPack;
    private String deliveryInfo;

    private Order(long orderID, String name, LocalDateTime draftAt) {
        this.orderID = orderID;
        this.name = name;
        this.draftAt = draftAt;
    }

    static Order of(GetOrderResponseBody getOrderResponseBody) {
        return new Order(
                getOrderResponseBody.order_id(), getOrderResponseBody.name(), AppDateTime.toLocal(getOrderResponseBody.draft_at())
        );
    }

    private static GetOrderResponseBody requestGetOrderResponseBody(long orderID) {
        Request getOrderRequest = Request.of(
                RequestUtils.getURI("/orders/" + orderID), Method.GET, RequestUtils.getBearerAuthorization()
        );
        return Requester.exchange(
                getOrderRequest,
                response -> RequestUtils.processEntityResponse(response, 200, GetOrderResponseBody.class)
        );
    }

    public static Order get(long orderID) {
        return of(requestGetOrderResponseBody(orderID));
    }

    public static Order draft(NameController nameController, @Nullable BigDecimal discount, @Nullable Boolean isTakeawayPack, @Nullable String deliveryInfo) {
        DraftOrderRequestBody draftOrderRequestBody = new DraftOrderRequestBody(
                nameController.getFreeName(), discount, isTakeawayPack, deliveryInfo
        );
        Request draftOrderRequest = Request.of(
                RequestUtils.getURI("/orders/draft"), Method.POST, RequestUtils.getBearerAuthorization(), draftOrderRequestBody
        );
        GetOrderResponseBody draftOrderResponse = Requester.exchange(
                draftOrderRequest,
                response -> RequestUtils.processEntityResponse(response, 201, GetOrderResponseBody.class)
        );
        return of(draftOrderResponse);
    }

    static OrderItem getItem(GetOrderItemResponseBody getOrderItemResponseBody) {
        OrderItemModifier[] modifiers = getOrderItemResponseBody.modifiers().stream()
                                                                .map(getOrderItemModifierResponseBody -> new OrderItemModifier(
                                                                        getOrderItemResponseBody.order_item_id(),
                                                                        getOrderItemModifierResponseBody.name(),
                                                                        getOrderItemModifierResponseBody.qty_measure(),
                                                                        getOrderItemModifierResponseBody.qty_default(),
                                                                        getOrderItemModifierResponseBody.quantity(),
                                                                        getOrderItemModifierResponseBody.unit_price()
                                                                ))
                                                                .toArray(OrderItemModifier[]::new);
        return new OrderItem(
                getOrderItemResponseBody.order_item_id(),
                getOrderItemResponseBody.order_id(),
                getOrderItemResponseBody.name(),
                getOrderItemResponseBody.type(),
                getOrderItemResponseBody.tax(),
                getOrderItemResponseBody.mark(),
                getOrderItemResponseBody.qty_measure(),
                getOrderItemResponseBody.qty_min(),
                getOrderItemResponseBody.qty_max(),
                getOrderItemResponseBody.quantity(),
                getOrderItemResponseBody.unit_price(),
                modifiers
        );
    }

    public OrderItem createItem(long productID, @Nullable String mark, @Nullable Integer quantity, @Nullable Map<Long, Integer> notDefaultProductToppingsIDaQTY) {
        List<NotDefaultProductToppingToCreateOrderItemRequestBody> notDefaultProductToppings = List.of();
        if (notDefaultProductToppingsIDaQTY != null) {
            notDefaultProductToppings = notDefaultProductToppingsIDaQTY.entrySet().stream()
                                                                       .map(entry -> new NotDefaultProductToppingToCreateOrderItemRequestBody(
                                                                               entry.getKey(), entry.getValue()
                                                                       ))
                                                                       .toList();
        }
        CreateOrderItemRequestBody createOrderItemRequestBody = new CreateOrderItemRequestBody(
                orderID, productID, mark, quantity, notDefaultProductToppings
        );
        Request createOrderItemRequest = Request.of(
                RequestUtils.getURI("/orders/items"), Method.POST, RequestUtils.getBearerAuthorization(), createOrderItemRequestBody
        );
        GetOrderItemResponseBody getOrderItemResponseBody = Requester.exchange(
                createOrderItemRequest,
                response -> RequestUtils.processEntityResponse(response, 201, GetOrderItemResponseBody.class)
        );
        return getItem(getOrderItemResponseBody);
    }

    public OrderItem[] getItems() {
        Request getAllOrderItemsRequest = Request.of(
                RequestUtils.getURI("/orders/items?order_id=" + orderID), Method.GET, RequestUtils.getBearerAuthorization()
        );
        List<GetOrderItemResponseBody> getAllOrderItemsResponseBodies = Requester.exchange(
                getAllOrderItemsRequest,
                response -> RequestUtils.processEntityArrayResponse(response, 200, GetOrderItemResponseBody.class)
        );
        return getAllOrderItemsResponseBodies.stream().map(Order::getItem).toArray(OrderItem[]::new);
    }

    public OrderItem setItemMark(OrderItem item, @Nullable String newMark) {
        SetOrderItemMarkRequestBody setOrderItemMarkRequestBody = new SetOrderItemMarkRequestBody(
                newMark
        );
        Request getEditOrderItemRequest = Request.of(
                RequestUtils.getURI("/orders/items/" + item.itemID() + "/mark"), Method.PUT, RequestUtils.getBearerAuthorization(), setOrderItemMarkRequestBody
        );
        Requester.exchange(
                getEditOrderItemRequest,
                response -> RequestUtils.processEmptyResponse(response, 204)
        );
        return item.copyWithMark(newMark);
    }

    public OrderItem setItemQuantity(OrderItem item, int newQuantity) {
        SetOrderItemQuantityRequestBody setOrderItemQuantityRequestBody = new SetOrderItemQuantityRequestBody(
                newQuantity
        );
        Request getEditOrderItemRequest = Request.of(
                RequestUtils.getURI("/orders/items/" + item.itemID() + "/quantity"), Method.PUT, RequestUtils.getBearerAuthorization(), setOrderItemQuantityRequestBody
        );
        Requester.exchange(
                getEditOrderItemRequest,
                response -> RequestUtils.processEmptyResponse(response, 204)
        );
        return item.copyWithQuantity(newQuantity);
    }

    public void deleteItem(OrderItem item) {
        Request deleteOrderItemRequest = Request.of(
                RequestUtils.getURI("/orders/items/" + item.itemID()), Method.DELETE, RequestUtils.getBearerAuthorization()
        );
        Requester.exchange(
                deleteOrderItemRequest,
                response -> RequestUtils.processEmptyResponse(response, 204)
        );
    }

    public long getOrderID() {
        return orderID;
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getDraftAt() {
        return draftAt;
    }

    public OrderStatus getStatus() {
        if (isCompleted) {
            return OrderStatus.COMPLETED;
        }

        Request getOrderStatusRequest = Request.of(
                RequestUtils.getURI("/orders/" + orderID + "/status"), Method.GET, RequestUtils.getBearerAuthorization()
        );
        GetOrderStatusResponseBody getOrderStatusResponseBody = Requester.exchange(
                getOrderStatusRequest,
                response -> RequestUtils.processEntityResponse(response, 200, GetOrderStatusResponseBody.class)
        );

        if (getOrderStatusResponseBody.status() == OrderStatus.COMPLETED) {
            isCompleted = true;
        }
        return getOrderStatusResponseBody.status();
    }

    public void upgradeStatus(OrderStatus newStatus) {
        UpgradeOrderStatusRequestBody upgradeOrderStatusRequestBody = new UpgradeOrderStatusRequestBody(
                newStatus
        );
        Request upgradeOrderStatusRequest = Request.of(
                RequestUtils.getURI("/orders/" + orderID + "/status"), Method.PUT, RequestUtils.getBearerAuthorization(), upgradeOrderStatusRequestBody
        );
        Requester.exchange(
                upgradeOrderStatusRequest,
                response -> RequestUtils.processEmptyResponse(response, 204)
        );

        if (newStatus == OrderStatus.COMPLETED) {
            isCompleted = true;
        }
    }

    public BigDecimal getTotalAmount() {
        if (totalAmount != null) {
            return totalAmount;
        }

        Request getOrderTotalAmountRequest = Request.of(
                RequestUtils.getURI("/orders/" + orderID + "/total_amount"), Method.GET, RequestUtils.getBearerAuthorization()
        );
        GetOrderTotalAmountResponseBody getOrderTotalAmountResponseBody = Requester.exchange(
                getOrderTotalAmountRequest,
                response -> RequestUtils.processEntityResponse(response, 200, GetOrderTotalAmountResponseBody.class)
        );

        if (isCompleted || isPaid) {
            totalAmount = getOrderTotalAmountResponseBody.total_amount();
        }
        return getOrderTotalAmountResponseBody.total_amount();
    }

    public boolean isPaid() {
        if (isPaid) {
            return true;
        }

        Request getOrderIsPaidRequest = Request.of(
                RequestUtils.getURI("/orders/" + orderID + "/is_paid"), Method.GET, RequestUtils.getBearerAuthorization()
        );
        GetOrderIsPaidResponseBody getOrderIsPaidResponseBody = Requester.exchange(
                getOrderIsPaidRequest,
                response -> RequestUtils.processEntityResponse(response, 200, GetOrderIsPaidResponseBody.class)
        );

        isPaid = getOrderIsPaidResponseBody.is_paid();
        return isPaid;
    }

    public void setPaid() {
        Request setOrderIsPaidRequest = Request.of(
                RequestUtils.getURI("/orders/" + orderID + "/is_paid"), Method.PUT, RequestUtils.getBearerAuthorization()
        );
        Requester.exchange(
                setOrderIsPaidRequest,
                response -> RequestUtils.processEmptyResponse(response, 204)
        );

        isPaid = true;
    }

    @Nullable
    public LocalDateTime getPaidAt() {
        if (paidAt != null) {
            return paidAt;
        }

        GetOrderResponseBody getOrderResponseBody = requestGetOrderResponseBody(orderID);

        if (getOrderResponseBody.paid_at() != null) {
            paidAt = AppDateTime.toLocal(getOrderResponseBody.paid_at());
            isPaid = true;
        }
        return paidAt;
    }

    private GetOrderResponseBody editOrder(@Nullable BigDecimal newDiscount, @Nullable Boolean newIsTakeawayPack, @Nullable String newDeliveryInfo) {
        EditOrderRequestBody editOrderRequestBody = new EditOrderRequestBody(
                newDiscount, newIsTakeawayPack, newDeliveryInfo
        );
        Request editOrderRequest = Request.of(
                RequestUtils.getURI("/orders/" + orderID), Method.PATCH, RequestUtils.getBearerAuthorization(), editOrderRequestBody
        );
        return Requester.exchange(
                editOrderRequest,
                response -> RequestUtils.processEntityResponse(response, 200, GetOrderResponseBody.class)
        );
    }

    public void setDiscount(BigDecimal newDiscount) {
        editOrder(newDiscount, null, null);
    }

    public void setTakeawayPack(boolean newIsTakeawayPack) {
        editOrder(null, newIsTakeawayPack, null);
    }

    public void setDeliveryInfo(@Nullable String newDeliveryInfo) {
        editOrder(null, null, newDeliveryInfo == null ? "" : newDeliveryInfo);
    }

    public BigDecimal getDiscount() {
        if (discount != null) {
            return discount;
        }

        GetOrderResponseBody getOrderResponseBody = requestGetOrderResponseBody(orderID);

        if (isCompleted || isPaid) {
            discount = getOrderResponseBody.discount();
        }
        return getOrderResponseBody.discount();
    }

    public boolean isTakeawayPack() {
        if (isTakeawayPack != null) {
            return isTakeawayPack;
        }

        GetOrderResponseBody getOrderResponseBody = requestGetOrderResponseBody(orderID);

        if (isCompleted) {
            isTakeawayPack = getOrderResponseBody.is_takeaway_pack();
        }
        return getOrderResponseBody.is_takeaway_pack();
    }

    @Nullable
    public String getDeliveryInfo() {
        if (deliveryInfo != null) {
            return deliveryInfo;
        }

        GetOrderResponseBody getOrderResponseBody = requestGetOrderResponseBody(orderID);

        if (isCompleted) {
            deliveryInfo = getOrderResponseBody.delivery_info();
        }
        return getOrderResponseBody.delivery_info();
    }

    @Nullable
    public LocalDateTime getCompletedAt() {
        if (completedAt != null) {
            return completedAt;
        }

        GetOrderResponseBody getOrderResponseBody = requestGetOrderResponseBody(orderID);

        if (getOrderResponseBody.completed_at() != null) {
            completedAt = AppDateTime.toLocal(getOrderResponseBody.completed_at());
            isCompleted = true;
        }
        return completedAt;
    }

    public void delete(NameController nameController) {
        Request deleteOrderRequest = Request.of(
                RequestUtils.getURI("/orders/" + orderID), Method.DELETE, RequestUtils.getBearerAuthorization()
        );
        Requester.exchange(
                deleteOrderRequest,
                response -> RequestUtils.processEmptyResponse(response, 204)
        );
        nameController.backIfCurrent(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Order order = (Order) o;
        return Objects.equals(orderID, order.orderID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderID);
    }

}
