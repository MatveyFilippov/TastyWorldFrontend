package homer.tastyworld.frontend.pos.creator;

import homer.tastyworld.frontend.pos.creator.core.orders.OrderCreator;
import homer.tastyworld.frontend.pos.creator.core.vkb.VirtualKeyboardPrompts;
import homer.tastyworld.frontend.pos.creator.panes.ParentPane;
import homer.tastyworld.frontend.pos.creator.panes.dynamic.AddProductParentPane;
import homer.tastyworld.frontend.pos.creator.panes.dynamic.DynamicParentPane;
import homer.tastyworld.frontend.pos.creator.panes.dynamic.EndOrderCreatingParentPane;
import homer.tastyworld.frontend.pos.creator.panes.dynamic.LookOrderParentPane;
import homer.tastyworld.frontend.pos.creator.panes.dynamic.ProductsParentPane;
import homer.tastyworld.frontend.pos.creator.panes.stable.MainParentPane;
import homer.tastyworld.frontend.pos.creator.panes.stable.MenuParentPane;
import homer.tastyworld.frontend.pos.creator.panes.stable.StableParentPane;
import homer.tastyworld.frontend.starterpack.base.exceptions.SubscriptionDaysAreOverError;
import homer.tastyworld.frontend.starterpack.base.utils.ui.AlertWindow;
import homer.tastyworld.frontend.starterpack.base.utils.ui.DialogWindow;
import homer.tastyworld.frontend.starterpack.base.utils.ui.VirtualKeyboard;
import homer.tastyworld.frontend.starterpack.entity.Product;
import homer.tastyworld.frontend.starterpack.entity.current.Token;
import homer.tastyworld.frontend.starterpack.order.Order;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import java.util.Objects;

public class POSCreatorController {

    @FXML
    private AnchorPane base;

    private StableParentPane mainPane;
    @FXML
    private AnchorPane mainPaneParent;
    @FXML
    private GridPane mainPaneGridNodeContainer;
    @FXML
    private AnchorPane mainPaneDaysLeftAlert, mainPaneDaysLeftAlertTopic;
    @FXML
    private AnchorPane mainPaneClientPointNameTopic;
    @FXML
    private AnchorPane mainPaneSettingsImgBtn, mainPaneNewOrderImgBtn;
    @FXML
    private AnchorPane mainPaneCookingOrdersTopic, mainPaneReadyOrdersTopic;
    @FXML
    private GridPane mainPaneCookingOrdersTable, mainPaneReadyOrdersTable;

    private StableParentPane menuPane;
    @FXML
    private AnchorPane menuPaneParent;
    @FXML
    private AnchorPane menuPaneDeleteOrderImgBtn, menuPaneLookOrderImgBtn;
    @FXML
    private AnchorPane menuPaneTopic;
    @FXML
    private GridPane menuPaneImgMenuContainer;

    private DynamicParentPane productsPane;
    @FXML
    private AnchorPane productsPaneParent;
    @FXML
    private AnchorPane productsPaneBackInMenuImgBtn;
    @FXML
    private AnchorPane productsPaneMenuTopic;
    @FXML
    private GridPane productPaneImgProductsContainer;

    private DynamicParentPane addProductPane;
    @FXML
    private AnchorPane addProductPaneParent;
    @FXML
    private AnchorPane addProductCloseImgBtn, addProductSubmitImgBtn;
    @FXML
    private AnchorPane addProductNameTopic;
    @FXML
    private AnchorPane addProductPriceTopic;
    @FXML
    private TextField addProductTotalPriceField;
    @FXML
    private AnchorPane addProductMinusQTYImgBtn, addProductPlusQTYImgBtn;
    @FXML
    private AnchorPane addProductQTYTypeTopic;
    @FXML
    private TextField addProductQTYFiled;
    @FXML
    private GridPane addProductNumbersKeyboard;
    @FXML
    private AnchorPane addProductAdditivesTopic;
    @FXML
    private GridPane addProductAdditivesContainer;

