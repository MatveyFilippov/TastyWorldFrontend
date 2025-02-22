package homer.tastyworld.frontend.starterpack.base.utils.ui.helpers;

import javafx.scene.layout.AnchorPane;

public class Helper {

    public static void openParentPane(AnchorPane toClose, AnchorPane toOpen) {
        if (toClose != null) {
            toClose.setVisible(false);
        }
        toOpen.setVisible(true);
    }

    public static void closeAllParentPane(AnchorPane base) {
        base.getChildren().forEach(node -> {
            if (node instanceof AnchorPane && node.getId().endsWith("Parent")) {
                node.setVisible(false);
            }
        });
    }

}
