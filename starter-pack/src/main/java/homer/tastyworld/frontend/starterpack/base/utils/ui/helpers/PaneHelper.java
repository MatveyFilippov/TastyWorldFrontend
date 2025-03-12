package homer.tastyworld.frontend.starterpack.base.utils.ui.helpers;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javafx.util.Duration;

public class PaneHelper {

    private static final Map<Node, Integer> nodeClickedCount = new ConcurrentHashMap<>();

    public static void setColorRoundBackground(AnchorPane pane, int radius, Color color) {
        pane.setBackground(new Background(new BackgroundFill(color, new CornerRadii(radius), Insets.EMPTY)));
    }

    public static void setImageBackgroundCentre(AnchorPane pane, InputStream img) {
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

    public static void setImageBackgroundBottom(AnchorPane pane, InputStream img) {
        BackgroundImage backgroundImage = new BackgroundImage(
                new Image(img),
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                new BackgroundPosition(
                        BackgroundPosition.CENTER.getHorizontalSide(),
                        BackgroundPosition.CENTER.getHorizontalPosition(),
                        true,
                        null,
                        1,
                        true
                ),
                new BackgroundSize(-1, -1, true, true, true, false)
                //new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, false)
        );
        pane.setBackground(new Background(backgroundImage));
    }

    public static void setOnMouseClickedWithPressingTimeChecking(Node node, Duration requiredPressingTime, EventHandler<ActionEvent> event) {
        PauseTransition delay = new PauseTransition(requiredPressingTime);
        node.setOnMousePressed(ignored -> delay.playFromStart());
        node.setOnMouseReleased(ignored -> delay.stop());
        delay.setOnFinished(event);
    }

    public static void setOnMouseClickedWithPressingCountChecking(Node node, int count, EventHandler<MouseEvent> event) {
        nodeClickedCount.put(node, 0);
        PauseTransition clickDelay = new PauseTransition(Duration.millis(500));
        clickDelay.setOnFinished(ignored -> nodeClickedCount.put(node, 0));
        node.setOnMouseClicked(mouseEvent -> {
            int clicked = nodeClickedCount.get(node) + 1;
            if (clicked >= count) {
                event.handle(mouseEvent);
                nodeClickedCount.put(node, 0);
            } else {
                clickDelay.playFromStart();
                nodeClickedCount.put(node, clicked);
            }
        });
    }

}