    private DynamicParentPane endOrderCreatingPane;
    @FXML
    private AnchorPane endOrderCreatingPaneParent;
    @FXML
    private AnchorPane endOrderCreatingOpenMenuImgBtn, endOrderCreatingCommitImgBtn;
    @FXML
    private AnchorPane endOrderCreatingNameTopic, endOrderCreatingTotalPriceTopic;
    @FXML
    private GridPane endOrderCreatingItemsContainer;
    @FXML
    private TextField endOrderCreatingDeliveryField;
    @FXML
    private CheckBox endOrderCreatingIsPaidCheckBox;

    private DynamicParentPane lookOrderPane;
    @FXML
    private AnchorPane lookOrderPaneParent;
    @FXML
    private AnchorPane lookOrderNameTopic, lookOrderTotalPriceTopic;
    @FXML
    private AnchorPane lookOrderClosePaneImgBtn, lookOrderSetDoneImgBtn;
    @FXML
    private GridPane lookOrderItemsContainer;
    @FXML
    private TextField lookOrderDeliveryField;
    @FXML
    private Button lookOrderSetPaidBtn;

    private VirtualKeyboard virtualKeyboard;
    @FXML
    private AnchorPane virtualKeyboardPaneParent;
    @FXML
    private HBox virtualKeyboardPrompts;
    @FXML
    private AnchorPane virtualKeyboardPlace;


    private void initMainPane() {
        mainPane = MainParentPane
                .builder()
                .current(mainPaneParent)
                .lookOrderParentPane(lookOrderPane)
                .parentPlace(mainPaneGridNodeContainer)
                .daysLeftAlert(mainPaneDaysLeftAlert)
                .daysLeftAlertTopic(mainPaneDaysLeftAlertTopic)
                .clientPointNameTopic(mainPaneClientPointNameTopic)
                .openSettingsImgBtn(mainPaneSettingsImgBtn)
                .startNewOrderImgBtn(mainPaneNewOrderImgBtn)
                .cookingOrdersTableTopic(mainPaneCookingOrdersTopic)
                .readyOrdersTableTopic(mainPaneReadyOrdersTopic)
                .cookingOrdersTable(mainPaneCookingOrdersTable)
                .readyOrdersTable(mainPaneReadyOrdersTable)
                .build();
        mainPane.initialize();
    }

    private void initMenuPane() {
        menuPane = MenuParentPane
                .builder()
                .current(menuPaneParent)
                .productsParentPane(productsPane)
                .deleteOrderImgBtn(menuPaneDeleteOrderImgBtn)
                .lookOrderImgBtn(menuPaneLookOrderImgBtn)
                .paneNameTopic(menuPaneTopic)
                .menuImgBtnContainer(menuPaneImgMenuContainer)
                .build();
        menuPane.initialize();
    }

    private void initProductsPane() {
        productsPane = ProductsParentPane
                .builder()
                .current(productsPaneParent)
                .addProductToOrderParentPane(addProductPane)
                .goBackInMenuImgBtn(productsPaneBackInMenuImgBtn)
                .paneNameTopic(productsPaneMenuTopic)
                .productsImgBtnContainer(productPaneImgProductsContainer)
                .build();
        productsPane.initialize();
    }

    private void initAddProductPane() {
        addProductPane = AddProductParentPane
                .builder()
                .current(addProductPaneParent)
                .cancelImgBtn(addProductCloseImgBtn)
                .submitImgBtn(addProductSubmitImgBtn)
                .productNameTopic(addProductNameTopic)
                .productPriceTopic(addProductPriceTopic)
                .totalPriceField(addProductTotalPriceField)
                .minusProductQTYImgBtn(addProductMinusQTYImgBtn)
                .plusProductQTYImgBtn(addProductPlusQTYImgBtn)
                .productQTYFiled(addProductQTYFiled)
                .productQTYTypeTopic(addProductQTYTypeTopic)
                .productQTYNumbersKeyboard(addProductNumbersKeyboard)
                .additivesTopic(addProductAdditivesTopic)
                .additivesContainer(addProductAdditivesContainer)
                .build();
        addProductPane.initialize();
    }

