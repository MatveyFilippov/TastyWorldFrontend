package homer.tastyworld.frontend.starterpack.base.utils.ui.helpers;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringExpression;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

public class Text {

    public static StringExpression getAdaptiveFontSize(AnchorPane pane, int divide) {
        return Bindings.concat(
                "-fx-font-size: ",
                Bindings.max(pane.widthProperty().divide(divide), pane.heightProperty().divide(divide)).asString("%.0f"),
                "px;"
        );
    }

    public static void setTextLeft(AnchorPane pane, String text, StringExpression fontSize, Color color) {
        Label label = new Label(text);
        label.setWrapText(false);
        label.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        AnchorPane.setTopAnchor(label, 0.0);
        AnchorPane.setBottomAnchor(label, 0.0);
        if (color != null) {
            label.setTextFill(color);
        }
        label.styleProperty().bind(fontSize);
        pane.getChildren().add(label);
    }

    public static void setTextCentre(AnchorPane pane, String text, StringExpression fontSize, Color color) {
        Label label = new Label(text);
        label.setTextAlignment(TextAlignment.CENTER);
        label.setAlignment(Pos.CENTER);
        label.setWrapText(false);
        label.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        AnchorPane.setTopAnchor(label, 0.0);
        AnchorPane.setBottomAnchor(label, 0.0);
        AnchorPane.setLeftAnchor(label, 0.0);
        AnchorPane.setRightAnchor(label, 0.0);
        if (color != null) {
            label.setTextFill(color);
        }
        label.styleProperty().bind(fontSize);
        pane.getChildren().add(label);
    }

}
