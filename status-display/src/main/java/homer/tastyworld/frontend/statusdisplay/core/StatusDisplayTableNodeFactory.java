package homer.tastyworld.frontend.statusdisplay.core;

import homer.tastyworld.frontend.starterpack.base.utils.managers.table.TableNodeFactory;
import homer.tastyworld.frontend.starterpack.base.utils.ui.helpers.AdaptiveTextHelper;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import java.util.HashMap;
import java.util.Map;
import javafx.scene.layout.Region;

public class StatusDisplayTableNodeFactory implements TableNodeFactory {

    private final Map<String, Node> nodeCache = new HashMap<>();

    private AnchorPane createNode(String name) {
        AnchorPane pane = new AnchorPane();
        AdaptiveTextHelper.setTextCentre(pane, name, 1.5, Color.BLACK);
        pane.setOpacity(1.0);
        pane.setMinSize(0, 0);
        pane.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        pane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        GridPane.setFillHeight(pane, true);
        GridPane.setFillWidth(pane, true);
        return pane;
    }

    @Override
    public Node getNode(long id, String name) {
        return nodeCache.computeIfAbsent(name, key -> createNode(name));
    }

}
