package homer.tastyworld.frontend.pos.creator.panes.implementations;

import homer.tastyworld.frontend.pos.creator.POSCreatorApplication;
import homer.tastyworld.frontend.pos.creator.core.orders.table.listeners.OrderStatusUpdatesListener;
import homer.tastyworld.frontend.pos.creator.core.orders.table.POSCreatorTableNodeFactory;
import homer.tastyworld.frontend.pos.creator.panes.ParentPane;
import homer.tastyworld.frontend.starterpack.api.sra.entity.current.ClientPoint;
import homer.tastyworld.frontend.starterpack.api.sra.entity.order.Order;
import homer.tastyworld.frontend.starterpack.utils.managers.table.TableManager;
import homer.tastyworld.frontend.starterpack.utils.managers.table.TableNodeFactory;
import homer.tastyworld.frontend.starterpack.utils.managers.table.cursors.DefaultTableCursor;
import homer.tastyworld.frontend.starterpack.utils.misc.TypeChanger;
import homer.tastyworld.frontend.starterpack.utils.ui.AlertWindows;
import homer.tastyworld.frontend.starterpack.utils.ui.helpers.AdaptiveTextHelper;
import homer.tastyworld.frontend.starterpack.utils.ui.helpers.PaneHelper;
import javafx.beans.binding.StringExpression;
import javafx.geometry.Pos;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import lombok.experimental.SuperBuilder;

@SuperBuilder
public class MainParentPane extends ParentPane<Void> {

    private final GridPane currentGrid;
    private final AnchorPane daysLeftAlert, daysLeftAlertTopic;
    private final AnchorPane clientPointNameTopic;
    private final AnchorPane openSettingsImgBtn, draftNewOrderImgBtn;
    private final AnchorPane preparingOrdersTableTopic, readyOrdersTableTopic;
    private final GridPane preparingOrdersTable, readyOrdersTable;
    private final ParentPane<Order> lookCreatedOrderPane;

    private void initClientPointNameTopic() {
        AdaptiveTextHelper.setTextCentre(
                clientPointNameTopic, ClientPoint.name, 0.058, Color.web("#808080")
        );
    }

    private void initDaysLeftAlert() {
        int subscriptionAvailableDays = ClientPoint.getSubscriptionDays();
        boolean isAlertTopicRequired = subscriptionAvailableDays <= 5;

        daysLeftAlert.setVisible(false);
        if (subscriptionAvailableDays <= 7) {
            String text = "Если не оплатить подписку, то через %s программа перестанет работать".formatted(
                    TypeChanger.toDaysFormat(subscriptionAvailableDays)
            );
            if (isAlertTopicRequired) {
                AdaptiveTextHelper.setTextCentre(daysLeftAlertTopic, text, 0.022, null);
                daysLeftAlert.setVisible(true);
            }
            AlertWindows.showInfo("Близится окончание подписки", text, true);
        }
        if (!isAlertTopicRequired) {
            currentGrid.getRowConstraints().getFirst().setPercentHeight(0);
            currentGrid.getRowConstraints().getLast().setPercentHeight(85);
        }
    }

    private void initImgBtns() {
        PaneHelper.setImageBackgroundCentre(
                draftNewOrderImgBtn,
                POSCreatorApplication.class.getResourceAsStream("images/buttons/MainPane/NewOrder.png")
        );
        PaneHelper.setImageBackgroundCentre(
                openSettingsImgBtn,
                POSCreatorApplication.class.getResourceAsStream("images/buttons/MainPane/Settings.png")
        );
    }

    private void initTables() {
        StringExpression topicFontSize = AdaptiveTextHelper.getFontSize(preparingOrdersTableTopic, 0.06);
        Color topicsColor = Color.web("#555555");

        AdaptiveTextHelper.setTextCentre(preparingOrdersTableTopic, "Готовится", topicFontSize, topicsColor);
        AdaptiveTextHelper.setTextCentre(readyOrdersTableTopic, "На выдаче", topicFontSize, topicsColor);
        preparingOrdersTable.setAlignment(Pos.CENTER);
        readyOrdersTable.setAlignment(Pos.CENTER);

        TableNodeFactory tableNodeFactory = new POSCreatorTableNodeFactory(lookCreatedOrderPane);
        OrderStatusUpdatesListener.init(
                new TableManager(preparingOrdersTable, new DefaultTableCursor(4, 3), tableNodeFactory),
                new TableManager(readyOrdersTable, new DefaultTableCursor(4, 2), tableNodeFactory)
        );
    }

    @Override
    public void initialize() {
        initClientPointNameTopic();
        initDaysLeftAlert();
        initImgBtns();
        initTables();
    }

    @Override
    protected void beforeOpen(Void data) {}

    @Override
    protected void beforeClose() {}

}
