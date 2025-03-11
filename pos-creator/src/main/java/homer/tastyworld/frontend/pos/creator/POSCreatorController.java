package homer.tastyworld.frontend.pos.creator;

import homer.tastyworld.frontend.pos.creator.core.orders.internal.OrderCreating;
import homer.tastyworld.frontend.pos.creator.core.vkb.VirtualKeyboardPrompts;
import homer.tastyworld.frontend.pos.creator.panes.ParentPane;
import homer.tastyworld.frontend.pos.creator.panes.dynamic.AddProductParentPane;
import homer.tastyworld.frontend.pos.creator.panes.dynamic.DynamicParentPane;
import homer.tastyworld.frontend.pos.creator.panes.dynamic.EndOrderCreatingParentPane;
import homer.tastyworld.frontend.pos.creator.panes.dynamic.ProductsParentPane;
import homer.tastyworld.frontend.pos.creator.panes.stable.MenuParentPane;
import homer.tastyworld.frontend.pos.creator.core.orders.internal.OrderLooking;
import homer.tastyworld.frontend.pos.creator.panes.dynamic.LookOrderParentPane;
import homer.tastyworld.frontend.pos.creator.panes.stable.StableParentPane;
import homer.tastyworld.frontend.pos.creator.panes.stable.MainParentPane;
import homer.tastyworld.frontend.starterpack.api.requests.MyParams;
import homer.tastyworld.frontend.starterpack.base.exceptions.SubscriptionDaysAreOverError;
import homer.tastyworld.frontend.starterpack.base.utils.ui.AlertWindow;
import homer.tastyworld.frontend.starterpack.base.utils.ui.DialogWindow;
import homer.tastyworld.frontend.starterpack.base.utils.ui.VirtualKeyboard;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

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
                .parent(mainPaneParent)
                .lookOrderParentPane(lookOrderPane)
                .mainPaneGridNodeContainer(mainPaneGridNodeContainer)
                .mainPaneDaysLeftAlert(mainPaneDaysLeftAlert)
                .mainPaneDaysLeftAlertTopic(mainPaneDaysLeftAlertTopic)
                .mainPaneSettingsImgBtn(mainPaneSettingsImgBtn)
                .mainPaneNewOrderImgBtn(mainPaneNewOrderImgBtn)
                .mainPaneCookingOrdersTopic(mainPaneCookingOrdersTopic)
                .mainPaneReadyOrdersTopic(mainPaneReadyOrdersTopic)
                .mainPaneCookingOrdersTable(mainPaneCookingOrdersTable)
                .mainPaneReadyOrdersTable(mainPaneReadyOrdersTable)
                .build();
        mainPane.initialize();
    }

    private void initMenuPane() {
        menuPane = MenuParentPane
                .builder()
                .parent(menuPaneParent)
                .productsParentPane(productsPane)
                .menuPaneDeleteOrderImgBtn(menuPaneDeleteOrderImgBtn)
                .menuPaneLookOrderImgBtn(menuPaneLookOrderImgBtn)
                .menuPaneTopic(menuPaneTopic)
                .menuPaneImgMenuContainer(menuPaneImgMenuContainer)
                .build();
        menuPane.initialize();
    }

    private void initProductsPane() {
        productsPane = ProductsParentPane
                .builder()
                .parent(productsPaneParent)
                .addProductParentPane(addProductPane)
                .productsPaneBackInMenuImgBtn(productsPaneBackInMenuImgBtn)
                .productsPaneMenuTopic(productsPaneMenuTopic)
                .productPaneImgProductsContainer(productPaneImgProductsContainer)
                .build();
        productsPane.initialize();
    }

    private void initAddProductPane() {
        addProductPane = AddProductParentPane
                .builder()
                .parent(addProductPaneParent)
                .addProductCloseImgBtn(addProductCloseImgBtn)
                .addProductSubmitImgBtn(addProductSubmitImgBtn)
                .addProductNameTopic(addProductNameTopic)
                .addProductPriceTopic(addProductPriceTopic)
                .addProductTotalPriceField(addProductTotalPriceField)
                .addProductMinusQTYImgBtn(addProductMinusQTYImgBtn)
                .addProductPlusQTYImgBtn(addProductPlusQTYImgBtn)
                .addProductQTYFiled(addProductQTYFiled)
                .addProductQTYTypeTopic(addProductQTYTypeTopic)
                .addProductNumbersKeyboard(addProductNumbersKeyboard)
                .addProductAdditivesTopic(addProductAdditivesTopic)
                .addProductAdditivesContainer(addProductAdditivesContainer)
                .build();
        addProductPane.initialize();
    }

    private void initEndOrderPane() {
        endOrderCreatingPane = EndOrderCreatingParentPane
                .builder()
                .parent(endOrderCreatingPaneParent)
                .endOrderCreatingOpenMenuImgBtn(endOrderCreatingOpenMenuImgBtn)
                .endOrderCreatingCommitImgBtn(endOrderCreatingCommitImgBtn)
                .endOrderCreatingNameTopic(endOrderCreatingNameTopic)
                .endOrderCreatingTotalPriceTopic(endOrderCreatingTotalPriceTopic)
                .endOrderCreatingItemsContainer(endOrderCreatingItemsContainer)
                .endOrderCreatingDeliveryField(endOrderCreatingDeliveryField)
                .endOrderCreatingIsPaidCheckBox(endOrderCreatingIsPaidCheckBox)
                .build();
        endOrderCreatingPane.initialize();
    }

    private void initLookOrderPane() {
        lookOrderPane = LookOrderParentPane
                .builder()
                .parent(lookOrderPaneParent)
                .lookOrderNameTopic(lookOrderNameTopic)
                .lookOrderTotalPriceTopic(lookOrderTotalPriceTopic)
                .lookOrderClosePaneImgBtn(lookOrderClosePaneImgBtn)
                .lookOrderSetDoneImgBtn(lookOrderSetDoneImgBtn)
                .lookOrderItemsContainer(lookOrderItemsContainer)
                .lookOrderDeliveryField(lookOrderDeliveryField)
                .lookOrderSetPaidBtn(lookOrderSetPaidBtn)
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
        long subscriptionAvailableDays = MyParams.getTokenSubscriptionAvailableDays();
        if (subscriptionAvailableDays < 0) {
            throw new SubscriptionDaysAreOverError();
        }
        ParentPane.setBase(base);
        initLookOrderPane();
        initMainPane();
        initAddProductPane();
        initProductsPane();
        initMenuPane();
        initEndOrderPane();
        initVirtualKeyboard();
        mainPane.openAndCloseOther();
    }

    @FXML
    void addProductCloseImgBtnPressed() {
        productsPane.fill(AddProductParentPane.Product.menuID);
        addProductPane.clean();
        productsPane.openAndCloseFrom(addProductPaneParent);
    }

    @FXML
    void addProductSubmitImgBtnPressed() {
        OrderCreating.start(true);
        OrderCreating.appendProduct(
                AddProductParentPane.Product.productID,
                AddProductParentPane.Product.qty,
                AddProductParentPane.Product.notDefaultAdditives
        );
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

    }

    @FXML
    void mainPaneNewOrderImgBtnPressed() {
        // OrderCreating.newOrder();
        menuPane.openAndCloseFrom(mainPaneParent);
    }

    @FXML
    void menuPaneDeleteOrderImgBtnPressed() {
        OrderCreating.delete();
        mainPane.openAndCloseFrom(menuPaneParent);
    }

    @FXML
    void menuPaneLookOrderImgBtnPressed() {
        if (OrderCreating.id == null || OrderCreating.isEmpty()) {
            AlertWindow.showInfo(
                    "Заказ пуст", "Добавьте хотя бы один продукт, прежде чем завершить создание заказа", true
            );
        } else {
            endOrderCreatingPane.fill(OrderCreating.id);
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
        if (OrderCreating.id == null || OrderCreating.isEmpty()) {
            AlertWindow.showInfo(
                    "Заказ пуст", "Добавьте хотя бы один продукт, прежде чем завершить создание заказа", true
            );
            return;
        }
        String address = endOrderCreatingDeliveryField.getText().trim();
        if (!address.equals("")) {
            OrderCreating.editDeliveryAddress(address);
            VirtualKeyboardPrompts.appendVar(endOrderCreatingDeliveryField);
        }
        OrderCreating.finish();
        if (endOrderCreatingIsPaidCheckBox.isSelected()) {
            OrderCreating.setPaid();
        }
        endOrderCreatingPane.clean();
        mainPane.openAndCloseFrom(endOrderCreatingPaneParent);
    }

    @FXML
    void closeLookOrderPane() {
        OrderLooking.editDeliveryAddressIfNotEqual(lookOrderDeliveryField.getText().trim());
        lookOrderPane.clean();
        OrderLooking.finish();
        lookOrderPaneParent.setVisible(false);
    }

    @FXML
    void lookOrderSetDoneImgBtnPressed() {
        boolean isPaid = OrderLooking.isPaid();
        boolean isAccess = DialogWindow.askBool(
                "Выдан", "Нет", "Завершение заказа",
                String.format("Отметить заказ как выданный%s?", isPaid ? " и оплаченный" : ""),
                "Заказ будет завешён, отменить это действие нльзя"
        );
        if (!isAccess) {
            return;
        }
        if (!isPaid) {
            OrderLooking.setPaid();
            lookOrderSetPaidBtn.setDisable(true);
        }
        OrderLooking.setDone();
        closeLookOrderPane();
    }

    @FXML
    void lookOrderSetPaidBtnPressed() {
        boolean isAccess = !OrderLooking.isPaid() && DialogWindow.askBool(
                "Оплачен", "Нет", "Оплата заказа",
                "Отметить заказ как оплаченный и получить чек?",
                "Позиции заказа будут закрыты для редактирования, отменить это действие нльзя"
        );
        if (isAccess) {
            OrderLooking.setPaid();
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
