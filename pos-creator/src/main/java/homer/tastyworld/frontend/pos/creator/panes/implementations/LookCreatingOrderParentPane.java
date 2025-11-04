package homer.tastyworld.frontend.pos.creator.panes.implementations;

import homer.tastyworld.frontend.pos.creator.POSCreatorApplication;
import homer.tastyworld.frontend.pos.creator.panes.ParentPane;
import homer.tastyworld.frontend.starterpack.api.sra.entity.order.Order;
import homer.tastyworld.frontend.starterpack.api.sra.entity.order.OrderItem;
import homer.tastyworld.frontend.starterpack.api.sra.entity.order.OrderItemModifier;
import homer.tastyworld.frontend.starterpack.utils.ui.DialogWindows;
import homer.tastyworld.frontend.starterpack.utils.ui.helpers.AdaptiveTextHelper;
import homer.tastyworld.frontend.starterpack.utils.ui.helpers.PaneHelper;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.util.function.Function;

@SuperBuilder
public class LookCreatingOrderParentPane extends ParentPane<Order> {

    private final AnchorPane goBackInMenuCategoriesImgBtn, formOrderImgBtn;
    private final AnchorPane nameTopic, totalAmountTopic;
    private final TextField deliveryField;
    private final AnchorPane discountTopic;
    private final Slider discountSlider;
    private final CheckBox sendPaymentCheckBox;
    private final ScrollPane itemsScroll;

    private Label nameLabel;
    private final URL deleteItemImgBtnResource = POSCreatorApplication.class.getResource("images/buttons/LookCreatingOrderPane/DeleteItem.png");

    private Order order;
    private final BigDecimal BIG_DECIMAL_ONE_HUNDRED = BigDecimal.valueOf(100);
    private final ObjectProperty<BigDecimal> itemsTotalPrice = new SimpleObjectProperty<>(BigDecimal.ZERO);
    private final ObjectProperty<BigDecimal> reverseDiscount = new SimpleObjectProperty<>(BigDecimal.ONE);

    public BigDecimal getOrderDiscount() {
        return BigDecimal.ONE.subtract(reverseDiscount.get());
    }

    public void setItemsScrollContent() {
        VBox rows = new VBox(5);
        rows.setFillWidth(true);
        rows.setAlignment(Pos.CENTER);

        ObservableList<Node> items = rows.getChildren();
        BigDecimal itemsTotalPriceTemp = BigDecimal.ZERO;
        for (OrderItem item : order.getItems()) {
            addItemRows(item, items);
            itemsTotalPriceTemp = itemsTotalPriceTemp.add(item.totalPrice());
        }
        itemsTotalPrice.set(itemsTotalPriceTemp);

        itemsScroll.setContent(rows);
    }

    private void addItemRows(OrderItem item, ObservableList<Node> rows) {
        HBox row = new HBox(5);
        row.getStyleClass().add("order-item-line");
        row.prefWidthProperty().bind(itemsScroll.widthProperty());
        row.prefHeightProperty().bind(itemsScroll.heightProperty().multiply(0.16));
        row.setAlignment(Pos.CENTER);

        AnchorPane space1 = new AnchorPane();
        AnchorPane deleteImgBtn = getDeleteItemFromOrderImgBtn(item, rows, row);
        AnchorPane name = new AnchorPane();
        AdaptiveTextHelper.setTextCentre(name, item.name(), 0.065, null);
        AnchorPane quantity = new AnchorPane();
        AdaptiveTextHelper.setTextCentre(quantity, item.quantity() + " " + item.qtyMeasure().shortName, 0.25, null);
        VBox modifiers = getModifiers(item.notDefaultModifiers());
        AnchorPane price = new AnchorPane();
        AdaptiveTextHelper.setTextCentre(price, item.totalPrice() + " р", 0.2, null);
        AnchorPane space2 = new AnchorPane();

        row.getChildren().addAll(space1, deleteImgBtn, name, quantity, modifiers, price, space2);
        HBox.setHgrow(space1, Priority.ALWAYS);
        HBox.setHgrow(deleteImgBtn, Priority.ALWAYS);
        HBox.setHgrow(name, Priority.ALWAYS);
        HBox.setHgrow(quantity, Priority.ALWAYS);
        HBox.setHgrow(modifiers, Priority.ALWAYS);
        HBox.setHgrow(price, Priority.ALWAYS);
        HBox.setHgrow(space2, Priority.ALWAYS);
        space1.prefWidthProperty().bind(row.widthProperty().multiply(0.01));
        deleteImgBtn.prefWidthProperty().bind(row.widthProperty().multiply(0.08));
        name.prefWidthProperty().bind(row.widthProperty().multiply(0.33));
        quantity.prefWidthProperty().bind(row.widthProperty().multiply(0.12));
        modifiers.prefWidthProperty().bind(row.widthProperty().multiply(0.3));
        price.prefWidthProperty().bind(row.widthProperty().multiply(0.15));
        space2.prefWidthProperty().bind(row.widthProperty().multiply(0.01));

        rows.add(row);
    }

