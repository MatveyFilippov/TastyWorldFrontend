package homer.tastyworld.frontend.poscreator;

import homer.tastyworld.frontend.poscreator.core.menu.ImageMenu;
import homer.tastyworld.frontend.poscreator.core.orders.table.OrderStatusUpdatesListener;
import homer.tastyworld.frontend.poscreator.core.orders.table.POSCreatorTableNodeFactory;
import homer.tastyworld.frontend.starterpack.api.requests.MyParams;
import homer.tastyworld.frontend.starterpack.base.exceptions.SubscriptionDaysAreOverError;
import homer.tastyworld.frontend.starterpack.base.utils.managers.tablemanager.TableManager;
import homer.tastyworld.frontend.starterpack.base.utils.managers.tablemanager.TableNodeFactory;
import homer.tastyworld.frontend.starterpack.base.utils.misc.TypeChanger;
import homer.tastyworld.frontend.starterpack.base.utils.ui.AlertWindow;
import homer.tastyworld.frontend.starterpack.base.utils.ui.helpers.Helper;
import homer.tastyworld.frontend.starterpack.base.utils.ui.helpers.Text;
import javafx.beans.binding.StringExpression;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

public class POSCreatorController {

    @FXML
    private AnchorPane base;

    @FXML
    private AnchorPane mainPaneParent;
    @FXML
    private GridPane mainPaneGridNodeContainer;
    @FXML
    private AnchorPane mainPaneDaysLeftAlert, mainPaneDaysLeftAlertTopic;
    @FXML
    private AnchorPane mainPaneSettingsImgBtn, mainPaneNewOrderImgBtn;
    @FXML
    private AnchorPane mainPaneCookingOrdersTopic, mainPaneReadyOrdersTopic;
    @FXML
    private GridPane mainPaneCookingOrdersTable, mainPaneReadyOrdersTable;

    @FXML
    private AnchorPane menuPaneParent;
    @FXML
    private AnchorPane menuPaneDeleteOrderImgBtn, menuPaneLookOrderImgBtn;
    @FXML
    private AnchorPane menuPaneTopic;
    @FXML
    private GridPane menuPaneImgMenuContainer;

    @FXML
    private void initialize() {
        long subscriptionAvailableDays = MyParams.getTokenSubscriptionAvailableDays();
        if (subscriptionAvailableDays < 0) {
            throw new SubscriptionDaysAreOverError();
        }
        initMainPane(subscriptionAvailableDays);
        initMenuPane();
    }

    private void initMainPane(long subscriptionAvailableDays) {
        initDaysLeftAlertInMainPane(subscriptionAvailableDays);
        initImgBtnsInMainPane();
        initTablesInMainPane();
    }

    private void initDaysLeftAlertInMainPane(long subscriptionAvailableDays) {
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
        Helper.setAnchorPaneImageBackground(
                mainPaneNewOrderImgBtn,
                getClass().getResourceAsStream("images/buttons/MainPane/mainPaneNewOrderImgBtn.png")
        );
        Helper.setAnchorPaneImageBackground(
                mainPaneSettingsImgBtn,
                getClass().getResourceAsStream("images/buttons/MainPane/mainPaneSettingsImgBtn.png")
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

    @FXML
    void mainPaneSettingsImgBtnPressed(MouseEvent event) {

    }

    @FXML
    void mainPaneNewOrderImgBtnPressed(MouseEvent event) {
        Helper.openParentPane(mainPaneParent, menuPaneParent);
    }

    private void initMenuPane() {
        ImageMenu.fill(menuPaneImgMenuContainer, 1, 0);
        initTopicInMenuPane();
        initImgBtnsInMenuPane();
    }

    private void initTopicInMenuPane() {
        Text.setTextCentre(menuPaneTopic, "Меню", Text.getAdaptiveFontSize(menuPaneTopic, 25), null);
    }

    private void initImgBtnsInMenuPane() {
        Helper.setAnchorPaneImageBackground(
                menuPaneDeleteOrderImgBtn,
                getClass().getResourceAsStream("images/buttons/MenuPane/menuPaneDeleteOrderImgBtn.png")
        );
        Helper.setAnchorPaneImageBackground(
                menuPaneLookOrderImgBtn,
                getClass().getResourceAsStream("images/buttons/MenuPane/menuPaneLookOrderImgBtn.png")
        );
    }

    @FXML
    void menuPaneDeleteOrderImgBtnPressed(MouseEvent event) {
        Helper.openParentPane(menuPaneParent, mainPaneParent);
    }

    @FXML
    void menuPaneLookOrderImgBtnPressed(MouseEvent event) {

    }

}
