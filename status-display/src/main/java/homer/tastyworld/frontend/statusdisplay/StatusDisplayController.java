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

public class StatusDisplayController {

    @FXML
    private AnchorPane cookingTopic, readyTopic;
    @FXML
    private GridPane cookingOrders, readyOrders;

    @FXML
    private void initialize() {
        StringExpression topicsFontSize = AdaptiveTextHelper.getFontSize(cookingTopic, 2);
        AdaptiveTextHelper.setTextLeft(cookingTopic, "Заказ готовится", topicsFontSize, null);
        AdaptiveTextHelper.setTextLeft(readyTopic, "Готов к выдаче", topicsFontSize, null);
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
