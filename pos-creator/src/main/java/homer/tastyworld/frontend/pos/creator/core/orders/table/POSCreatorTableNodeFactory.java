package homer.tastyworld.frontend.pos.creator.core.orders.table;

import homer.tastyworld.frontend.pos.creator.core.orders.table.listeners.OrderPaidMarkUpdateListener;
import homer.tastyworld.frontend.pos.creator.panes.ParentPane;
import homer.tastyworld.frontend.starterpack.api.sra.entity.order.Order;
import homer.tastyworld.frontend.starterpack.api.sra.entity.order.OrderUtils;
import homer.tastyworld.frontend.starterpack.utils.managers.table.TableNodeFactory;
import homer.tastyworld.frontend.starterpack.utils.ui.helpers.AdaptiveTextHelper;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;

public class POSCreatorTableNodeFactory implements TableNodeFactory {

    private final ParentPane<Order> lookOrderPane;

    public POSCreatorTableNodeFactory(ParentPane<Order> lookOrderPane) {
        this.lookOrderPane = lookOrderPane;
    }

    private void setOnTouch(Node node, long orderID) {
        node.setOnMouseClicked(e -> lookOrderPane.open(OrderUtils.getOrCreateInstance(orderID)));
    }

    private AnchorPane createNode(String name) {
        AnchorPane pane = new AnchorPane();
        AdaptiveTextHelper.setTextCentre(pane, name, 0.4, null);
        pane.setOpacity(1.0);
        pane.setMinSize(0, 0);
        pane.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        pane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        GridPane.setFillHeight(pane, true);
        GridPane.setFillWidth(pane, true);
        return pane;
    }

    @Override
    public Node getNode(long orderID, String name) {
        AnchorPane node = createNode(name);
        setOnTouch(node, orderID);
        OrderPaidMarkUpdateListener.addWaiter(orderID, (Label) node.getChildren().getLast());
        return node;
    }

}
