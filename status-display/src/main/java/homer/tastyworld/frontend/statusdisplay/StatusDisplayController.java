package homer.tastyworld.frontend.statusdisplay;

import homer.tastyworld.frontend.starterpack.api.requests.MyParams;
import homer.tastyworld.frontend.starterpack.base.utils.managers.table.TableManager;
import homer.tastyworld.frontend.starterpack.base.utils.managers.table.TableNodeFactory;
import homer.tastyworld.frontend.starterpack.base.utils.managers.table.cursors.DefaultTableCursor;
import homer.tastyworld.frontend.starterpack.base.utils.ui.helpers.AdaptiveTextHelper;
import homer.tastyworld.frontend.statusdisplay.core.OrderUpdatesListener;
import homer.tastyworld.frontend.statusdisplay.core.StatusDisplayTableNodeFactory;
import javafx.beans.binding.StringExpression;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

public class StatusDisplayController {

    @FXML
    private AnchorPane cookingTopic, readyTopic;
    @FXML
    private GridPane cookingOrders, readyOrders;

    @FXML
    private void initialize() {
        StringExpression topicsFontSize = AdaptiveTextHelper.getFontSize(cookingTopic, 10);
        Color topicsColor = Color.web("#999999");
        AdaptiveTextHelper.setTextLeft(cookingTopic, "Заказ готовится", topicsFontSize, topicsColor);
        AdaptiveTextHelper.setTextLeft(readyTopic, "Готов к выдаче", topicsFontSize, topicsColor);
        cookingOrders.setAlignment(Pos.CENTER);
        readyOrders.setAlignment(Pos.CENTER);
        if (MyParams.getTokenSubscriptionAvailableDays() >= 0) {
            TableNodeFactory tableNodeFactory = new StatusDisplayTableNodeFactory();
            OrderUpdatesListener.init(
                    new TableManager(cookingOrders, new DefaultTableCursor(5, 3), tableNodeFactory),
                    new TableManager(readyOrders, new DefaultTableCursor(5, 2), tableNodeFactory)
            );
        }
    }

}
