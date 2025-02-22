package homer.tastyworld.frontend.statusdisplay;

import homer.tastyworld.frontend.starterpack.api.requests.MyParams;
import homer.tastyworld.frontend.starterpack.base.utils.managers.tablemanager.TableManager;
import homer.tastyworld.frontend.starterpack.base.utils.ui.helpers.Helper;
import homer.tastyworld.frontend.statusdisplay.base.OrderUpdatesListener;
import homer.tastyworld.frontend.statusdisplay.base.StatusDisplayTableNodeFactory;
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
        StringExpression topicFontSize = Helper.getAdaptiveFontSize(cookingTopic, 10);
        setTopic(cookingTopic, "Заказ готовится", topicFontSize);
        setTopic(readyTopic, "Готов к выдаче", topicFontSize);
        if (MyParams.getAvailableDays() >= 0) {
            OrderUpdatesListener.init(
                    new TableManager(cookingOrders, new StatusDisplayTableNodeFactory()),
                    new TableManager(readyOrders, new StatusDisplayTableNodeFactory())
            );
        }
    }

    private void setTopic(AnchorPane topic, String name, StringExpression fontSize) {
        Label cookingLabel = new Label(name);
        topic.getChildren().add(cookingLabel);
        cookingLabel.styleProperty().bind(fontSize);
        AnchorPane.setTopAnchor(cookingLabel, 0.0);
        AnchorPane.setBottomAnchor(cookingLabel, 0.0);
    }

}