    private void initEndOrderPane() {
        endOrderCreatingPane = EndOrderCreatingParentPane
                .builder()
                .current(endOrderCreatingPaneParent)
                .goBackInMenuImgBtn(endOrderCreatingOpenMenuImgBtn)
                .submitEndingImgBtn(endOrderCreatingCommitImgBtn)
                .nameTopic(endOrderCreatingNameTopic)
                .totalPriceTopic(endOrderCreatingTotalPriceTopic)
                .itemsContainer(endOrderCreatingItemsContainer)
                .deliveryInfoField(endOrderCreatingDeliveryField)
                .isPaidCheckBox(endOrderCreatingIsPaidCheckBox)
                .build();
        endOrderCreatingPane.initialize();
    }

    private void initLookOrderPane() {
        lookOrderPane = LookOrderParentPane
                .builder()
                .current(lookOrderPaneParent)
                .nameTopic(lookOrderNameTopic)
                .totalPriceTopic(lookOrderTotalPriceTopic)
                .closePaneImgBtn(lookOrderClosePaneImgBtn)
                .setDoneImgBtn(lookOrderSetDoneImgBtn)
                .itemsContainer(lookOrderItemsContainer)
                .deliveryInfoField(lookOrderDeliveryField)
                .setPaidBtn(lookOrderSetPaidBtn)
                .build();
        lookOrderPane.initialize();
    }

    private void initVirtualKeyboard() {
        virtualKeyboard = new VirtualKeyboard(virtualKeyboardPlace);
        VirtualKeyboardPrompts.setPromptsContainer(virtualKeyboardPrompts);
        VirtualKeyboardPrompts.setInputField(endOrderCreatingDeliveryField);
        VirtualKeyboardPrompts.setInputField(lookOrderDeliveryField);
    }

    @FXML
    private void initialize() {
        if (Token.getTokenSubscriptionAvailableDays() < 0) {
            throw new SubscriptionDaysAreOverError();
        }
        ParentPane.setBase(base);
        initLookOrderPane();
        initAddProductPane();
        initProductsPane();
        initMenuPane();
        initEndOrderPane();
        initVirtualKeyboard();
        initMainPane();
        mainPane.openAndCloseOther();
    }

    @FXML
    void addProductCloseImgBtnPressed() {
        productsPane.fill(AddProductParentPane.ProductToAdd.product.getMenuID());
        addProductPane.clean();
        productsPane.openAndCloseFrom(addProductPaneParent);
    }

    @FXML
    void addProductSubmitImgBtnPressed() {
        int itemQTY = AddProductParentPane.ProductToAdd.pieceQTY;
        Product product = AddProductParentPane.ProductToAdd.product;

        if (itemQTY < product.getMinPieceQTY() || itemQTY > product.getMaxPieceQTY()) {
            AlertWindow.showInfo(
                    "Недопустимое количество",
                    "Продукт должен быть в рамках [%s-%s] %s".formatted(product.getMinPieceQTY(), product.getMaxPieceQTY(), product.getPieceType().shortName),
                    true
            );
            return;
        }

        Order order = OrderCreator.get();
        order.appendItem(product.getId(), itemQTY, AddProductParentPane.ProductToAdd.notDefaultAdditives);

        addProductPane.clean();
        menuPane.openAndCloseFrom(addProductPaneParent);
    }

    @FXML
    void productsPaneBackInMenuImgBtnPressed() {
        productsPane.clean();
        menuPane.openAndCloseFrom(productsPaneParent);
    }

    @FXML
    void mainPaneSettingsImgBtnPressed() {
        AlertWindow.showInfo(
                "К сожалению, нет доступа", "В настощий момент данный блок является недоступным к использованию", false
        );
    }

    @FXML
    void mainPaneNewOrderImgBtnPressed() {
        OrderCreator.start(true);
        menuPane.openAndCloseFrom(mainPaneParent);
    }

    @FXML
    void menuPaneDeleteOrderImgBtnPressed() {
        OrderCreator.cancel();
        mainPane.openAndCloseFrom(menuPaneParent);
    }

    @FXML
    void menuPaneLookOrderImgBtnPressed() {
        Order order = OrderCreator.get();
        if (order.getItems().length == 0) {
            AlertWindow.showInfo(
                    "Заказ пуст", "Добавьте хотя бы один продукт, прежде чем завершить создание заказа", true
            );
        } else {
            endOrderCreatingPane.fill(order.id);
            endOrderCreatingPane.openAndCloseFrom(menuPaneParent);
        }
    }

