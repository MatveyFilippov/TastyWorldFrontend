package homer.tastyworld.frontend.poscreator;

import homer.tastyworld.frontend.poscreator.core.orders.internal.OrderCreating;
import homer.tastyworld.frontend.poscreator.panes.ParentPane;
import homer.tastyworld.frontend.poscreator.panes.dynamic.AddProductParentPane;
import homer.tastyworld.frontend.poscreator.panes.dynamic.DynamicParentPane;
import homer.tastyworld.frontend.poscreator.panes.dynamic.ProductsParentPane;
import homer.tastyworld.frontend.poscreator.panes.stable.StableParentPane;
import homer.tastyworld.frontend.poscreator.panes.stable.MainParentPane;
import homer.tastyworld.frontend.poscreator.panes.stable.MenuParentPane;
import homer.tastyworld.frontend.starterpack.api.requests.MyParams;
import homer.tastyworld.frontend.starterpack.base.exceptions.SubscriptionDaysAreOverError;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

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

    private AddProductParentPane addProductPane;
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
    }

    @FXML
    void mainPaneSettingsImgBtnPressed() {

    }

    @FXML
    void mainPaneNewOrderImgBtnPressed() {
        OrderCreating.newOrder();
        menuPane.openAndCloseFrom(mainPaneParent);
    }

    @FXML
    void menuPaneDeleteOrderImgBtnPressed() {
        OrderCreating.delete();
        mainPane.openAndCloseFrom(menuPaneParent);
    }

    @FXML
    void menuPaneLookOrderImgBtnPressed() {

    }

    @FXML
    void productsPaneBackInMenuImgBtnPressed() {
        productsPane.clean();
        menuPane.openAndCloseFrom(productsPaneParent);
    }

    @FXML
    void addProductCloseImgBtnPressed() {
        addProductPane.clean();
        menuPane.openAndCloseFrom(addProductPaneParent);
    }

    @FXML
    void addProductSubmitImgBtnPressed() {
        // TODO: add product
        addProductCloseImgBtnPressed();
    }

}
