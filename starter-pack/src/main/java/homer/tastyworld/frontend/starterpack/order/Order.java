package homer.tastyworld.frontend.starterpack.order;

import homer.tastyworld.frontend.starterpack.base.AppDateTime;
import homer.tastyworld.frontend.starterpack.base.exceptions.response.BadRequestException;
import homer.tastyworld.frontend.starterpack.base.utils.misc.TypeChanger;
import homer.tastyworld.frontend.starterpack.entity.misc.OrderStatus;
import homer.tastyworld.frontend.starterpack.order.core.PreparedRequests;
import homer.tastyworld.frontend.starterpack.order.core.items.OrderItem;
import homer.tastyworld.frontend.starterpack.order.core.items.OrderItemFetcher;
import homer.tastyworld.frontend.starterpack.order.core.names.NameController;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;

public class Order {

    public final long id;
    public final String name;
    public final LocalDateTime createdAt;
    private LocalDateTime closedAt = null;
    private LocalDateTime paidAt = null;
    private boolean isClosed = false;
    private BigDecimal totalPrice = null;
    private boolean isPaid = false;
    private String deliveryInfo = "";
    private OrderItem[] items = null;

    private Order(long id, String name, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
    }

    public long appendItem(long productID, Integer pieceQTY, Map<Long, Integer> netDefaultAdditives) {
        return PreparedRequests.appendItem(id, productID, pieceQTY, netDefaultAdditives);
    }

    public OrderItem[] getItems() {
        if ((isClosed || isPaid) && items != null) {
            return items;
        }
        long[] itemIDs = PreparedRequests.getItemIDs(id);
        items = Arrays.stream(itemIDs).mapToObj(OrderItemFetcher::getItem).toArray(OrderItem[]::new);
        return items;
    }

    public void editItem(long itemID, int newQTY) {
        PreparedRequests.editItem(itemID, newQTY);
    }

    public void editItemAdditive(long additiveID, int newQTY) {
        PreparedRequests.editItemAdditive(additiveID, newQTY);
    }

    public void removeItem(long itemID) {
        PreparedRequests.removeItem(itemID);
    }

    public OrderStatus getStatus() {
        if (isClosed) {
            return OrderStatus.CLOSED;
        }
        OrderStatus status = PreparedRequests.getStatus(id);
        if (status == OrderStatus.CLOSED) {
            isClosed = true;
        }
        return status;
    }

    public void setStatus(OrderStatus status) {
        if (status == OrderStatus.CLOSED) {
            PreparedRequests.close(id);
            isClosed = true;
        } else {
            PreparedRequests.setStatus(id, status);
        }
    }

    public BigDecimal getTotalPrice() {
        if ((isClosed || isPaid) && totalPrice != null) {
            return totalPrice;
        }
        totalPrice = PreparedRequests.getTotalPrice(id);
        return totalPrice;
    }

    public boolean isPaid() {
        if (isPaid) {
            return true;
        }
        isPaid = PreparedRequests.isPaid(id);
        return isPaid;
    }

    public LocalDateTime getPaidAt() {
        if (paidAt != null) {
            return paidAt;
        }
        String strPaidAt = PreparedRequests.getPaidAt(id);
        if (strPaidAt.equals("NOT PAID YET")) {
            return null;
        }
        paidAt = AppDateTime.backendToLocal(AppDateTime.parseDateTime(strPaidAt));
        return paidAt;
    }

    public void markAsPaid() {
        PreparedRequests.markAsPaid(id);
        isPaid = true;
    }

    public String getDeliveryInfo() {
        if (isClosed || isPaid) {
            return deliveryInfo;
        }
        deliveryInfo = PreparedRequests.getDeliveryInfo(id);
        if (deliveryInfo.equals("NOT FOR DELIVERY")) {
            deliveryInfo = null;
        }
        return deliveryInfo;
    }

    public void editDeliveryInfo(String info) {
        PreparedRequests.editDeliveryInfo(id, info);
    }

    public LocalDateTime getClosedAt() {
        if (closedAt != null) {
            return closedAt;
        }
        String strClosedAt = PreparedRequests.getClosedAt(id);
        if (strClosedAt.equals("NOT CLOSED YET")) {
            return null;
        }
        closedAt = AppDateTime.backendToLocal(AppDateTime.parseDateTime(strClosedAt));
        return closedAt;
    }

    public void delete(NameController nameController) {
        nameController.back();
        PreparedRequests.delete(id);
    }

    public static Order get(long id) {
        Map<String, Object> result = PreparedRequests.fullRead(id);
        return new Order(
                id,
                (String) result.get("NAME"),
                AppDateTime.backendToLocal(AppDateTime.parseDateTime((String) result.get("CREATED_AT")))
        );
    }

    public static Order create(NameController nameController, String deliveryInfo) {
        long newID = PreparedRequests.create(nameController.getFreeName(), deliveryInfo);
        return get(newID);
    }

}
