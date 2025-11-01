package homer.tastyworld.frontend.starterpack.utils.ui.vkb;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.Nullable;

public class VirtualKeyboard {

    private static VBox getRoot(AnchorPane keyboardPlace) {
        VBox root = new VBox(10);
        root.setFillWidth(true);
        root.setAlignment(Pos.CENTER);
        root.prefWidthProperty().bind(keyboardPlace.widthProperty());
        root.prefHeightProperty().bind(keyboardPlace.heightProperty());
        root.setPadding(new Insets(15));
        AnchorPane.setLeftAnchor(root, 0.0);
        AnchorPane.setRightAnchor(root, 0.0);
        AnchorPane.setTopAnchor(root, 0.0);
        AnchorPane.setBottomAnchor(root, 0.0);
        return root;
    }

    private static HBox getRow(VBox root, int rowsQTY) {
        HBox row = new HBox(15);
        row.setFillHeight(true);
        row.setAlignment(Pos.CENTER);
        row.prefWidthProperty().bind(root.widthProperty());
        row.prefHeightProperty().bind(root.heightProperty().divide(rowsQTY + 2));
        return row;
    }

    public static void addTo(VBox root, @Nullable Node keyTarget) {
        AnchorPane[][] keys = VirtualKeyboardKeys.createKeys(root, keyTarget);
        int rowsQTY = keys.length + 1;
        for (AnchorPane[] keysRow : keys) {
            HBox row = getRow(root, rowsQTY);
            for (AnchorPane key : keysRow) {
                key.prefWidthProperty().bind(row.widthProperty().divide(keysRow.length + 3));
                key.prefHeightProperty().bind(row.heightProperty());
                row.getChildren().add(key);
            }
            root.getChildren().add(row);
        }
        HBox spaceRow = getRow(root, rowsQTY);
        AnchorPane spaceKey = VirtualKeyboardKeys.createSpaceKey(root, keyTarget);
        spaceKey.prefWidthProperty().bind(spaceRow.widthProperty().multiply(0.9));
        spaceKey.prefHeightProperty().bind(spaceRow.heightProperty());
        spaceRow.getChildren().add(spaceKey);
        root.getChildren().add(spaceRow);
    }

    public static void addTo(AnchorPane keyboardPlace, @Nullable Node keyTarget) {
        VBox root = getRoot(keyboardPlace);
        addTo(root, keyTarget);
        keyboardPlace.getChildren().add(root);
    }

}
