package homer.tastyworld.frontend.pos.creator.panes.dynamic;

import homer.tastyworld.frontend.pos.creator.POSCreatorApplication;
import homer.tastyworld.frontend.starterpack.base.utils.ui.helpers.AdaptiveTextHelper;
import homer.tastyworld.frontend.starterpack.base.utils.ui.helpers.PaneHelper;
import homer.tastyworld.frontend.starterpack.order.Order;
import homer.tastyworld.frontend.starterpack.order.core.items.OrderItem;
import homer.tastyworld.frontend.starterpack.order.core.items.OrderItemAdditive;
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
import java.math.BigDecimal;

@SuperBuilder
public class LookOrderParentPane extends DynamicParentPane {

    private static Long currentOrderID = null;
    private final AnchorPane nameTopic, totalPriceTopic;
    private final AnchorPane closePaneImgBtn, setDoneImgBtn;
    private final GridPane itemsContainer;
    private final TextField deliveryInfoField;
    private final Button setPaidBtn;
    @Getter(lazy = true, value = AccessLevel.PRIVATE)
    private final Label nameTopicLabel = AdaptiveTextHelper.setTextCentre(nameTopic, "", 6, null);
    @Getter(lazy = true, value = AccessLevel.PRIVATE)
    private final Label priceTopicLabel = AdaptiveTextHelper.setTextCentre(totalPriceTopic, "", 10, null);
    private final ScrollPane scroll = new ScrollPane();

    public static Long getCurrentOrderID() {
        return currentOrderID;
    }

    @Override
    protected void cacheTask(long ignored) {}

    @Override
    public void fill(long orderID) {
        Order order = Order.get(orderID);
        currentOrderID = orderID;
        deliveryInfoField.setText(order.getDeliveryInfo());
        getNameTopicLabel().setText("Заказ " + order.name);
        getPriceTopicLabel().setText("Стоимость: " + order.getTotalPrice());
        setPaidBtn.setDisable(order.isPaid());
        scroll.setContent(computeItemsTable(order.getItems()));
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
        row.prefHeightProperty().bind(scroll.heightProperty().divide(5));
        row.setAlignment(Pos.CENTER);

        AnchorPane space1 = new AnchorPane();
        AnchorPane name = getItemName(item);
        AnchorPane pieceQTY = getItemQTY(item);
        VBox additives = getAdditives(item);
        AnchorPane price = getItemPrice(item);
        AnchorPane space2 = new AnchorPane();

        row.getChildren().addAll(space1, name, pieceQTY, additives, price, space2);
        HBox.setHgrow(space1, Priority.ALWAYS);
        HBox.setHgrow(name, Priority.ALWAYS);
        HBox.setHgrow(pieceQTY, Priority.ALWAYS);
        HBox.setHgrow(additives, Priority.ALWAYS);
        HBox.setHgrow(price, Priority.ALWAYS);
        HBox.setHgrow(space2, Priority.ALWAYS);
        space1.prefWidthProperty().bind(row.widthProperty().multiply(0.02));
        name.prefWidthProperty().bind(row.widthProperty().multiply(0.35));
        pieceQTY.prefWidthProperty().bind(row.widthProperty().multiply(0.13));
        additives.prefWidthProperty().bind(row.widthProperty().multiply(0.35));
        price.prefWidthProperty().bind(row.widthProperty().multiply(0.13));
        space2.prefWidthProperty().bind(row.widthProperty().multiply(0.02));

        return row;
    }

    private AnchorPane getItemName(OrderItem item) {
        AnchorPane name = new AnchorPane();
        AdaptiveTextHelper.setTextCentre(name, item.productName(), 13, null);
        return name;
    }

    private AnchorPane getItemQTY(OrderItem item) {
        AnchorPane pieceQTY = new AnchorPane();
        AdaptiveTextHelper.setTextCentre(pieceQTY, item.pieceQTY() + " " + item.pieceType().shortName, 4, null);
        return pieceQTY;
    }

    private VBox getAdditives(OrderItem item) {
        VBox result = new VBox();
        result.setFillWidth(true);
        result.setAlignment(Pos.CENTER);
        for (OrderItemAdditive additive : item.getNotDefaultAdditives()) {
            result.getChildren().add(getAdditiveLine(additive));
        }
        return result;
    }

    private AnchorPane getAdditiveLine(OrderItemAdditive additive) {
        AnchorPane additiveLine = new AnchorPane();
        additiveLine.setStyle("-fx-border-color: #000000;");
        AdaptiveTextHelper.setTextCentre(
                additiveLine,
                "%s %s %s".formatted(additive.productAdditiveName(), additive.pieceQTY(), additive.pieceType().shortName),
                15,
                null
        );
        return additiveLine;
    }

    private AnchorPane getItemPrice(OrderItem item) {
        AnchorPane price = new AnchorPane();
        AdaptiveTextHelper.setTextCentre(
                price,
                item.pricePerPiece().multiply(BigDecimal.valueOf(item.pieceQTY())).toString(),
                4,
                null
        );
        return price;
    }

    @Override
    protected void cleanTask() {
        scroll.setVvalue(0.0);
        getNameTopicLabel().setText("");
        getPriceTopicLabel().setText("");
        deliveryInfoField.clear();
        setPaidBtn.setDisable(true);
        currentOrderID = null;
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

}
