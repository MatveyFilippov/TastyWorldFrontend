package homer.tastyworld.frontend.poscreator.core.orders.table;

import homer.tastyworld.frontend.poscreator.core.orders.internal.OrderLooking;
import homer.tastyworld.frontend.poscreator.panes.dynamic.DynamicParentPane;
import homer.tastyworld.frontend.starterpack.base.utils.managers.tablemanager.TableNodeFactory;
import homer.tastyworld.frontend.starterpack.base.utils.ui.helpers.Helper;
import homer.tastyworld.frontend.starterpack.base.utils.ui.helpers.Text;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.util.Duration;

public class POSCreatorTableNodeFactory implements TableNodeFactory {

    private final DynamicParentPane lookOrderPane;

    public POSCreatorTableNodeFactory(DynamicParentPane lookOrderPane) {
        this.lookOrderPane = lookOrderPane;
    }

    private void setOnTouch(Node node, long orderID) {
        Helper.setOnMouseClickedWithPressingTimeChecking(node, Duration.millis(50), event -> {
            lookOrderPane.fill(orderID);
            OrderLooking.start(orderID);
            lookOrderPane.getParent().setVisible(true);
        });
    }

    private AnchorPane createNode(String name) {
        AnchorPane pane = new AnchorPane();
        Text.setTextCentre(pane, name, Text.getAdaptiveFontSize(pane, 3), null);
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
