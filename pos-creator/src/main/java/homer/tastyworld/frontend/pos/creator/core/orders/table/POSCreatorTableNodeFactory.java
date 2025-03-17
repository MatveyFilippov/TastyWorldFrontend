package homer.tastyworld.frontend.pos.creator.core.orders.table;

import homer.tastyworld.frontend.pos.creator.panes.dynamic.DynamicParentPane;
import homer.tastyworld.frontend.pos.creator.core.orders.internal.OrderLooking;
import homer.tastyworld.frontend.starterpack.base.utils.managers.table.TableNodeFactory;
import homer.tastyworld.frontend.starterpack.base.utils.ui.helpers.AdaptiveTextHelper;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;

public class POSCreatorTableNodeFactory implements TableNodeFactory {

    private final DynamicParentPane lookOrderPane;

    public POSCreatorTableNodeFactory(DynamicParentPane lookOrderPane) {
        this.lookOrderPane = lookOrderPane;
    }

    private void setOnTouch(Node node, long orderID) {
        node.setOnMouseClicked(event -> {
            lookOrderPane.fill(orderID);
            OrderLooking.start(orderID);
            lookOrderPane.getParent().setVisible(true);
        });
    }

    private AnchorPane createNode(String name) {
        AnchorPane pane = new AnchorPane();
        AdaptiveTextHelper.setTextCentre(pane, name, 2.5, null);
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
