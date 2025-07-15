package homer.tastyworld.frontend.pos.creator.panes.dynamic;

import homer.tastyworld.frontend.pos.creator.POSCreatorApplication;
import homer.tastyworld.frontend.pos.creator.core.orders.OrderCreator;
import homer.tastyworld.frontend.starterpack.base.utils.ui.DialogWindow;
import homer.tastyworld.frontend.starterpack.base.utils.ui.helpers.AdaptiveTextHelper;
import homer.tastyworld.frontend.starterpack.base.utils.ui.helpers.PaneHelper;
import homer.tastyworld.frontend.starterpack.order.Order;
import homer.tastyworld.frontend.starterpack.order.core.items.OrderItem;
import homer.tastyworld.frontend.starterpack.order.core.items.OrderItemAdditive;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
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
import java.net.URL;

@SuperBuilder
public class EndOrderCreatingParentPane extends DynamicParentPane {

    private final AnchorPane goBackInMenuImgBtn, submitEndingImgBtn;
    private final AnchorPane nameTopic, totalPriceTopic;
    private final GridPane itemsContainer;
    private final TextField deliveryInfoField;
    private final CheckBox isPaidCheckBox;
    private final ScrollPane scroll = new ScrollPane();
    @Getter(lazy = true, value = AccessLevel.PRIVATE)
    private final Label nameTopicLabel = AdaptiveTextHelper.setTextCentre(nameTopic, "", 6, null);
    @Getter(lazy = true, value = AccessLevel.PRIVATE)
    private final Label totalPriceTopicLabel = AdaptiveTextHelper.setTextCentre(totalPriceTopic, "", 10, null);;
    private final URL deleteItemImgBtnResource = POSCreatorApplication.class.getResource("images/buttons/EndOrderCreatingPane/DeleteItem.png");

    @Override
    protected void cacheTask(long ignored) {}

    @Override
    public void fill(long orderID) {
        Order order = OrderCreator.get();
        deliveryInfoField.setText(order.getDeliveryInfo());
        getNameTopicLabel().setText("Заказ " + order.name);
        getTotalPriceTopicLabel().setText("Стоимость: " + order.getTotalPrice());
        isPaidCheckBox.setSelected(order.isPaid());
        scroll.setContent(computeItemsTable(order.getItems()));
    }

    public GridPane computeItemsTable(OrderItem[] items) {
        GridPane table = new GridPane();
        table.setVgap(5);
        table.setAlignment(Pos.CENTER);
        ObservableList<Node> lines = table.getChildren();
        for (int i = 0; i < items.length; i++) {
            table.add(getItemLine(items[i], lines), 0, i);
        }
        return table;
    }

    private HBox getItemLine(OrderItem item, ObservableList<Node> lines) {
        HBox row = new HBox(7);
        row.setStyle("-fx-border-color: #000000;");
        row.prefWidthProperty().bind(scroll.widthProperty());
        row.prefHeightProperty().bind(scroll.heightProperty().divide(7));
        row.setAlignment(Pos.CENTER);

        AnchorPane delete = getDeleteItem(item, lines, row);
        AnchorPane name = getItemName(item);
        AnchorPane pieceQTY = getItemQTY(item);
        VBox additives = getAdditives(item);
        AnchorPane price = getItemPrice(item);
        AnchorPane space = new AnchorPane();

        row.getChildren().addAll(delete, name, pieceQTY, additives, price, space);
        HBox.setHgrow(delete, Priority.ALWAYS);
        HBox.setHgrow(name, Priority.ALWAYS);
        HBox.setHgrow(pieceQTY, Priority.ALWAYS);
        HBox.setHgrow(additives, Priority.ALWAYS);
        HBox.setHgrow(price, Priority.ALWAYS);
        HBox.setHgrow(space, Priority.ALWAYS);
        delete.prefWidthProperty().bind(row.widthProperty().multiply(0.1));
        name.prefWidthProperty().bind(row.widthProperty().multiply(0.35));
        pieceQTY.prefWidthProperty().bind(row.widthProperty().multiply(0.12));
        additives.prefWidthProperty().bind(row.widthProperty().multiply(0.29));
        price.prefWidthProperty().bind(row.widthProperty().multiply(0.12));
        space.prefWidthProperty().bind(row.widthProperty().multiply(0.02));

        return row;
    }

    private AnchorPane getDeleteItem(OrderItem item, ObservableList<Node> deleteFrom, Node toDelete) {
        AnchorPane delete = new AnchorPane();
        PaneHelper.setImageBackgroundCentre(delete, deleteItemImgBtnResource);
        delete.setOnMouseClicked(event -> {
            if (!DialogWindow.askBool(
                    "Да", "Нет", "Редактирование заказа",
                    String.format("Вы уверены что хотите удалить '%s' из заказа?", item.productName()),
                    "Продолжить?"
            )) {
                return;
            }
            Order order = OrderCreator.get();
            order.removeItem(item.id());
            deleteFrom.remove(toDelete);
            totalPriceTopic.getChildren().clear();
            getTotalPriceTopicLabel().setText("Стоимость: " + order.getTotalPrice());
        });
        return delete;
    }

    private AnchorPane getItemName(OrderItem item) {
        AnchorPane name = new AnchorPane();
        AdaptiveTextHelper.setTextCentre(name, item.productName(), 15, null);
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
        String line = additive.additiveName() + " " + additive.pieceQTY() + additive.pieceType().shortName;
        AdaptiveTextHelper.setTextCentre(additiveLine, line, 17, null);
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
        getTotalPriceTopicLabel().setText("");
        deliveryInfoField.clear();
        isPaidCheckBox.setSelected(false);
    }

    private void initItemsTable() {
        scroll.setFitToWidth(true);
        itemsContainer.add(scroll, 1, 2);
    }

    private void initImgBtnsInEndOrderCreatingPane() {
        PaneHelper.setImageBackgroundBottom(
                goBackInMenuImgBtn,
                POSCreatorApplication.class.getResourceAsStream("images/buttons/EndOrderCreatingPane/BackInMenu.png")
        );
        PaneHelper.setImageBackgroundBottom(
                submitEndingImgBtn,
                POSCreatorApplication.class.getResourceAsStream("images/buttons/EndOrderCreatingPane/SubmitEnding.png")
        );
    }

    @Override
    public void initialize() {
        initItemsTable();
        initImgBtnsInEndOrderCreatingPane();
    }

}
