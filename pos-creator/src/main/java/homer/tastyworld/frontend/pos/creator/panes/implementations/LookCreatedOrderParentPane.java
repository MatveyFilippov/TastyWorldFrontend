package homer.tastyworld.frontend.pos.creator.panes.implementations;

import homer.tastyworld.frontend.pos.creator.POSCreatorApplication;
import homer.tastyworld.frontend.pos.creator.panes.ParentPane;
import homer.tastyworld.frontend.starterpack.api.sra.entity.order.Order;
import homer.tastyworld.frontend.starterpack.api.sra.entity.order.OrderItem;
import homer.tastyworld.frontend.starterpack.api.sra.entity.order.OrderItemModifier;
import homer.tastyworld.frontend.starterpack.utils.ui.helpers.AdaptiveTextHelper;
import homer.tastyworld.frontend.starterpack.utils.ui.helpers.PaneHelper;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import lombok.experimental.SuperBuilder;

@SuperBuilder
public class LookCreatedOrderParentPane extends ParentPane<Order> {

    private final AnchorPane closeImgBtn, completeOrderImgBtn, setOrderPaidImgBtn;
    private final AnchorPane nameTopic, totalAmountTopic, discountTopic;
    private final TextField deliveryField;
    private final ScrollPane itemsScroll;

    private Label nameLabel, totalAmountLabel, discountLabel;

    private Order order;
    private final BigDecimal BIG_DECIMAL_ONE_HUNDRED = BigDecimal.valueOf(100);

    public Order getOrder() {
        if (order == null) {
            throw new NullPointerException("The order being opened does not exist");
        }
        return order;
    }

    public void setItemsScrollContent() {
        VBox rows = new VBox(5);
        rows.setFillWidth(true);
        rows.setAlignment(Pos.CENTER);

        ObservableList<Node> items = rows.getChildren();
        Arrays.stream(order.getItems()).forEach(item -> addItemRow(item, items));

        itemsScroll.setContent(rows);
    }

    private void addItemRow(OrderItem item, ObservableList<Node> rows) {
        HBox row = new HBox(5);
        row.getStyleClass().add("order-item-line");
        row.prefWidthProperty().bind(itemsScroll.widthProperty());
        row.prefHeightProperty().bind(itemsScroll.heightProperty().multiply(0.16));
        row.setAlignment(Pos.CENTER);

        AnchorPane space1 = new AnchorPane();
        AnchorPane name = new AnchorPane();
        AdaptiveTextHelper.setTextCentre(name, item.name(), 0.075, null);
        AnchorPane quantity = new AnchorPane();
        AdaptiveTextHelper.setTextCentre(quantity, item.quantity() + " " + item.qtyMeasure().shortName, 0.25, null);
        VBox modifiers = getModifiers(item.notDefaultModifiers());
        AnchorPane price = new AnchorPane();
        AdaptiveTextHelper.setTextCentre(price, item.totalPrice() + " р", 0.2, null);
        AnchorPane space2 = new AnchorPane();

        row.getChildren().addAll(space1, name, quantity, modifiers, price, space2);
        HBox.setHgrow(space1, Priority.ALWAYS);
        HBox.setHgrow(name, Priority.ALWAYS);
        HBox.setHgrow(quantity, Priority.ALWAYS);
        HBox.setHgrow(modifiers, Priority.ALWAYS);
        HBox.setHgrow(price, Priority.ALWAYS);
        HBox.setHgrow(space2, Priority.ALWAYS);
        space1.prefWidthProperty().bind(row.widthProperty().multiply(0.02));
        name.prefWidthProperty().bind(row.widthProperty().multiply(0.35));
        quantity.prefWidthProperty().bind(row.widthProperty().multiply(0.13));
        modifiers.prefWidthProperty().bind(row.widthProperty().multiply(0.35));
        price.prefWidthProperty().bind(row.widthProperty().multiply(0.13));
        space2.prefWidthProperty().bind(row.widthProperty().multiply(0.02));

        rows.add(row);
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
        PaneHelper.setImageBackgroundCentre(
                closeImgBtn,
                POSCreatorApplication.class.getResourceAsStream("images/buttons/LookCreatedOrderPane/Close.png")
        );
        PaneHelper.setImageBackgroundCentre(
                completeOrderImgBtn,
                POSCreatorApplication.class.getResourceAsStream("images/buttons/LookCreatedOrderPane/CompleteOrder.png")
        );
        PaneHelper.setImageBackgroundCentre(
                setOrderPaidImgBtn,
                POSCreatorApplication.class.getResourceAsStream("images/buttons/LookCreatedOrderPane/SetOrderPaid.png")
        );
    }

    private void initTopicsLabel() {
        nameLabel = AdaptiveTextHelper.setTextCentre(nameTopic, "", 0.16, null);
        totalAmountLabel = AdaptiveTextHelper.setTextCentre(totalAmountTopic, "", 0.1, null);
        discountLabel = AdaptiveTextHelper.setTextCentre(discountTopic, "", 0.14, null);
    }

    @Override
    public void initialize() {
        initImgBtns();
        initTopicsLabel();
    }

    @Override
    protected void beforeOpen(Order order) {
        this.order = order;

        setItemsScrollContent();
        setOrderPaidImgBtn.setDisable(order.isPaid());
        totalAmountLabel.setText("Стоимость: " + order.getTotalAmount().setScale(2, RoundingMode.HALF_UP) + " р");
        deliveryField.setText(order.getDeliveryInfo());
        discountLabel.setText("Скидка " + order.getDiscount().multiply(BIG_DECIMAL_ONE_HUNDRED).setScale(0, RoundingMode.UNNECESSARY) + "%");
        nameLabel.setText("Заказ " + order.getName());
    }

    @Override
    protected void beforeClose() {
        nameLabel.setText("");
        discountLabel.setText("");
        deliveryField.clear();
        totalAmountLabel.setText("");
        setOrderPaidImgBtn.setDisable(true);
        itemsScroll.setVvalue(0.0);
        itemsScroll.setContent(null);

        this.order = null;
    }

}
