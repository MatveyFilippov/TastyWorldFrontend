package homer.tastyworld.frontend.pos.processor.core;

import homer.tastyworld.frontend.pos.processor.core.helpers.OrderInfoPaneRenderer;
import homer.tastyworld.frontend.starterpack.api.Request;
import homer.tastyworld.frontend.starterpack.api.notifications.Subscriber;
import homer.tastyworld.frontend.starterpack.api.notifications.Theme;
import homer.tastyworld.frontend.starterpack.api.requests.MyParams;
import homer.tastyworld.frontend.starterpack.base.exceptions.response.BadRequestException;
import homer.tastyworld.frontend.starterpack.base.utils.ui.helpers.AdaptiveTextHelper;
import homer.tastyworld.frontend.starterpack.base.utils.ui.helpers.PaneHelper;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import org.apache.hc.core5.http.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class OrderUpdatesListener {

    protected static class Colors {

        public static Color LOOKED = Color.BLACK;
        public static Color NOT_LOOKED = Color.web("#FF4040");

    }

    private static int rows = 0;
    private static ScrollPane scroll;
    private static final GridPane ordersList = new GridPane();
    private static final Map<Long, Node> ordersNode = new ConcurrentHashMap<>();
    private static final Request READ_ORDER_REQUEST = new Request("/order/read", Method.GET);

    static {
        ordersList.setVgap(5);
        ordersList.setAlignment(Pos.CENTER);
    }

    public static void init(ScrollPane scroll) {
        scroll.setContent(ordersList);
        OrderUpdatesListener.scroll = scroll;
        Arrays.stream(MyParams.getActiveOrders()).forEach(OrderUpdatesListener::process);
        Subscriber.subscribe(Theme.ORDER_STATUS_CHANGED, orderID -> process(Long.parseLong(orderID)));
    }

    public static void process(long orderID) {
        READ_ORDER_REQUEST.putInBody("id", orderID);
        Map<String, Object> orderInfo;
        String status = "NOT_EXISTS";
        String name = "UNKNOWN";
        try {
            orderInfo = READ_ORDER_REQUEST.request().getResultAsJSON();
            status = (String) orderInfo.get("STATUS");
            name = (String) orderInfo.get("NAME");
        } catch (BadRequestException ignored) {}
        switch (status) {
            case "CREATED": put(orderID, name); break;
            case "PROCESSING": putIfNotExists(orderID, name); break;
            default: remove(orderID); break;
        }
    }

    private static void put(long orderID, String name) {
        putIfNotExists(orderID, name);
        OrderActions.setProcessing(orderID);
    }

    private static void putIfNotExists(long orderID, String name) {
        if (!ordersNode.containsKey(orderID)) {
            Node row = getClickableOrder(orderID, name);
            ordersList.add(row, 0, rows);
            ordersNode.put(orderID, row);
            rows++;
        }
    }

    private static void remove(long orderID) {
        OrderInfoPaneRenderer.cleanIfFilled(orderID);
        Node order = ordersNode.remove(orderID);
        if (order != null) {
            ordersList.getChildren().remove(order);
            rows--;
        }
    }

    protected static void setColor(long orderID, Color color) {
        AnchorPane order = (AnchorPane) ordersNode.get(orderID);
        if (order != null) {
            ((Label) order.getChildren().getLast()).setTextFill(color);
        }
    }

    private static Node getClickableOrder(long orderID, String name) {
        AnchorPane row = new AnchorPane();
        row.setStyle("-fx-border-color: #000000;");
        row.prefWidthProperty().bind(scroll.widthProperty());
        row.prefHeightProperty().bind(scroll.heightProperty().divide(10));
        AdaptiveTextHelper.setTextCentre(row, name, 2.5, Colors.NOT_LOOKED);
        PaneHelper.setOnMouseClickedWithPressingCountChecking(row, 2, event -> OrderInfoPaneRenderer.render(orderID));
        return row;
    }

}
