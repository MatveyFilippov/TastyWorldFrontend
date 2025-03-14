package homer.tastyworld.frontend.pos.processor;

import homer.tastyworld.frontend.pos.processor.core.OrderActions;
import homer.tastyworld.frontend.pos.processor.core.helpers.OrderInfoPaneRenderer;
import homer.tastyworld.frontend.pos.processor.core.OrderUpdatesListener;
import homer.tastyworld.frontend.pos.processor.core.helpers.EditItemQtyPane;
import homer.tastyworld.frontend.starterpack.api.requests.MyParams;
import homer.tastyworld.frontend.starterpack.base.exceptions.SubscriptionDaysAreOverError;
import homer.tastyworld.frontend.starterpack.base.utils.misc.TypeChanger;
import homer.tastyworld.frontend.starterpack.base.utils.ui.AlertWindow;
import homer.tastyworld.frontend.starterpack.base.utils.ui.helpers.PaneHelper;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

public class POSProcessorController {

    @FXML
    private ScrollPane scrollItems, scrollOrders;
    @FXML
    private AnchorPane printOrderImgBtn, doneOrderImgBtn;
    @FXML
    private AnchorPane orderCreatedTimeTopic, orderNameTopic;

    @FXML
    private AnchorPane editItemQtyPaneParent;
    @FXML
    private AnchorPane editItemQtyPaneCloseImgBtn, editItemQtyPaneCommitImgBtn;
    @FXML
    private AnchorPane editItemQtyTopic;
    @FXML
    private GridPane editItemQtyNumbersKeyboard;

    @FXML
    private void initialize() {
        checkDaysLeft();
        initImgBtnsInMainPane();
        OrderInfoPaneRenderer.init(scrollItems, orderCreatedTimeTopic, orderNameTopic);
        OrderUpdatesListener.init(scrollOrders);
        EditItemQtyPane.init(editItemQtyPaneParent, editItemQtyTopic, editItemQtyNumbersKeyboard);
    }

    private void checkDaysLeft() {
        long subscriptionAvailableDays = MyParams.getTokenSubscriptionAvailableDays();
        if (subscriptionAvailableDays < 0) {
            throw new SubscriptionDaysAreOverError();
        }
        if (subscriptionAvailableDays <= 7) {
            String text = String.format(
                    "Если не оплатить подписку, то через %s программа перестанет работать",
                    TypeChanger.toDaysFormat(subscriptionAvailableDays)
            );
            AlertWindow.showInfo("Близится окончание подписки", text, true);
        }
    }

    private void initImgBtnsInMainPane() {
        PaneHelper.setImageBackgroundCentre(
                printOrderImgBtn,
                POSProcessorApplication.class.getResourceAsStream("images/buttons/printOrderImgBtn.png")
        );
        PaneHelper.setImageBackgroundCentre(
                doneOrderImgBtn,
                POSProcessorApplication.class.getResourceAsStream("images/buttons/doneOrderImgBtn.png")
        );
    }

    @FXML
    void printOrderImgBtnPressed() {
        if (OrderInfoPaneRenderer.orderID == null) {
            AlertWindow.showInfo("Заказ не выбран", "Печатать нечего, для начала откройте заказ", true);
            return;
        }
        OrderActions.print(OrderInfoPaneRenderer.orderID);
    }

    @FXML
    void doneOrderImgBtnPressed() {
        if (OrderInfoPaneRenderer.orderID == null) {
            AlertWindow.showInfo("Заказ не выбран", "Завершать нечего, для начала откройте заказ", true);
            return;
        }
        OrderActions.setProcessed(OrderInfoPaneRenderer.orderID);
    }

    @FXML
    void closeEditItemQtyPane() {
        EditItemQtyPane.close();
    }

    @FXML
    void editItemQtyPaneCommitImgBtnPressed() {
        if (EditItemQtyPane.itemID != null && EditItemQtyPane.qty != null) {
            OrderActions.editItemQTY(EditItemQtyPane.itemID, EditItemQtyPane.qty);
            OrderInfoPaneRenderer.rerender();
        }
        closeEditItemQtyPane();
    }

}
