package homer.tastyworld.frontend.starterpack.base.utils.ui.helpers;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringExpression;
import javafx.scene.layout.AnchorPane;

public class Helper {

    public static StringExpression getAdaptiveFontSize(AnchorPane pane, int divide) {
        return Bindings.concat(
                "-fx-font-size: ",
                Bindings.max(pane.widthProperty().divide(divide), pane.heightProperty().divide(divide)).asString(),
                "px;"
        );
    }

    public static void openPane(AnchorPane toClose, AnchorPane toOpen) {
        if (toClose != null) {
            toClose.setVisible(false);
        }
        toOpen.setVisible(true);
    }

}
