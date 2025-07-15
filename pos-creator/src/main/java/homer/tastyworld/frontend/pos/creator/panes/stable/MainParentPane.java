package homer.tastyworld.frontend.pos.creator.panes.stable;

import homer.tastyworld.frontend.pos.creator.POSCreatorApplication;
import homer.tastyworld.frontend.pos.creator.core.orders.table.listeners.OrderStatusUpdatesListener;
import homer.tastyworld.frontend.pos.creator.core.orders.table.POSCreatorTableNodeFactory;
import homer.tastyworld.frontend.pos.creator.panes.dynamic.DynamicParentPane;
import homer.tastyworld.frontend.starterpack.base.utils.managers.table.TableManager;
import homer.tastyworld.frontend.starterpack.base.utils.managers.table.TableNodeFactory;
import homer.tastyworld.frontend.starterpack.base.utils.managers.table.cursors.DefaultTableCursor;
import homer.tastyworld.frontend.starterpack.base.utils.misc.TypeChanger;
import homer.tastyworld.frontend.starterpack.base.utils.ui.AlertWindow;
import homer.tastyworld.frontend.starterpack.base.utils.ui.helpers.AdaptiveTextHelper;
import homer.tastyworld.frontend.starterpack.base.utils.ui.helpers.PaneHelper;
import homer.tastyworld.frontend.starterpack.entity.current.ClientPoint;
import homer.tastyworld.frontend.starterpack.entity.current.Token;
import javafx.beans.binding.StringExpression;
import javafx.geometry.Pos;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import lombok.experimental.SuperBuilder;

@SuperBuilder
public class MainParentPane extends StableParentPane {

    private final GridPane parentPlace;
    private final AnchorPane daysLeftAlert, daysLeftAlertTopic;
    private final AnchorPane clientPointNameTopic;
    private final AnchorPane openSettingsImgBtn, startNewOrderImgBtn;
    private final AnchorPane cookingOrdersTableTopic, readyOrdersTableTopic;
    private final GridPane cookingOrdersTable, readyOrdersTable;
    private final DynamicParentPane lookOrderParentPane;

    private void initClientPointTopicInMainPane() {
        AdaptiveTextHelper.setTextCentre(
                clientPointNameTopic, ClientPoint.name, 17, Color.web("#808080")
        );
    }

    private void initDaysLeftAlert() {
        long subscriptionAvailableDays = Token.getTokenSubscriptionAvailableDays();
        boolean isAlertTopicRequired = subscriptionAvailableDays <= 5;

        daysLeftAlert.setVisible(false);
        if (subscriptionAvailableDays <= 7) {
            String text = String.format(
                    "Если не оплатить подписку, то через %s программа перестанет работать",
                    TypeChanger.toDaysFormat(subscriptionAvailableDays)
            );
            if (isAlertTopicRequired) {
                AdaptiveTextHelper.setTextCentre(daysLeftAlertTopic, text, 45, null);
                daysLeftAlert.setVisible(true);
            }
            AlertWindow.showInfo("Близится окончание подписки", text, true);
        }
        if (!isAlertTopicRequired) {
            parentPlace.getRowConstraints().getFirst().setPercentHeight(0);
            parentPlace.getRowConstraints().getLast().setPercentHeight(85);
        }
    }

    private void initImgBtnsInMainPane() {
        PaneHelper.setImageBackgroundCentre(
                startNewOrderImgBtn,
                POSCreatorApplication.class.getResourceAsStream("images/buttons/MainPane/NewOrder.png")
        );
        PaneHelper.setImageBackgroundCentre(
                openSettingsImgBtn,
                POSCreatorApplication.class.getResourceAsStream("images/buttons/MainPane/Settings.png")
        );
    }

    private void initTablesInMainPane() {
        StringExpression topicFontSize = AdaptiveTextHelper.getFontSize(cookingOrdersTableTopic, 16);
        Color topicsColor = Color.web("#555555");

        AdaptiveTextHelper.setTextCentre(cookingOrdersTableTopic, "Готовится", topicFontSize, topicsColor);
        AdaptiveTextHelper.setTextCentre(readyOrdersTableTopic, "На выдаче", topicFontSize, topicsColor);
        cookingOrdersTable.setAlignment(Pos.CENTER);
        readyOrdersTable.setAlignment(Pos.CENTER);

        TableNodeFactory tableNodeFactory = new POSCreatorTableNodeFactory(lookOrderParentPane);
        OrderStatusUpdatesListener.init(
                new TableManager(cookingOrdersTable, new DefaultTableCursor(4, 3), tableNodeFactory),
                new TableManager(readyOrdersTable, new DefaultTableCursor(4, 2), tableNodeFactory)
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