    @FXML
    void endOrderCreatingOpenMenuImgBtnPressed() {
        endOrderCreatingPane.clean();
        menuPane.openAndCloseFrom(endOrderCreatingPaneParent);
    }

    @FXML
    void endOrderCreatingCommitImgBtnPressed() {
        Order order = OrderCreator.get();
        if (order.getItems().length == 0) {
            AlertWindow.showInfo(
                    "Заказ пуст", "Добавьте хотя бы один продукт, прежде чем завершить создание заказа", true
            );
            return;
        }
        if (endOrderCreatingIsPaidCheckBox.isSelected()) {
            boolean isAccess = DialogWindow.askBool(
                    "Оплачен", "Нет", "Оплата заказа",
                    "Отметить заказ как оплаченный и получить чек?",
                    "Позиции заказа будут закрыты для редактирования, отменить это действие нльзя"
            );
            if (isAccess) {
                order.markAsPaid();
            } else {
                endOrderCreatingIsPaidCheckBox.setSelected(false);
                return;
            }
        }
        String deliveryInfo = endOrderCreatingDeliveryField.getText();
        if (deliveryInfo != null) {
            deliveryInfo = deliveryInfo.trim();
            if (deliveryInfo.equals("")) {
                deliveryInfo = null;
            } else {
                VirtualKeyboardPrompts.appendVar(endOrderCreatingDeliveryField);
            }
        }
        if (!Objects.equals(deliveryInfo, order.getDeliveryInfo())) {
            order.editDeliveryInfo(deliveryInfo);
        }
        OrderCreator.finish();
        endOrderCreatingPane.clean();
        mainPane.openAndCloseFrom(endOrderCreatingPaneParent);
    }

    @FXML
    void closeLookOrderPane() {
        Order order = Order.get(LookOrderParentPane.getCurrentOrderID());
        if (!order.isPaid()) {
            String deliveryInfo = lookOrderDeliveryField.getText();
            if (deliveryInfo != null) {
                deliveryInfo = deliveryInfo.trim();
                if (deliveryInfo.equals("")) {
                    deliveryInfo = null;
                } else {
                    VirtualKeyboardPrompts.appendVar(endOrderCreatingDeliveryField);
                }
            }
            if (!Objects.equals(deliveryInfo, order.getDeliveryInfo())) {
                order.editDeliveryInfo(deliveryInfo);
            }
        }
        lookOrderPane.clean();
        lookOrderPaneParent.setVisible(false);
    }

    @FXML
    void lookOrderSetDoneImgBtnPressed() {
        Order order = Order.get(LookOrderParentPane.getCurrentOrderID());
        boolean isPaid = order.isPaid();
        boolean isAccess = DialogWindow.askBool(
                "Выдан", "Нет", "Завершение заказа",
                String.format("Отметить заказ как выданный%s?", !isPaid ? " и оплаченный" : ""),
                "Заказ будет завешён, отменить это действие нльзя"
        );
        if (!isAccess) {
            return;
        }
        if (!isPaid) {
            order.markAsPaid();
            lookOrderSetPaidBtn.setDisable(true);
        }
        closeLookOrderPane();
    }

    @FXML
    void lookOrderSetPaidBtnPressed() {
        Order order = Order.get(LookOrderParentPane.getCurrentOrderID());
        boolean isAccess = !order.isPaid() && DialogWindow.askBool(
                "Оплачен", "Нет", "Оплата заказа",
                "Отметить заказ как оплаченный и получить чек?",
                "Позиции заказа будут закрыты для редактирования, отменить это действие нльзя"
        );
        if (isAccess) {
            order.markAsPaid();
            lookOrderSetPaidBtn.setDisable(true);
        }
    }

    @FXML
    void hideVirtualKeyboardParentPane() {
        VirtualKeyboardPrompts.clean();
        virtualKeyboardPaneParent.setVisible(false);
    }

    @FXML
    void showVirtualKeyboardParentPane() {
        virtualKeyboardPaneParent.setVisible(true);
    }

}
