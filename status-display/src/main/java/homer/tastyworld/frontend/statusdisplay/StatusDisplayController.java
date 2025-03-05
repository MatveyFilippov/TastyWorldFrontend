package homer.tastyworld.frontend.statusdisplay;

import homer.tastyworld.frontend.starterpack.api.requests.MyParams;
import homer.tastyworld.frontend.starterpack.base.utils.managers.tablemanager.TableManager;
import homer.tastyworld.frontend.starterpack.base.utils.managers.tablemanager.TableNodeFactory;
import homer.tastyworld.frontend.starterpack.base.utils.ui.helpers.Text;
import homer.tastyworld.frontend.statusdisplay.core.OrderUpdatesListener;
import homer.tastyworld.frontend.statusdisplay.core.StatusDisplayTableNodeFactory;
import javafx.beans.binding.StringExpression;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

public class StatusDisplayController {

    @FXML
    private AnchorPane cookingTopic, readyTopic;
    @FXML
    private GridPane cookingOrders, readyOrders;

    @FXML
    private void initialize() {
        StringExpression topicFontSize = Text.getAdaptiveFontSize(cookingTopic, 10);
        Text.setTextLeft(cookingTopic, "Заказ готовится", topicFontSize, null);
        Text.setTextLeft(readyTopic, "Готов к выдаче", topicFontSize, null);
        if (MyParams.getTokenSubscriptionAvailableDays() >= 0) {
            TableNodeFactory tableNodeFactory = new StatusDisplayTableNodeFactory();
            OrderUpdatesListener.init(
                    new TableManager(cookingOrders, tableNodeFactory, 5, 3),
                    new TableManager(readyOrders, tableNodeFactory, 5, 2)
            );
            // TODO: if node not displayed, problem may be with caching in factory -> make 2 factory
            // OrderUpdatesListener.init(
            //         new TableManager(cookingOrders, tableNodeFactory),
            //         new TableManager(readyOrders, new StatusDisplayTableNodeFactory())
            // );
        }
    }

}
