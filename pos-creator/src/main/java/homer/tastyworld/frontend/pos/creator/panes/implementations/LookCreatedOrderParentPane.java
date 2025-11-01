package homer.tastyworld.frontend.pos.creator.panes.implementations;

import homer.tastyworld.frontend.pos.creator.POSCreatorApplication;
import homer.tastyworld.frontend.pos.creator.panes.ParentPane;
import homer.tastyworld.frontend.starterpack.api.sra.entity.order.Order;
import homer.tastyworld.frontend.starterpack.api.sra.entity.order.OrderItem;
import homer.tastyworld.frontend.starterpack.api.sra.entity.order.OrderItemModifier;
import homer.tastyworld.frontend.starterpack.api.sra.entity.order.OrderUtils;
import homer.tastyworld.frontend.starterpack.utils.ui.helpers.AdaptiveTextHelper;
import homer.tastyworld.frontend.starterpack.utils.ui.helpers.PaneHelper;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
public class LookCreatedOrderParentPane extends ParentPane<Order> {

    private static Long currentOrderID = null;
    private final AnchorPane nameTopic, totalAmountTopic;
    private final AnchorPane closePaneImgBtn, setDoneImgBtn;
    private final GridPane itemsContainer;
    private final TextField deliveryInfoField;
    private final Button setPaidBtn;
    @Getter(lazy = true, value = AccessLevel.PRIVATE)
    private final Label nameTopicLabel = AdaptiveTextHelper.setTextCentre(nameTopic, "", 0.16, null);
    @Getter(lazy = true, value = AccessLevel.PRIVATE)
    private final Label priceTopicLabel = AdaptiveTextHelper.setTextCentre(totalAmountTopic, "", 0.1, null);
    private final ScrollPane scroll = new ScrollPane();

    public static Long getCurrentOrderID() {
        return currentOrderID;
    }

    public GridPane computeItemsTable(OrderItem[] items) {
        GridPane table = new GridPane();
        table.setVgap(5);
        table.setAlignment(Pos.CENTER);

        for (int i = 0; i < items.length; i++) {
            table.add(getItemLine(items[i]), 0, i);
        }

        return table;
    }

    private HBox getItemLine(OrderItem item) {
        HBox row = new HBox(7);
        row.setStyle("-fx-border-color: #000000;");
        row.prefWidthProperty().bind(scroll.widthProperty());
        row.prefHeightProperty().bind(scroll.heightProperty().multiply(0.2));
        row.setAlignment(Pos.CENTER);

        AnchorPane space1 = new AnchorPane();
        AnchorPane name = getItemName(item);
        AnchorPane pieceQTY = getItemQTY(item);
        VBox modifiers = getModifiers(item);
        AnchorPane price = getItemPrice(item);
        AnchorPane space2 = new AnchorPane();

        row.getChildren().addAll(space1, name, pieceQTY, modifiers, price, space2);
        HBox.setHgrow(space1, Priority.ALWAYS);
        HBox.setHgrow(name, Priority.ALWAYS);
        HBox.setHgrow(pieceQTY, Priority.ALWAYS);
        HBox.setHgrow(modifiers, Priority.ALWAYS);
        HBox.setHgrow(price, Priority.ALWAYS);
        HBox.setHgrow(space2, Priority.ALWAYS);
        space1.prefWidthProperty().bind(row.widthProperty().multiply(0.02));
        name.prefWidthProperty().bind(row.widthProperty().multiply(0.35));
        pieceQTY.prefWidthProperty().bind(row.widthProperty().multiply(0.13));
        modifiers.prefWidthProperty().bind(row.widthProperty().multiply(0.35));
        price.prefWidthProperty().bind(row.widthProperty().multiply(0.13));
        space2.prefWidthProperty().bind(row.widthProperty().multiply(0.02));

        return row;
    }

    private AnchorPane getItemName(OrderItem item) {
        AnchorPane name = new AnchorPane();
        AdaptiveTextHelper.setTextCentre(name, item.name(), 0.075, null);
        return name;
    }

    private AnchorPane getItemQTY(OrderItem item) {
        AnchorPane pieceQTY = new AnchorPane();
        AdaptiveTextHelper.setTextCentre(pieceQTY, item.quantity() + " " + item.qtyMeasure().shortName, 0.25, null);
        return pieceQTY;
    }

    private VBox getModifiers(OrderItem item) {
        VBox result = new VBox();
        result.setFillWidth(true);
        result.setAlignment(Pos.CENTER);
        for (OrderItemModifier modifier : item.notDefaultModifiers()) {
            result.getChildren().add(getModifierLine(modifier));
        }
        return result;
    }

    private AnchorPane getModifierLine(OrderItemModifier modifier) {
        AnchorPane modifierLine = new AnchorPane();
        modifierLine.setStyle("-fx-border-color: #000000;");
        AdaptiveTextHelper.setTextCentre(
                modifierLine,
                "%s %s %s".formatted(modifier.name(), modifier.quantity(), modifier.qtyMeasure().shortName),
                0.065,
                null
        );
        return modifierLine;
    }

    private AnchorPane getItemPrice(OrderItem item) {
        AnchorPane price = new AnchorPane();
        AdaptiveTextHelper.setTextCentre(price, item.totalPrice().toString(), 0.25, null);
        return price;
    }

    private void initItemsTable() {
        scroll.setFitToWidth(true);
        itemsContainer.add(scroll, 1, 1);
    }

    private void initImgBtnsInLookOrderPane() {
        PaneHelper.setImageBackgroundCentre(
                closePaneImgBtn,
                POSCreatorApplication.class.getResourceAsStream("images/buttons/LookOrderPane/ClosePane.png")
        );
        PaneHelper.setImageBackgroundCentre(
                setDoneImgBtn,
                POSCreatorApplication.class.getResourceAsStream("images/buttons/LookOrderPane/SetDone.png")
        );
    }

    @Override
    public void initialize() {
        initItemsTable();
        initImgBtnsInLookOrderPane();
    }

    @Override
    protected void beforeOpen(Order order) {
        currentOrderID = order.getOrderID();
        deliveryInfoField.setText(order.getDeliveryInfo());
        getNameTopicLabel().setText("Заказ " + order.getName());
        getPriceTopicLabel().setText("Стоимость: " + order.getTotalAmount());
        setPaidBtn.setDisable(order.isPaid());
        scroll.setContent(computeItemsTable(order.getItems()));
    }

    @Override
    protected void beforeClose() {
        scroll.setVvalue(0.0);
        getNameTopicLabel().setText("");
        getPriceTopicLabel().setText("");
        deliveryInfoField.clear();
        setPaidBtn.setDisable(true);
        currentOrderID = null;
    }

}
