package homer.tastyworld.frontend.pos.processor;

import homer.tastyworld.frontend.pos.processor.core.OrderActions;
import homer.tastyworld.frontend.pos.processor.core.helpers.OrderInfoPaneRenderer;
import homer.tastyworld.frontend.pos.processor.core.OrderUpdatesListener;
import homer.tastyworld.frontend.pos.processor.core.helpers.EditItemQtyPane;
import homer.tastyworld.frontend.starterpack.api.requests.MyParams;
import homer.tastyworld.frontend.starterpack.base.exceptions.SubscriptionDaysAreOverError;
import homer.tastyworld.frontend.starterpack.base.utils.managers.printer.PrinterManager;
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
    private AnchorPane startOrderImgBtn, doneOrderImgBtn;
    @FXML
    private AnchorPane orderCreatedTimeTopic, orderDeliveryTopic, orderNameTopic;

    @FXML
    private AnchorPane editItemQtyPaneParent;
    @FXML
    private AnchorPane editItemQtyPaneCloseImgBtn, editItemQtyPaneCommitImgBtn;
    @FXML
    private AnchorPane editItemQtyPaneNameTopic, editItemQtyPaneTotalTopic;
    @FXML
    private GridPane editItemQtyPaneNumbersKeyboard;

    @FXML
    private void initialize() {
        checkDaysLeft();
        initImgBtnsInMainPane();
        initImgBtnsInEditItemQtyPane();
        OrderInfoPaneRenderer.init(scrollItems, orderCreatedTimeTopic, orderDeliveryTopic, orderNameTopic);
        OrderUpdatesListener.init(scrollOrders);
        EditItemQtyPane.init(editItemQtyPaneParent, editItemQtyPaneNameTopic, editItemQtyPaneTotalTopic, editItemQtyPaneNumbersKeyboard);
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
                startOrderImgBtn,
                POSProcessorApplication.class.getResourceAsStream(
                        PrinterManager.isPrinterAvailable()
                        ? "images/buttons/startAndPrintOrderImgBtn.png"
                        : "images/buttons/startOrderImgBtn.png"
                )
        );
        PaneHelper.setImageBackgroundCentre(
                doneOrderImgBtn,
                POSProcessorApplication.class.getResourceAsStream("images/buttons/doneOrderImgBtn.png")
        );
    }

    private void initImgBtnsInEditItemQtyPane() {
        PaneHelper.setImageBackgroundCentre(
                editItemQtyPaneCloseImgBtn,
                POSProcessorApplication.class.getResourceAsStream("images/buttons/EditItemQtyPane/editItemQtyPaneCloseImgBtn.png")
        );
        PaneHelper.setImageBackgroundCentre(
                editItemQtyPaneCommitImgBtn,
                POSProcessorApplication.class.getResourceAsStream("images/buttons/EditItemQtyPane/editItemQtyPaneCommitImgBtn.png")
        );
    }

    @FXML
    void startOrderImgBtnPressed() {
        if (OrderInfoPaneRenderer.orderID == null) {
            AlertWindow.showInfo(
                    "Заказ не выбран",
                    PrinterManager.isPrinterAvailable()
                    ? "Печатать нечего, для начала откройте заказ"
                    : "Начать выполнение невозможно, для начала откройте заказ",
                    true
            );
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
