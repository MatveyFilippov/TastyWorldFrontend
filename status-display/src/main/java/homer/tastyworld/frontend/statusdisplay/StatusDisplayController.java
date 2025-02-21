package homer.tastyworld.frontend.statusdisplay;

import homer.tastyworld.frontend.starterpack.utils.AppLogger;
import homer.tastyworld.frontend.starterpack.utils.config.AppConfig;
import homer.tastyworld.frontend.starterpack.utils.misc.SHA256;
import homer.tastyworld.frontend.statusdisplay.base.tablemanager.TableManager;
import homer.tastyworld.frontend.statusdisplay.base.updater.OrderUpdatesListener;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringExpression;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

public class StatusDisplayController {

    @FXML
    private AnchorPane cookingTopic, readyTopic;
    @FXML
    private GridPane cookingOrders, readyOrders;

    @FXML
    private void initialize() {
        StringExpression topicFontSize = getTopicFontSize(cookingTopic);
        setTopic(cookingTopic, "Заказ готовится", topicFontSize);
        setTopic(readyTopic, "Готов к выдаче", topicFontSize);
        OrderUpdatesListener.init(new TableManager(cookingOrders), new TableManager(readyOrders));
    }

    private StringExpression getTopicFontSize(AnchorPane topic) {
        return Bindings.concat(
                "-fx-font-size: ",
                topic.widthProperty().divide(10).asString(),
                "px;"
        );
    }

    private void setTopic(AnchorPane topic, String name, StringExpression fontSize) {
        Label cookingLabel = new Label(name);
        topic.getChildren().add(cookingLabel);
        cookingLabel.styleProperty().bind(fontSize);
        AnchorPane.setTopAnchor(cookingLabel, 0.0);
        AnchorPane.setBottomAnchor(cookingLabel, 0.0);
    }

}