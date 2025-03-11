package homer.tastyworld.frontend.statusdisplay;

import homer.tastyworld.frontend.starterpack.api.requests.MyParams;
import homer.tastyworld.frontend.starterpack.base.utils.managers.table.TableManager;
import homer.tastyworld.frontend.starterpack.base.utils.managers.table.TableNodeFactory;
import homer.tastyworld.frontend.starterpack.base.utils.managers.table.cursors.DefaultTableCursor;
import homer.tastyworld.frontend.starterpack.base.utils.ui.helpers.TextHelper;
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
        StringExpression topicFontSize = TextHelper.getAdaptiveFontSize(cookingTopic, 10);
        TextHelper.setTextLeft(cookingTopic, "Заказ готовится", topicFontSize, null);
        TextHelper.setTextLeft(readyTopic, "Готов к выдаче", topicFontSize, null);
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
