package homer.tastyworld.frontend.poscreator;

import homer.tastyworld.frontend.poscreator.core.orders.internal.OrderCreating;
import homer.tastyworld.frontend.poscreator.core.vkb.VirtualKeyboardPrompts;
import homer.tastyworld.frontend.poscreator.panes.ParentPane;
import homer.tastyworld.frontend.poscreator.panes.dynamic.AddProductParentPane;
import homer.tastyworld.frontend.poscreator.panes.dynamic.DynamicParentPane;
import homer.tastyworld.frontend.poscreator.panes.dynamic.EndOrderCreatingParentPane;
import homer.tastyworld.frontend.poscreator.panes.dynamic.ProductsParentPane;
import homer.tastyworld.frontend.poscreator.panes.stable.StableParentPane;
import homer.tastyworld.frontend.poscreator.panes.stable.MainParentPane;
import homer.tastyworld.frontend.poscreator.panes.stable.MenuParentPane;
import homer.tastyworld.frontend.starterpack.api.requests.MyParams;
import homer.tastyworld.frontend.starterpack.base.exceptions.SubscriptionDaysAreOverError;
import homer.tastyworld.frontend.starterpack.base.utils.ui.AlertWindow;
import homer.tastyworld.frontend.starterpack.base.utils.ui.helpers.VirtualKeyboard;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
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

    private DynamicParentPane productsPane;
    @FXML
    private AnchorPane productsPaneParent;
    @FXML
    private AnchorPane productsPaneBackInMenuImgBtn;
    @FXML
    private AnchorPane productsPaneMenuTopic;
    @FXML
    private GridPane productPaneImgProductsContainer;

    private StableParentPane menuPane;
    @FXML
    private AnchorPane menuPaneParent;
    @FXML
    private AnchorPane menuPaneDeleteOrderImgBtn, menuPaneLookOrderImgBtn;
    @FXML
    private AnchorPane menuPaneTopic;
    @FXML
    private GridPane menuPaneImgMenuContainer;

    private DynamicParentPane endOrderPane;
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

    private void initEndOrderPane() {
        endOrderPane = EndOrderCreatingParentPane
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
        endOrderPane.initialize();
    }

    private void initVirtualKeyboard() {
        virtualKeyboard = new VirtualKeyboard(virtualKeyboardPlace);
        VirtualKeyboardPrompts.setPromptsContainer(virtualKeyboardPrompts);
        VirtualKeyboardPrompts.setInputField(endOrderCreatingDeliveryField);
    }

    @FXML
    private void initialize() {
        long subscriptionAvailableDays = MyParams.getTokenSubscriptionAvailableDays();
        if (subscriptionAvailableDays < 0) {
            throw new SubscriptionDaysAreOverError();
        }
        ParentPane.setBase(base);
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
        if (OrderCreating.id == null) {
            AlertWindow.showInfo(
                    "Заказ пуст", "Добавьте хотя бы один продукт, прежде чем завершить создание заказа", true
            );
        } else {
            endOrderPane.fill(OrderCreating.id);
            endOrderPane.openAndCloseFrom(menuPaneParent);
        }
    }

    @FXML
    void endOrderCreatingOpenMenuImgBtnPressed() {
        endOrderPane.clean();
        menuPane.openAndCloseFrom(endOrderCreatingPaneParent);
    }

    @FXML
    void endOrderCreatingCommitImgBtnPressed() {
        String address = endOrderCreatingDeliveryField.getText().trim();
        if (!address.equals("")) {
            OrderCreating.editDeliveryAddress(address);
            VirtualKeyboardPrompts.appendVar(endOrderCreatingDeliveryField);
        }
        OrderCreating.finish();
        if (endOrderCreatingIsPaidCheckBox.isSelected()) {
            OrderCreating.setPaid();
        }
        endOrderPane.clean();
        mainPane.openAndCloseFrom(endOrderCreatingPaneParent);
    }

    @FXML
    void hideVirtualKeyboardParentPane() {
        virtualKeyboardPaneParent.setVisible(false);
    }

    @FXML
    void showVirtualKeyboardParentPane() {
        VirtualKeyboardPrompts.clean();
        virtualKeyboardPaneParent.setVisible(true);
    }

}
