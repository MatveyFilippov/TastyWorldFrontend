package homer.tastyworld.frontend.poscreator.core.orders.table;

import homer.tastyworld.frontend.starterpack.base.utils.managers.tablemanager.TableNodeFactory;
import homer.tastyworld.frontend.starterpack.base.utils.ui.helpers.Text;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

public class POSCreatorTableNodeFactory implements TableNodeFactory {

    private static void setOnTouch(Node node, long orderID) {
        node.setOnMouseClicked(event -> System.out.println(orderID));
    }

    public static AnchorPane createNode(String name) {
        AnchorPane pane = new AnchorPane();
        Text.setTextCentre(pane, name, Text.getAdaptiveFontSize(pane, 5), Color.web("#FF4040"));
        pane.setOpacity(1.0);
        pane.getChildren().getFirst().setOpacity(1.0);
        pane.setMinSize(0, 0);
        pane.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        pane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
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
