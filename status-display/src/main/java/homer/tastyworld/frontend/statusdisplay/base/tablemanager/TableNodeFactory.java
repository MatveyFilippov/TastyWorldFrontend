package homer.tastyworld.frontend.statusdisplay.base.tablemanager;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringExpression;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.TextAlignment;
import java.util.HashMap;
import java.util.Map;

class TableNodeFactory {

    private final Map<String, Node> nodeCache = new HashMap<>();

    public TableNodeFactory() {}

    private static StringExpression getItemFontSize(AnchorPane pane) {
        return Bindings.concat(
                "-fx-font-size: ",
                Bindings.min(pane.widthProperty().divide(3), pane.heightProperty().divide(3)).asString(),
                "px;"
        );
    }

    public static AnchorPane createNode(String name) {
        AnchorPane pane = new AnchorPane();
        Label label = new Label(name);
        label.setTextAlignment(TextAlignment.CENTER);
        label.setAlignment(Pos.CENTER);
        AnchorPane.setTopAnchor(label, 0.0);
        AnchorPane.setBottomAnchor(label, 0.0);
        AnchorPane.setLeftAnchor(label, 0.0);
        AnchorPane.setRightAnchor(label, 0.0);
        label.styleProperty().bind(getItemFontSize(pane));
        pane.getChildren().add(label);
        return pane;
    }

    public Node getNode(String name) {
        return nodeCache.computeIfAbsent(name, key -> createNode(name));
    }

}
