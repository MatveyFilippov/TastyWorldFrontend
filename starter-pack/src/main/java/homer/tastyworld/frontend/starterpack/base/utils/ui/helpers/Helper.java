package homer.tastyworld.frontend.starterpack.base.utils.ui.helpers;

import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import java.io.InputStream;

public class Helper {

    public static void setAnchorPaneColorRoundBackground(AnchorPane pane, int radius, Color color) {
        pane.setBackground(new Background(new BackgroundFill(color, new CornerRadii(radius), Insets.EMPTY)));
    }

    public static void setAnchorPaneImageBackground(AnchorPane pane, InputStream img) {
        BackgroundImage backgroundImage = new BackgroundImage(
                new Image(img),
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(-1, -1, true, true, true, false)
                //new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, false)
        );
        pane.setBackground(new Background(backgroundImage));
    }

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
