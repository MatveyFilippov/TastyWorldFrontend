package homer.tastyworld.frontend.pos.processor.core.queue;

import homer.tastyworld.frontend.pos.processor.core.OrderInfoPaneRenderer;
import homer.tastyworld.frontend.starterpack.base.utils.ui.helpers.AdaptiveTextHelper;
import homer.tastyworld.frontend.starterpack.base.utils.ui.helpers.PaneHelper;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class OrdersScrollQueue {

    private static class Colors {

        public static final Color WAITED = Color.web("#FF4040");
        public static final Color CHECKED = Color.BLACK;

    }

    private static int rows = 0;
    private static ScrollPane scroll;
    private static final GridPane queue = new GridPane();
    private static final Map<Long, Node> nodes = new ConcurrentHashMap<>();

    public static void init(ScrollPane scroll) {
        queue.setVgap(5);
        queue.setAlignment(Pos.CENTER);
        scroll.setContent(queue);
        OrdersScrollQueue.scroll = scroll;
        OrderUpdatesListener.init();
    }

    private static Node getNewClickableOrder(long orderID, String name) {
        AnchorPane row = new AnchorPane();
        row.setStyle("-fx-border-color: #000000;");
        row.prefWidthProperty().bind(scroll.widthProperty());
        row.prefHeightProperty().bind(scroll.heightProperty().divide(10));
        AdaptiveTextHelper.setTextCentre(row, name, 2.5, Colors.WAITED);
        PaneHelper.setOnMouseClickedWithPressingCountChecking(row, 2, event -> OrderInfoPaneRenderer.render(orderID));
        return row;
    }

    private static void setColor(long orderID, Color color) {
        AnchorPane order = (AnchorPane) nodes.get(orderID);
        if (order != null) {
            ((Label) order.getChildren().getLast()).setTextFill(color);
        }
    }

    public static void setWaited(long orderID) {
        setColor(orderID, Colors.WAITED);
    }

    public static void setChecked(long orderID) {
        setColor(orderID, Colors.CHECKED);
    }

    protected static void putIfNotExists(long orderID, String name) {
        if (!nodes.containsKey(orderID)) {
            Node row = getNewClickableOrder(orderID, name);
            queue.add(row, 0, rows);
            nodes.put(orderID, row);
            rows++;
        } else {
            setWaited(orderID);
        }
    }

    protected static void removeIfExists(long orderID) {
        Node order = nodes.remove(orderID);
        if (order != null) {
            queue.getChildren().remove(order);
            rows--;
        }
    }

}
