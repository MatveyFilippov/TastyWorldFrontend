package homer.tastyworld.frontend.statusdisplay.core;

import homer.tastyworld.frontend.starterpack.base.utils.managers.tablemanager.TableNodeFactory;
import homer.tastyworld.frontend.starterpack.base.utils.ui.helpers.Text;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import java.util.HashMap;
import java.util.Map;

public class StatusDisplayTableNodeFactory implements TableNodeFactory {

    private final Map<String, Node> nodeCache = new HashMap<>();

    public static AnchorPane createNode(String name) {
        AnchorPane pane = new AnchorPane();
        Text.setTextCentre(pane, name, Text.getAdaptiveFontSize(pane, 5), null);
        return pane;
    }

    @Override
    public Node getNode(long id, String name) {
        return nodeCache.computeIfAbsent(name, key -> createNode(name));
    }

}
