package homer.tastyworld.frontend.statusdisplay.base;

import homer.tastyworld.frontend.starterpack.base.utils.managers.tablemanager.TableNodeFactory;
import homer.tastyworld.frontend.starterpack.base.utils.ui.helpers.Helper;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.TextAlignment;
import java.util.HashMap;
import java.util.Map;

public class StatusDisplayTableNodeFactory implements TableNodeFactory {

    private final Map<String, Node> nodeCache = new HashMap<>();

    public StatusDisplayTableNodeFactory() {}

    public static AnchorPane createNode(String name) {
        AnchorPane pane = new AnchorPane();
        Label label = new Label(name);
        label.setTextAlignment(TextAlignment.CENTER);
        label.setAlignment(Pos.CENTER);
        label.setWrapText(true);
        AnchorPane.setTopAnchor(label, 0.0);
        AnchorPane.setBottomAnchor(label, 0.0);
        AnchorPane.setLeftAnchor(label, 0.0);
        AnchorPane.setRightAnchor(label, 0.0);
        label.styleProperty().bind(Helper.getAdaptiveFontSize(pane, 5));
        pane.getChildren().add(label);
        return pane;
    }

    @Override
    public Node getNode(long id, String name) {
        return nodeCache.computeIfAbsent(name, key -> createNode(name));
    }

}
