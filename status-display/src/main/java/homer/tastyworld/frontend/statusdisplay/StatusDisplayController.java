package homer.tastyworld.frontend.statusdisplay;

import homer.tastyworld.frontend.starterpack.api.requests.MyParams;
import homer.tastyworld.frontend.starterpack.base.utils.managers.tablemanager.TableManager;
import homer.tastyworld.frontend.starterpack.base.utils.ui.helpers.Helper;
import homer.tastyworld.frontend.starterpack.base.utils.ui.helpers.Text;
import homer.tastyworld.frontend.statusdisplay.base.OrderUpdatesListener;
import homer.tastyworld.frontend.statusdisplay.base.StatusDisplayTableNodeFactory;
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
        Text.setTextLeft(cookingTopic, "Заказ готовится", topicFontSize);
        Text.setTextLeft(readyTopic, "Готов к выдаче", topicFontSize);
        if (MyParams.getAvailableDays() >= 0) {
            OrderUpdatesListener.init(
                    new TableManager(cookingOrders, new StatusDisplayTableNodeFactory()),
                    new TableManager(readyOrders, new StatusDisplayTableNodeFactory())
            );
        }
    }

}