    private AnchorPane getDeleteItemFromOrderImgBtn(OrderItem item, ObservableList<Node> deleteFrom, Node toDelete) {
        AnchorPane deleteImgBtn = new AnchorPane();
        deleteImgBtn.getStyleClass().add("img-btn");
        PaneHelper.setImageBackgroundCentre(deleteImgBtn, deleteItemImgBtnResource);

        deleteImgBtn.setOnMouseClicked(event -> {
            if (!DialogWindows.askBool(
                    "Удалить", "Нет",
                    "Редактирование заказа",
                    "Вы уверены что хотите удалить '%s' из заказа?".formatted(item.name()),
                    "Продолжить?"
            )) {
                return;
            }
            order.deleteItem(item);
            deleteFrom.remove(toDelete);
            itemsTotalPrice.set(itemsTotalPrice.get().subtract(item.totalPrice()));
        });

        return deleteImgBtn;
    }

    private VBox getModifiers(OrderItemModifier[] modifiers) {
        VBox result = new VBox();
        result.setFillWidth(true);
        result.setAlignment(Pos.CENTER);
        for (OrderItemModifier modifier : modifiers) {
            AnchorPane modifierLine = new AnchorPane();
            AdaptiveTextHelper.setTextCentre(
                    modifierLine,
                    "%s (%s р) %s %s".formatted(
                            modifier.name(), modifier.unitPrice(), modifier.quantity(), modifier.qtyMeasure().shortName
                    ),
                    0.05,
                    null
            );
            result.getChildren().add(modifierLine);
        }
        return result;
    }

    private void initImgBtns() {
        PaneHelper.setImageBackgroundBottom(
                goBackInMenuCategoriesImgBtn,
                POSCreatorApplication.class.getResourceAsStream("images/buttons/LookCreatingOrderPane/BackInMenuCategories.png")
        );
        PaneHelper.setImageBackgroundBottom(
                formOrderImgBtn,
                POSCreatorApplication.class.getResourceAsStream("images/buttons/LookCreatingOrderPane/FormOrder.png")
        );
    }

    private void initTotalAmountListeners() {
        Label totalAmountLabel = AdaptiveTextHelper.setTextCentre(totalAmountTopic, "Стоимость: 0 р", 0.1, null);
        Function<Void, BigDecimal> calculateTotalAmount = (ignored) -> {
            BigDecimal totalAmount = itemsTotalPrice.get().multiply(reverseDiscount.get()).setScale(2, RoundingMode.HALF_UP);
            return totalAmount.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ONE : totalAmount;
        };
        itemsTotalPrice.addListener(
                (observable, oldValue, newValue) -> totalAmountLabel.setText("Стоимость: " + calculateTotalAmount.apply(null) + " р")
        );
        reverseDiscount.addListener(
                (observable, oldValue, newValue) -> totalAmountLabel.setText("Стоимость: " + calculateTotalAmount.apply(null) + " р")
        );
    }

    private void initDiscountListeners() {
        Label discountLabel = AdaptiveTextHelper.setTextCentre(discountTopic, "Скидка 0%", 0.12, null);
        discountSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            int discount = newValue.intValue();
            discountLabel.setText("Скидка " + discount + "%");
            reverseDiscount.set(BigDecimal.ONE.subtract(BigDecimal.valueOf(discount).divide(BIG_DECIMAL_ONE_HUNDRED, 2, RoundingMode.HALF_UP)));
        });
    }

    private void initTopicsLabel() {
        nameLabel = AdaptiveTextHelper.setTextCentre(nameTopic, "", 0.16, null);
    }

    @Override
    public void initialize() {
        initImgBtns();
        initTotalAmountListeners();
        initDiscountListeners();
        initTopicsLabel();
    }

    @Override
    protected void beforeOpen(Order order) {
        this.order = order;

        setItemsScrollContent();
        deliveryField.setText(order.getDeliveryInfo());
        discountSlider.setValue(order.getDiscount().multiply(BIG_DECIMAL_ONE_HUNDRED).doubleValue());
        nameLabel.setText("Заказ " + order.getName());
        sendPaymentCheckBox.setSelected(order.isPaid());
    }

    @Override
    protected void beforeClose() {
        sendPaymentCheckBox.setSelected(false);
        nameLabel.setText("");
        discountSlider.setValue(0.0);
        deliveryField.clear();
        itemsScroll.setVvalue(0.0);
        itemsScroll.setContent(null);
        itemsTotalPrice.set(BigDecimal.ONE);

        this.order = null;
    }

}
