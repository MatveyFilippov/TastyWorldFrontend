package homer.tastyworld.frontend.starterpack.base.utils.ui.helpers;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringExpression;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

public class AdaptiveTextHelper {

    public static StringExpression getFontSize(AnchorPane pane, double divide) {
        return Bindings.concat(
                "-fx-font-size: ",
                pane.widthProperty().divide(divide).asString("%.0f"),
                "px;"
        );
    }

    public static Label setTextLeft(AnchorPane pane, String text, StringExpression fontSize, Color color) {
        Label label = new Label(text);
        label.setWrapText(false);
        label.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        AnchorPane.setTopAnchor(label, 0.0);
        AnchorPane.setBottomAnchor(label, 0.0);
        if (color != null) {
            label.setTextFill(color);
            label.setOpacity(1.0);
        }
        label.styleProperty().bind(fontSize);
        pane.getChildren().add(label);
        return label;
    }

    public static Label setTextLeft(AnchorPane pane, String text, double divideForAdaptiveFontSize, Color color) {
        return setTextLeft(pane, text, getFontSize(pane, divideForAdaptiveFontSize), color);
    }

    public static Label setTextCentre(AnchorPane pane, String text, StringExpression fontSize, Color color) {
        Label label = setTextLeft(pane, text, fontSize, color);
        label.setTextAlignment(TextAlignment.CENTER);
        label.setAlignment(Pos.CENTER);
        AnchorPane.setLeftAnchor(label, 0.0);
        AnchorPane.setRightAnchor(label, 0.0);
        return label;
    }

    public static Label setTextCentre(AnchorPane pane, String text, double divideForAdaptiveFontSize, Color color) {
        return setTextCentre(pane, text, getFontSize(pane, divideForAdaptiveFontSize), color);
    }

}
