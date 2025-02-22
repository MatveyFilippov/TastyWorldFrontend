package homer.tastyworld.frontend.poscreator;

import homer.tastyworld.frontend.starterpack.api.requests.MyParams;
import homer.tastyworld.frontend.starterpack.base.exceptions.SubscriptionDaysAreOverError;
import homer.tastyworld.frontend.starterpack.base.utils.ui.AlertWindow;
import homer.tastyworld.frontend.starterpack.base.utils.ui.helpers.Text;
import javafx.beans.binding.StringExpression;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

public class POSCreatorController {

    @FXML
    private AnchorPane base;

    @FXML
    private AnchorPane mainPaneParent;
    @FXML
    private GridPane mainPanePositions;
    @FXML
    private AnchorPane mainPaneDaysLeftAlert, mainPaneDaysLeftAlertTopic;
    @FXML
    private GridPane mainPaneCookingOrdersTable, mainPaneReadyOrdersTable;
    @FXML
    private AnchorPane mainPaneCookingOrdersTopic, mainPaneReadyOrdersTopic;

    @FXML
    private void initialize() {
        long availableDays = MyParams.getAvailableDays();
        if (availableDays < 0) {
            throw new SubscriptionDaysAreOverError();
        }
        initMainPane(availableDays);
    }

    private void initMainPane(long availableDays) {
        initDaysLeftAlertInMainPane(availableDays);
        initTablesInMainPane();
    }

    private void initDaysLeftAlertInMainPane(long availableDays) {
        if (availableDays < 7) {
            String text = String.format(
                    "Если не оплатить подписку, то через %s дней программа перестанет работать", availableDays
            );
            if (availableDays < 5) {
                Text.setTextCentre(
                        mainPaneDaysLeftAlertTopic, text, Text.getAdaptiveFontSize(mainPaneDaysLeftAlertTopic, 45)
                );
                mainPaneDaysLeftAlert.setVisible(true);
            }
            AlertWindow.showInfo("Близится окончание подписки", text, true);
        } else {
            mainPaneDaysLeftAlert.setVisible(false);
            mainPanePositions.getRowConstraints().getFirst().setPercentHeight(0);
            mainPanePositions.getRowConstraints().getLast().setPercentHeight(85);
        }
    }

    private void initTablesInMainPane() {
        StringExpression topicFontSize = Text.getAdaptiveFontSize(mainPaneCookingOrdersTopic, 17);
        Text.setTextLeft(mainPaneCookingOrdersTopic, "Заказ готовится", topicFontSize);
        Text.setTextLeft(mainPaneReadyOrdersTopic, "Готов к выдаче", topicFontSize);
    }

}
