package homer.tastyworld.frontend.poscreator.panes.stable;

import homer.tastyworld.frontend.poscreator.POSCreatorApplication;
import homer.tastyworld.frontend.poscreator.core.orders.table.OrderStatusUpdatesListener;
import homer.tastyworld.frontend.poscreator.core.orders.table.POSCreatorTableNodeFactory;
import homer.tastyworld.frontend.starterpack.api.requests.MyParams;
import homer.tastyworld.frontend.starterpack.base.utils.managers.tablemanager.TableManager;
import homer.tastyworld.frontend.starterpack.base.utils.managers.tablemanager.TableNodeFactory;
import homer.tastyworld.frontend.starterpack.base.utils.misc.TypeChanger;
import homer.tastyworld.frontend.starterpack.base.utils.ui.AlertWindow;
import homer.tastyworld.frontend.starterpack.base.utils.ui.helpers.Helper;
import homer.tastyworld.frontend.starterpack.base.utils.ui.helpers.Text;
import javafx.beans.binding.StringExpression;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class MainParentPane extends StableParentPane {

    private GridPane mainPaneGridNodeContainer;
    private AnchorPane mainPaneDaysLeftAlert, mainPaneDaysLeftAlertTopic;
    private AnchorPane mainPaneSettingsImgBtn, mainPaneNewOrderImgBtn;
    private AnchorPane mainPaneCookingOrdersTopic, mainPaneReadyOrdersTopic;
    private GridPane mainPaneCookingOrdersTable, mainPaneReadyOrdersTable;

    private void initDaysLeftAlertInMainPane() {
        long subscriptionAvailableDays = MyParams.getTokenSubscriptionAvailableDays();
        if (subscriptionAvailableDays <= 7) {
            String text = String.format(
                    "Если не оплатить подписку, то через %s программа перестанет работать",
                    TypeChanger.toDaysFormat(subscriptionAvailableDays)
            );
            if (subscriptionAvailableDays <= 5) {
                Text.setTextCentre(
                        mainPaneDaysLeftAlertTopic, text,
                        Text.getAdaptiveFontSize(mainPaneDaysLeftAlertTopic, 45), null
                );
                mainPaneDaysLeftAlert.setVisible(true);
            }
            AlertWindow.showInfo("Близится окончание подписки", text, true);
        } else {
            mainPaneDaysLeftAlert.setVisible(false);
            mainPaneGridNodeContainer.getRowConstraints().getFirst().setPercentHeight(0);
            mainPaneGridNodeContainer.getRowConstraints().getLast().setPercentHeight(85);
        }
    }

    private void initImgBtnsInMainPane() {
        Helper.setAnchorPaneImageBackgroundCentre(
                mainPaneNewOrderImgBtn,
                POSCreatorApplication.class.getResourceAsStream("images/buttons/MainPane/mainPaneNewOrderImgBtn.png")
        );
        Helper.setAnchorPaneImageBackgroundCentre(
                mainPaneSettingsImgBtn,
                POSCreatorApplication.class.getResourceAsStream("images/buttons/MainPane/mainPaneSettingsImgBtn.png")
        );
    }

    private void initTablesInMainPane() {
        StringExpression topicFontSize = Text.getAdaptiveFontSize(mainPaneCookingOrdersTopic, 17);
        Text.setTextCentre(mainPaneCookingOrdersTopic, "Заказ готовится", topicFontSize, null);
        Text.setTextCentre(mainPaneReadyOrdersTopic, "Готов к выдаче", topicFontSize, null);
        TableNodeFactory tableNodeFactory = new POSCreatorTableNodeFactory();
        OrderStatusUpdatesListener.init(
                new TableManager(mainPaneCookingOrdersTable, tableNodeFactory),
                new TableManager(mainPaneReadyOrdersTable, tableNodeFactory)
        );
    }

    @Override
    public void initialize() {
        initDaysLeftAlertInMainPane();
        initImgBtnsInMainPane();
        initTablesInMainPane();
    }

}
