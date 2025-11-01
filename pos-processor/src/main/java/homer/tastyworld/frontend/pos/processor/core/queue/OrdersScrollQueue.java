package homer.tastyworld.frontend.pos.processor.core.queue;

import homer.tastyworld.frontend.pos.processor.core.OrderInfoPaneRenderer;
import homer.tastyworld.frontend.starterpack.utils.ui.AlertWindows;
import homer.tastyworld.frontend.starterpack.utils.ui.helpers.AdaptiveTextHelper;
import homer.tastyworld.frontend.starterpack.utils.ui.helpers.PaneHelper;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class OrdersScrollQueue {

    private static ScrollPane scroll;
    private static ObservableList<Node> queue;
    private static final Map<Long, AnchorPane> orders = new ConcurrentHashMap<>();
    private static long selectedOrderID = -1;
    private static AnchorPane emptyQueueHint;

    public static void init(ScrollPane scroll) {
        VBox rows = new VBox(5);
        rows.setFillWidth(true);
        rows.setAlignment(Pos.CENTER);
        scroll.setContent(rows);
        OrdersScrollQueue.scroll = scroll;
        OrdersScrollQueue.queue = rows.getChildren();
        queue.add(getEmptyQueueHint());
        OrderUpdatesListener.init();
    }

    private static AnchorPane getEmptyQueueHint() {
        if (emptyQueueHint == null) {
            emptyQueueHint = new AnchorPane();
            emptyQueueHint.getStyleClass().add("empty-clickable-order-hint-in-queue");
            emptyQueueHint.prefWidthProperty().bind(scroll.widthProperty());
            emptyQueueHint.prefHeightProperty().bind(scroll.heightProperty().multiply(0.1));
            AdaptiveTextHelper.setTextCentre(emptyQueueHint, "Заказов пока нет", 0.1, null);
            emptyQueueHint.setOnMouseClicked(event -> AlertWindows.showInfo(
                    "Заказов пока нет", "Все заказы выполнены, новых задач не получено", true
            ));
        }
        return emptyQueueHint;
    }

    private static AnchorPane getNewClickableOrder(long orderID, String name) {
        AnchorPane order = new AnchorPane();
        order.getStyleClass().add("clickable-order-in-queue");
        order.prefWidthProperty().bind(scroll.widthProperty());
        order.prefHeightProperty().bind(scroll.heightProperty().multiply(0.1));
        AdaptiveTextHelper.setTextCentre(order, name, 0.4, null);
        PaneHelper.setOnMouseClickedWithPressingCountChecking(order, 2, event -> select(orderID));
        return order;
    }

    protected static void select(long orderID) {
        AnchorPane selectedOrder = orders.get(selectedOrderID);
        if (selectedOrder != null) {
            OrderInfoPaneRenderer.cleanIfFilled(orderID);
            selectedOrder.getStyleClass().remove("selected");
        }
        selectedOrder = orders.get(orderID);
        OrderInfoPaneRenderer.render(orderID);
        selectedOrder.getStyleClass().add("selected");
        selectedOrderID = orderID;
    }

    protected static void setPreparing(long orderID) {
        AnchorPane order = orders.get(orderID);
        order.getStyleClass().add("preparing");
    }

    protected static void putIfNotExists(long orderID, String name) {
        if (!orders.containsKey(orderID)) {
            queue.remove(getEmptyQueueHint());
            AnchorPane order = getNewClickableOrder(orderID, name);
            queue.add(order);
            orders.put(orderID, order);
        }
    }

    protected static void removeIfExists(long orderID) {
        Node order = orders.remove(orderID);
        if (order != null) {
            queue.remove(order);
            OrderInfoPaneRenderer.cleanIfFilled(orderID);
            if (queue.isEmpty()) {
                queue.add(getEmptyQueueHint());
            }
        }
    }

}
