package homer.tastyworld.frontend.pos.creator;

import homer.tastyworld.frontend.pos.creator.core.orders.OrderCreator;
import homer.tastyworld.frontend.pos.creator.core.orders.printer.OrderNamePrinterPageFactory;
import homer.tastyworld.frontend.pos.creator.core.vkb.VirtualKeyboardPrompts;
import homer.tastyworld.frontend.pos.creator.panes.ParentPane;
import homer.tastyworld.frontend.pos.creator.panes.implementations.ProductParentPane;
import homer.tastyworld.frontend.pos.creator.panes.implementations.LookCreatingOrderParentPane;
import homer.tastyworld.frontend.pos.creator.panes.implementations.LookCreatedOrderParentPane;
import homer.tastyworld.frontend.pos.creator.panes.implementations.MenuCategoryProductsParentPane;
import homer.tastyworld.frontend.pos.creator.panes.implementations.MainParentPane;
import homer.tastyworld.frontend.pos.creator.panes.implementations.MenuCategoriesParentPane;
import homer.tastyworld.frontend.starterpack.api.sra.entity.menu.Product;
import homer.tastyworld.frontend.starterpack.api.sra.entity.misc.OrderStatus;
import homer.tastyworld.frontend.starterpack.api.sra.entity.misc.ProductType;
import homer.tastyworld.frontend.starterpack.api.sra.entity.order.Order;
import homer.tastyworld.frontend.starterpack.api.sra.entity.order.OrderUtils;
import homer.tastyworld.frontend.starterpack.base.exceptions.controlled.ExternalModuleUnavailableException;
import homer.tastyworld.frontend.starterpack.utils.managers.external.payment.EvotorMobcashier;
import homer.tastyworld.frontend.starterpack.utils.managers.external.printer.PrinterManager;
import homer.tastyworld.frontend.starterpack.utils.ui.AlertWindows;
import homer.tastyworld.frontend.starterpack.utils.ui.DialogWindows;
import homer.tastyworld.frontend.starterpack.utils.ui.vkb.VirtualKeyboard;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class POSCreatorController {

    @FXML
    private AnchorPane base;

    private ParentPane<Void> mainPane;
    @FXML
    private AnchorPane mainPaneParent;
    @FXML
    private GridPane mainPaneGrid;
    @FXML
    private AnchorPane mainPaneDaysLeftAlert, mainPaneDaysLeftAlertTopic;
    @FXML
    private AnchorPane mainPaneClientPointNameTopic;
    @FXML
    private AnchorPane mainPaneOpenSettingsImgBtn, mainPaneDraftNewOrderImgBtn;
    @FXML
    private AnchorPane mainPanePreparingOrdersTableTopic, mainPaneReadyOrdersTableTopic;
    @FXML
    private GridPane mainPanePreparingOrdersTable, mainPaneReadyOrdersTable;

    private MenuCategoriesParentPane menuCategoriesPane;
    @FXML
    private AnchorPane menuCategoriesPaneParent;
    @FXML
    private AnchorPane menuCategoriesPaneDeleteCreatingOrderImgBtn, menuCategoriesPaneLookCreatingOrderImgBtn;
    @FXML
    private AnchorPane menuCategoriesPaneNameTopic;
    @FXML
    private ScrollPane menuCategoriesPaneIconsScroll;

    private MenuCategoryProductsParentPane menuCategoryProductsPane;
    @FXML
    private AnchorPane menuCategoryProductsPaneParent;
    @FXML
    private AnchorPane menuCategoryProductsPaneGoBackInMenuCategoriesImgBtn;
    @FXML
    private AnchorPane menuCategoryProductsPaneNameTopic;
    @FXML
    private ScrollPane menuCategoryProductsPaneIconsScroll;

    private ProductParentPane productPane;
    @FXML
    private AnchorPane productPaneParent;
    @FXML
    private AnchorPane productPaneGoBackInMenuCategoryProductsImgBtn, productPaneAddProductToOrderImgBtn;
    @FXML
    private AnchorPane productPaneNameTopic;
    @FXML
    private AnchorPane productPanePriceTopic;
    @FXML
    private TextField productPaneTotalPriceField;
    @FXML
    private AnchorPane productPaneMinusQuantityImgBtn, productPanePlusQuantityImgBtn;
    @FXML
    private AnchorPane productPaneQuantityMeasureTopic;
    @FXML
    private TextField productPaneQuantityFiled;
    @FXML
    private GridPane productPaneNumbersKeyboardGrid;
    @FXML
    private AnchorPane productPaneProductToppingsTopic;
    @FXML
    private ScrollPane productPaneProductToppingsScroll;

    private LookCreatingOrderParentPane lookCreatingOrderPane;
    @FXML
    private AnchorPane lookCreatingOrderPaneParent;
    @FXML
    private AnchorPane lookCreatingOrderPaneGoBackInMenuCategoriesImgBtn, lookCreatingOrderPaneFormOrderImgBtn;
    @FXML
    private AnchorPane lookCreatingOrderPaneNameTopic, lookCreatingOrderPaneTotalAmountTopic;
    @FXML
    private ScrollPane lookCreatingOrderPaneItemsScroll;
    @FXML
    private TextField lookCreatingOrderPaneDeliveryField;
    @FXML
    private AnchorPane lookCreatingOrderPaneDiscountTopic;
    @FXML
    private Slider lookCreatingOrderPaneDiscountSlider;
    @FXML
    private CheckBox lookCreatingOrderPaneSendPaymentCheckBox;

    private LookCreatedOrderParentPane lookCreatedOrderPane;
    @FXML
    private AnchorPane lookCreatedOrderPaneParent;
    @FXML
    private AnchorPane lookCreatedOrderPaneNameTopic, lookCreatedOrderPaneTotalAmountTopic;
    @FXML
    private AnchorPane lookCreatedOrderPaneCloseImgBtn, lookCreatedOrderPaneCompleteOrderImgBtn;
    @FXML
    private GridPane lookCreatedOrderPaneItemsContainer;
    @FXML
    private TextField lookCreatedOrderPaneDeliveryField;
    @FXML
    private Button lookCreatedOrderPaneSetPaidBtn;

    @FXML
    private AnchorPane virtualKeyboardPaneParent;
    @FXML
    private HBox virtualKeyboardPrompts;
    @FXML
    private VBox virtualKeyboardKeys;


    private void initMainPane() {
        mainPane = MainParentPane
                .builder()
                .current(mainPaneParent)
                .lookCreatedOrderPane(lookCreatedOrderPane)
                .currentGrid(mainPaneGrid)
                .daysLeftAlert(mainPaneDaysLeftAlert)
                .daysLeftAlertTopic(mainPaneDaysLeftAlertTopic)
                .clientPointNameTopic(mainPaneClientPointNameTopic)
                .openSettingsImgBtn(mainPaneOpenSettingsImgBtn)
                .draftNewOrderImgBtn(mainPaneDraftNewOrderImgBtn)
                .preparingOrdersTableTopic(mainPanePreparingOrdersTableTopic)
                .readyOrdersTableTopic(mainPaneReadyOrdersTableTopic)
                .preparingOrdersTable(mainPanePreparingOrdersTable)
                .readyOrdersTable(mainPaneReadyOrdersTable)
                .build();
        mainPane.initialize();
    }

    private void initMenuCategoriesPane() {
        menuCategoriesPane = MenuCategoriesParentPane
                .builder()
                .current(menuCategoriesPaneParent)
                .menuCategoryProductsPane(menuCategoryProductsPane)
                .deleteCreatingOrderImgBtn(menuCategoriesPaneDeleteCreatingOrderImgBtn)
                .lookCreatingOrderImgBtn(menuCategoriesPaneLookCreatingOrderImgBtn)
                .nameTopic(menuCategoriesPaneNameTopic)
                .iconsScroll(menuCategoriesPaneIconsScroll)
                .build();
        menuCategoriesPane.initialize();
    }

    private void initMenuCategoryProductsPane() {
        menuCategoryProductsPane = MenuCategoryProductsParentPane
                .builder()
                .current(menuCategoryProductsPaneParent)
                .productPane(productPane)
                .goBackInMenuCategoriesImgBtn(menuCategoryProductsPaneGoBackInMenuCategoriesImgBtn)
                .nameTopic(menuCategoryProductsPaneNameTopic)
                .iconsScroll(menuCategoryProductsPaneIconsScroll)
                .build();
        menuCategoryProductsPane.initialize();
    }

    private void initProductPane() {
        productPane = ProductParentPane
                .builder()
                .current(productPaneParent)
                .goBackInMenuCategoryProductsImgBtn(productPaneGoBackInMenuCategoryProductsImgBtn)
                .addProductToOrderImgBtn(productPaneAddProductToOrderImgBtn)
                .nameTopic(productPaneNameTopic)
                .priceTopic(productPanePriceTopic)
                .totalPriceField(productPaneTotalPriceField)
                .plusQuantityImgBtn(productPanePlusQuantityImgBtn)
                .minusQuantityImgBtn(productPaneMinusQuantityImgBtn)
                .productQuantityMeasureTopic(productPaneQuantityMeasureTopic)
                .productQuantityFiled(productPaneQuantityFiled)
                .numbersKeyboardGrid(productPaneNumbersKeyboardGrid)
                .productToppingsTopic(productPaneProductToppingsTopic)
                .productToppingsScroll(productPaneProductToppingsScroll)
                .build();
        productPane.initialize();
    }

    private void initEndOrderPane() {
        lookCreatingOrderPane = LookCreatingOrderParentPane
                .builder()
                .current(lookCreatingOrderPaneParent)
                .goBackInMenuCategoriesImgBtn(lookCreatingOrderPaneGoBackInMenuCategoriesImgBtn)
                .formOrderImgBtn(lookCreatingOrderPaneFormOrderImgBtn)
                .nameTopic(lookCreatingOrderPaneNameTopic)
                .totalAmountTopic(lookCreatingOrderPaneTotalAmountTopic)
                .deliveryField(lookCreatingOrderPaneDeliveryField)
                .discountTopic(lookCreatingOrderPaneDiscountTopic)
                .discountSlider(lookCreatingOrderPaneDiscountSlider)
                .sendPaymentCheckBox(lookCreatingOrderPaneSendPaymentCheckBox)
                .itemsScroll(lookCreatingOrderPaneItemsScroll)
                .build();
        lookCreatingOrderPane.initialize();
    }

    private void initLookOrderPane() {
        lookCreatedOrderPane = LookCreatedOrderParentPane
                .builder()
                .current(lookCreatedOrderPaneParent)
                .nameTopic(lookCreatedOrderPaneNameTopic)
                .totalAmountTopic(lookCreatedOrderPaneTotalAmountTopic)
                .closePaneImgBtn(lookCreatedOrderPaneCloseImgBtn)
                .setDoneImgBtn(lookCreatedOrderPaneCompleteOrderImgBtn)
                .itemsContainer(lookCreatedOrderPaneItemsContainer)
                .deliveryInfoField(lookCreatedOrderPaneDeliveryField)
                .setPaidBtn(lookCreatedOrderPaneSetPaidBtn)
                .build();
        lookCreatedOrderPane.initialize();
    }

    private void initVirtualKeyboard() {
        VirtualKeyboard.addTo(virtualKeyboardKeys, null);
        VirtualKeyboardPrompts.setPromptsContainer(virtualKeyboardPrompts);
        VirtualKeyboardPrompts.setInputField(lookCreatingOrderPaneDeliveryField);
        VirtualKeyboardPrompts.setInputField(lookCreatedOrderPaneDeliveryField);
    }

    @FXML
    private void initialize() {
        ParentPane.setBase(base);
        initLookOrderPane();
        initProductPane();
        initMenuCategoryProductsPane();
        initMenuCategoriesPane();
        initEndOrderPane();
        initVirtualKeyboard();
        initMainPane();
        mainPane.open(null);
    }

    @FXML
    void mainPaneOpenSettingsImgBtnPressed() {
        AlertWindows.showInfo(
                "К сожалению, нет доступа", "В настощий момент данный блок является недоступным к использованию", false
        );
    }

    @FXML
    void mainPaneDraftNewOrderImgBtnPressed() {
        OrderCreator.start(true);
        menuCategoriesPane.openAndCloseFrom(mainPane, null);
    }

    @FXML
    void menuCategoriesPaneDeleteCreatingOrderImgBtnPressed() {
        OrderCreator.cancel();
        mainPane.openAndCloseFrom(menuCategoriesPane, null);
    }

    @FXML
    void menuCategoriesPaneLookCreatingOrderImgBtnPressed() {
        Order order = OrderCreator.get();
        if (order.getItems().length == 0) {
            AlertWindows.showInfo(
                    "Заказ пуст", "Добавьте хотя бы один продукт, прежде чем завершить создание заказа", true
            );
            return;
        }
        lookCreatingOrderPane.openAndCloseFrom(menuCategoriesPane, order);
    }

    @FXML
    void menuCategoryProductsPaneGoBackInMenuCategoriesImgBtnPressed() {
        menuCategoriesPane.openAndCloseFrom(menuCategoryProductsPane, null);
    }

    @FXML
    void productPaneGoBackInMenuCategoryProductsImgBtnPressed() {
        menuCategoryProductsPane.openAndCloseFrom(productPane, productPane.getProductCategory());
    }

    @FXML
    void productPaneAddProductToOrderImgBtnPressed() {
        int quantity = productPane.getProductQuantity();
        Product product = productPane.getProduct();

        if (quantity < product.getQtyMin() || quantity > product.getQtyMax()) {
            AlertWindows.showInfo(
                    "Недопустимое количество",
                    "Продукт должен быть в рамках [%s-%s] %s".formatted(product.getQtyMin(), product.getQtyMax(), product.getQtyMeasure().shortName),
                    true
            );
            return;
        }

        String mark = null;
        if (product.getType() != ProductType.NORMAL) {
            while (mark == null) {
                Optional<String> optionalMark = DialogWindows.get(
                        "Маркировка продукта",
                        "Отсканируйте честный знак",
                        product.getName()
                );
                if (optionalMark.isEmpty()) {
                    return;
                }
                mark = optionalMark.get();
                if (mark.isBlank() || mark.length() < 30) {
                    AlertWindows.showInfo(
                            "Чтоб добавить продукт, необходимо обязательно отсканировать честный знак",
                            "Недопустимая маркировка: отсутсвует или слишком короткая",
                            true
                    );
                    mark = null;
                }
            }
        }

        Order order = OrderCreator.get();
        Map<Long, Integer> notDefaultProductToppingsIDaQTY = productPane.getProductNotDefaultProductToppingsQTY().entrySet()
                                                                        .stream()
                                                                        .collect(Collectors.toMap(
                                                                                entry -> entry.getKey().getProductToppingID(),
                                                                                Map.Entry::getValue
                                                                        ));
        order.createItem(product.getProductID(), mark, quantity, notDefaultProductToppingsIDaQTY);

        menuCategoriesPane.openAndCloseFrom(productPane, null);
    }

    @FXML
    void lookCreatingOrderPaneGoBackInMenuCategoriesImgBtnPressed() {
        menuCategoriesPane.openAndCloseFrom(lookCreatingOrderPane, null);
    }

    @FXML
    void lookCreatingOrderPaneFormOrderImgBtn() {
        Order order = OrderCreator.get();
        if (order.getItems().length == 0) {
            AlertWindows.showInfo(
                    "Заказ пуст", "Добавьте хотя бы один продукт, прежде чем завершить создание заказа", true
            );
            return;
        }

        boolean isSendPaymentSelected = lookCreatingOrderPaneSendPaymentCheckBox.isSelected();
        boolean isAccess = DialogWindows.askBool(
                "Отправить", "Нет", "Создание заказа",
                "Отправить заказ на кухню%s?".formatted(isSendPaymentSelected ? " и кассу" : ""),
                "%sтменить это действие нльзя".formatted(isSendPaymentSelected ? "После оплаты позиции заказа будут закрыты для редактирования, о" : "О")
        );
        if (!isAccess) {
            lookCreatingOrderPaneSendPaymentCheckBox.setSelected(false);
            return;
        }

        String deliveryInfo = lookCreatingOrderPaneDeliveryField.getText();
        order.setDeliveryInfo(deliveryInfo);
        if (deliveryInfo != null && !deliveryInfo.isBlank()) {
            VirtualKeyboardPrompts.appendVar(lookCreatingOrderPaneDeliveryField);
        }

        BigDecimal discount = BigDecimal.valueOf(lookCreatingOrderPaneDiscountSlider.getValue()).divide(BigDecimal.valueOf(100));
        order.setDiscount(discount);

        order.upgradeStatus(OrderStatus.FORMED);
        OrderCreator.finish();

        boolean isPaid;
        try {
            EvotorMobcashier.sendToCashRegister(order);
            isPaid = DialogWindows.askBool(
                    "Оплачено", "Повторю позже", "Оплата заказа",
                    "Отправил заказ на кассу, проверьте и оплатите",
                    "Кухня получила все позиции, заказ уже готовится"
            );
        } catch (ExternalModuleUnavailableException ignored) {
            isPaid = DialogWindows.askBool(
                    "Оплачено", "Вернуться позже", "Оплата заказа",
                    "Произошла ошибка с кассой, но можно отметить вручную, что оплата прошла или вернуться к этому позже",
                    "Кухня получила все позиции, заказ уже готовится"
            );
        }
        if (isPaid) {
            order.setPaid();
        } else {
            PrinterManager.print(OrderNamePrinterPageFactory.getFor(order.getOrderID()));
        }

        mainPane.openAndCloseFrom(lookCreatingOrderPane, null);
    }

    @FXML
    void closeLookOrderPane() {
        Order order = OrderUtils.getOrCreateInstance(LookCreatedOrderParentPane.getCurrentOrderID());

        String deliveryInfo = lookCreatedOrderPaneDeliveryField.getText();
        if (deliveryInfo != null && !deliveryInfo.isBlank()) {
            VirtualKeyboardPrompts.appendVar(lookCreatedOrderPaneDeliveryField);
        }
        if (!Objects.equals(deliveryInfo, order.getDeliveryInfo())) {
            order.setDeliveryInfo(deliveryInfo);
        }

        lookCreatedOrderPane.close();
    }

    @FXML
    void lookOrderSetDoneImgBtnPressed() {
        Order order = OrderUtils.getOrCreateInstance(LookCreatedOrderParentPane.getCurrentOrderID());
        boolean isPaid = order.isPaid();
        boolean isAccess = DialogWindows.askBool(
                "Выдан", "Нет", "Завершение заказа",
                "Отметить заказ как выданный%s?".formatted(isPaid ? "" : " и оплаченный"),
                "Заказ будет завешён, отменить это действие нльзя"
        );
        if (!isAccess) {
            return;
        }
        if (!isPaid) {
            order.setPaid();
            lookCreatedOrderPaneSetPaidBtn.setDisable(true);
        }
        order.upgradeStatus(OrderStatus.COMPLETED);
        closeLookOrderPane();
    }

    @FXML
    void lookOrderSetPaidBtnPressed() {
        Order order = OrderUtils.getOrCreateInstance(LookCreatedOrderParentPane.getCurrentOrderID());

        boolean isPaid;
        try {
            EvotorMobcashier.sendToCashRegister(order);
            isPaid = DialogWindows.askBool(
                    "Оплачено", "Повторю позже", "Оплата заказа",
                    "Отправил заказ на кассу, проверьте и оплатите",
                    "Позиции заказа будут закрыты для редактирования, отменить это действие нльзя"
            );
        } catch (ExternalModuleUnavailableException ignored) {
            isPaid = DialogWindows.askBool(
                    "Оплачено", "Вернуться позже", "Оплата заказа",
                    "Произошла ошибка с кассой, но можно отметить вручную, что оплата прошла или вернуться к этому позже",
                    "Позиции заказа будут закрыты для редактирования, отменить это действие нльзя"
            );
        }
        if (isPaid) {
            order.setPaid();
            lookCreatedOrderPaneSetPaidBtn.setDisable(true);
        }
    }

    @FXML
    void showVirtualKeyboardParentPane() {
        virtualKeyboardPaneParent.setVisible(true);
    }

    @FXML
    void hideVirtualKeyboardParentPane() {
        VirtualKeyboardPrompts.clean();
        virtualKeyboardPaneParent.setVisible(false);
    }

}
