package homer.tastyworld.frontend.pos.creator.panes.stable;

import homer.tastyworld.frontend.pos.creator.POSCreatorApplication;
import homer.tastyworld.frontend.pos.creator.core.orders.table.OrderStatusUpdatesListener;
import homer.tastyworld.frontend.pos.creator.core.orders.table.POSCreatorTableNodeFactory;
import homer.tastyworld.frontend.pos.creator.panes.dynamic.DynamicParentPane;
import homer.tastyworld.frontend.starterpack.api.requests.MyParams;
import homer.tastyworld.frontend.starterpack.base.utils.managers.table.TableManager;
import homer.tastyworld.frontend.starterpack.base.utils.managers.table.TableNodeFactory;
import homer.tastyworld.frontend.starterpack.base.utils.managers.table.cursors.DefaultTableCursor;
import homer.tastyworld.frontend.starterpack.base.utils.misc.TypeChanger;
import homer.tastyworld.frontend.starterpack.base.utils.ui.AlertWindow;
import homer.tastyworld.frontend.starterpack.base.utils.ui.helpers.AdaptiveTextHelper;
import homer.tastyworld.frontend.starterpack.base.utils.ui.helpers.PaneHelper;
import javafx.beans.binding.StringExpression;
import javafx.geometry.Pos;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class MainParentPane extends StableParentPane {

    private GridPane parentPlace;
    private AnchorPane daysLeftAlert, daysLeftAlertTopic;
    private AnchorPane mainPaneClientPointNameTopic;
    private AnchorPane mainPaneSettingsImgBtn, mainPaneNewOrderImgBtn;
    private AnchorPane mainPaneCookingOrdersTopic, mainPaneReadyOrdersTopic;
    private GridPane mainPaneCookingOrdersTable, mainPaneReadyOrdersTable;
    private DynamicParentPane lookOrderParentPane;

    private void initDaysLeftAlert() {
        daysLeftAlert.setVisible(false);
        long subscriptionAvailableDays = MyParams.getTokenSubscriptionAvailableDays();
        if (subscriptionAvailableDays <= 7) {
            String text = String.format(
                    "Если не оплатить подписку, то через %s программа перестанет работать",
                    TypeChanger.toDaysFormat(subscriptionAvailableDays)
            );
            if (subscriptionAvailableDays <= 5) {
                AdaptiveTextHelper.setTextCentre(daysLeftAlertTopic, text, 45, null);
                daysLeftAlert.setVisible(true);
            }
            AlertWindow.showInfo("Близится окончание подписки", text, true);
        } else {
            parentPlace.getRowConstraints().getFirst().setPercentHeight(0);
            parentPlace.getRowConstraints().getLast().setPercentHeight(85);
        }
    }

    private void initClientPointTopicInMainPane() {
        AdaptiveTextHelper.setTextCentre(
                mainPaneClientPointNameTopic, (String) MyParams.getClientPointInfo().get("NAME"), 17, Color.web("#808080")
        );
    }

    private void initImgBtnsInMainPane() {
        PaneHelper.setImageBackgroundCentre(
                mainPaneNewOrderImgBtn,
                POSCreatorApplication.class.getResourceAsStream("images/buttons/MainPane/mainPaneNewOrderImgBtn.png")
        );
        PaneHelper.setImageBackgroundCentre(
                mainPaneSettingsImgBtn,
                POSCreatorApplication.class.getResourceAsStream("images/buttons/MainPane/mainPaneSettingsImgBtn.png")
        );
    }

    private void initTablesInMainPane() {
        StringExpression topicFontSize = AdaptiveTextHelper.getFontSize(mainPaneCookingOrdersTopic, 17);
        Color topicsColor = Color.web("#555555");
        AdaptiveTextHelper.setTextCentre(mainPaneCookingOrdersTopic, "Заказ готовится", topicFontSize, topicsColor);
        AdaptiveTextHelper.setTextCentre(mainPaneReadyOrdersTopic, "Готов к выдаче", topicFontSize, topicsColor);
        mainPaneCookingOrdersTable.setAlignment(Pos.CENTER);
        mainPaneReadyOrdersTable.setAlignment(Pos.CENTER);
        TableNodeFactory tableNodeFactory = new POSCreatorTableNodeFactory(lookOrderParentPane);
        OrderStatusUpdatesListener.init(
                new TableManager(mainPaneCookingOrdersTable, new DefaultTableCursor(4, 3), tableNodeFactory),
                new TableManager(mainPaneReadyOrdersTable, new DefaultTableCursor(4, 2), tableNodeFactory)
        );
    }

    @Override
    public void initialize() {
        initClientPointTopicInMainPane();
        initDaysLeftAlert();
        initImgBtnsInMainPane();
        initTablesInMainPane();
    }

}
