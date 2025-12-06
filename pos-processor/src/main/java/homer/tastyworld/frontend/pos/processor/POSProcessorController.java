package homer.tastyworld.frontend.pos.processor;

import homer.tastyworld.frontend.pos.processor.core.EditItemQuantityPane;
import homer.tastyworld.frontend.pos.processor.core.printer.OrderPrinterPageFactory;
import homer.tastyworld.frontend.pos.processor.core.OrderInfoPaneRenderer;
import homer.tastyworld.frontend.pos.processor.core.queue.OrdersScrollQueue;
import homer.tastyworld.frontend.starterpack.api.sra.entity.current.ClientPoint;
import homer.tastyworld.frontend.starterpack.api.sra.entity.misc.OrderStatus;
import homer.tastyworld.frontend.starterpack.api.sra.entity.order.Order;
import homer.tastyworld.frontend.starterpack.api.sra.entity.order.OrderItem;
import homer.tastyworld.frontend.starterpack.utils.managers.external.printer.PrinterManager;
import homer.tastyworld.frontend.starterpack.utils.misc.TypeChanger;
import homer.tastyworld.frontend.starterpack.utils.ui.AlertWindows;
import homer.tastyworld.frontend.starterpack.utils.ui.DialogWindows;
import homer.tastyworld.frontend.starterpack.utils.ui.helpers.PaneHelper;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import java.util.Optional;

public class POSProcessorController {

    @FXML
    private ScrollPane scrollItems, scrollOrders;
    @FXML
    private AnchorPane setOrderPreparingImgBtn, setOrderReadyImgBtn;
    @FXML
    private AnchorPane orderNameTopic, orderDraftedTimeTopic, orderTakeawayPackTopic;

    @FXML
    private AnchorPane editItemQuantityPaneParent;
    @FXML
    private AnchorPane editItemQuantityPaneCloseImgBtn, editItemQuantityPaneSubmitImgBtn;
    @FXML
    private AnchorPane editItemQuantityPaneNameTopic, editItemQuantityPaneTotalTopic;
    @FXML
    private GridPane editItemQuantityPaneNumbersKeyboardGrid;

    @FXML
    private void initialize() {
        checkSubscriptionDays();
        initImgBtnsInMainPane();
        initImgBtnsInEditItemQuantityPane();
        OrderInfoPaneRenderer.init(scrollItems, orderNameTopic, orderDraftedTimeTopic, orderTakeawayPackTopic);
        OrdersScrollQueue.init(scrollOrders);
        EditItemQuantityPane.init(
                editItemQuantityPaneParent,
                editItemQuantityPaneNameTopic,
                editItemQuantityPaneTotalTopic,
                editItemQuantityPaneNumbersKeyboardGrid
        );
    }

    private void checkSubscriptionDays() {
        long subscriptionAvailableDays = ClientPoint.getSubscriptionDays();
        if (subscriptionAvailableDays <= 7) {
            AlertWindows.showInfo(
                    "Близится окончание подписки",
                    "Если не оплатить подписку, то через %s программа перестанет работать".formatted(TypeChanger.toDaysFormat(subscriptionAvailableDays)),
                    true
            );
        }
    }

    private void initImgBtnsInMainPane() {
        PaneHelper.setImageBackgroundCentre(
                setOrderPreparingImgBtn,
                POSProcessorApplication.class.getResourceAsStream("images/buttons/SetOrderPreparing.png")
        );
        PaneHelper.setImageBackgroundCentre(
                setOrderReadyImgBtn,
                POSProcessorApplication.class.getResourceAsStream("images/buttons/SetOrderReady.png")
        );
    }

    private void initImgBtnsInEditItemQuantityPane() {
        PaneHelper.setImageBackgroundCentre(
                editItemQuantityPaneCloseImgBtn,
                POSProcessorApplication.class.getResourceAsStream("images/buttons/EditItemQuantityPane/Close.png")
        );
        PaneHelper.setImageBackgroundCentre(
                editItemQuantityPaneSubmitImgBtn,
                POSProcessorApplication.class.getResourceAsStream("images/buttons/EditItemQuantityPane/Submit.png")
        );
    }

    @FXML
    void setOrderPreparingImgBtnPressed() {
        Optional<Order> optionalOrder = OrderInfoPaneRenderer.getRenderedOrder();
        if (optionalOrder.isEmpty()) {
            AlertWindows.showInfo("Заказ не выбран", "Начать выполнение невозможно, для начала откройте заказ", true);
            return;
        }
        Order order = optionalOrder.get();
        PrinterManager.print(new OrderPrinterPageFactory(order));
        order.upgradeStatus(OrderStatus.PREPARING);
    }

    @FXML
    void setOrderReadyImgBtnPressed() {
        Optional<Order> optionalOrder = OrderInfoPaneRenderer.getRenderedOrder();
        if (optionalOrder.isEmpty()) {
            AlertWindows.showInfo("Заказ не выбран", "Завершать нечего, для начала откройте заказ", true);
            return;
        }
        Order order = optionalOrder.get();
        boolean isConfirmed = DialogWindows.askBool(
                "Да, готов",
                "Нет",
                "Подтверждение готовности заказа",
                "Вы уверены, что заказ %s готов к выдаче?".formatted(order.getName()),
                "Это действие изменит статус заказа, отменить его нельзя"
        );
        if (isConfirmed) {
            order.upgradeStatus(OrderStatus.READY);
        }
    }

    @FXML
    void editItemQuantityPaneCloseImgBtnPressed() {
        EditItemQuantityPane.close();
    }

    @FXML
    void editItemQuantityPaneSubmitImgBtnPressed() {
        Optional<Order> optionalOrder = OrderInfoPaneRenderer.getRenderedOrder();
        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();

            OrderItem item = EditItemQuantityPane.getItem();
            int newQuantity = EditItemQuantityPane.getQuantity();
            if (newQuantity != item.quantity()) {
                if (newQuantity < item.qtyMin() || newQuantity > item.qtyMax()) {
                    AlertWindows.showInfo(
                            "Недопустимое количество",
                            "Продукт должен быть в рамках [%s-%s] %s".formatted(item.qtyMin(), item.qtyMax(), item.qtyMeasure().shortName),
                            true
                    );
                    return;
                }

                item = order.setItemQuantity(item, newQuantity);
                OrderInfoPaneRenderer.rerender();
            }
        }
        EditItemQuantityPane.close();
    }

}
