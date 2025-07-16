package homer.tastyworld.frontend.pos.processor;

import homer.tastyworld.frontend.pos.processor.core.printer.OrderPrinterPageFactory;
import homer.tastyworld.frontend.pos.processor.core.OrderInfoPaneRenderer;
import homer.tastyworld.frontend.pos.processor.core.EditItemQtyPane;
import homer.tastyworld.frontend.pos.processor.core.queue.OrdersScrollQueue;
import homer.tastyworld.frontend.starterpack.base.exceptions.SubscriptionDaysAreOverError;
import homer.tastyworld.frontend.starterpack.base.utils.managers.printer.PrinterManager;
import homer.tastyworld.frontend.starterpack.base.utils.misc.TypeChanger;
import homer.tastyworld.frontend.starterpack.base.utils.ui.AlertWindow;
import homer.tastyworld.frontend.starterpack.base.utils.ui.helpers.PaneHelper;
import homer.tastyworld.frontend.starterpack.entity.Product;
import homer.tastyworld.frontend.starterpack.entity.current.Token;
import homer.tastyworld.frontend.starterpack.entity.misc.OrderStatus;
import homer.tastyworld.frontend.starterpack.order.Order;
import homer.tastyworld.frontend.starterpack.order.core.items.OrderItem;
import homer.tastyworld.frontend.starterpack.order.core.items.OrderItemFetcher;
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
        OrdersScrollQueue.init(scrollOrders);
        EditItemQtyPane.init(editItemQtyPaneParent, editItemQtyPaneNameTopic, editItemQtyPaneTotalTopic, editItemQtyPaneNumbersKeyboard);
    }

    private void checkDaysLeft() {
        long subscriptionAvailableDays = Token.getTokenSubscriptionAvailableDays();
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
        if (OrderInfoPaneRenderer.order == null) {
            AlertWindow.showInfo(
                    "Заказ не выбран",
                    PrinterManager.isPrinterAvailable()
                    ? "Печатать нечего, для начала откройте заказ"
                    : "Начать выполнение невозможно, для начала откройте заказ",
                    true
            );
            return;
        }
        PrinterManager.print(new OrderPrinterPageFactory(OrderInfoPaneRenderer.order));
        OrdersScrollQueue.setChecked(OrderInfoPaneRenderer.order.id);
    }

    @FXML
    void doneOrderImgBtnPressed() {
        if (OrderInfoPaneRenderer.order == null) {
            AlertWindow.showInfo("Заказ не выбран", "Завершать нечего, для начала откройте заказ", true);
            return;
        }
        OrderInfoPaneRenderer.order.setStatus(OrderStatus.PROCESSED);
    }

    @FXML
    void closeEditItemQtyPane() {
        EditItemQtyPane.close();
    }

    @FXML
    void editItemQtyPaneCommitImgBtnPressed() {
        Order order = OrderInfoPaneRenderer.order;
        if (order != null && EditItemQtyPane.getItemID() != null && EditItemQtyPane.isEdit()) {
            OrderItem item = OrderItemFetcher.getItem(EditItemQtyPane.getItemID());
            Product product = Product.get(item.productID());
            int newQTY = EditItemQtyPane.getQTY();
            if (newQTY < product.getMinPieceQTY() || newQTY > product.getMaxPieceQTY()) {
                AlertWindow.showInfo(
                        "Недопустимое количество",
                        "Продукт должен быть в рамках [%s-%s] %s".formatted(product.getMinPieceQTY(), product.getMaxPieceQTY(), product.getPieceType().shortName),
                        true
                );
                return;
            }
            order.editItem(item.id(), newQTY);
            OrderInfoPaneRenderer.rerender();
        }
        closeEditItemQtyPane();
    }

}
