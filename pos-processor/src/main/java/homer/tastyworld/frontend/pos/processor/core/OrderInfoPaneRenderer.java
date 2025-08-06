package homer.tastyworld.frontend.pos.processor.core;

import homer.tastyworld.frontend.pos.processor.POSProcessorApplication;
import homer.tastyworld.frontend.starterpack.base.AppDateTime;
import homer.tastyworld.frontend.starterpack.base.utils.ui.AlertWindow;
import homer.tastyworld.frontend.starterpack.base.utils.ui.helpers.AdaptiveTextHelper;
import homer.tastyworld.frontend.starterpack.base.utils.ui.helpers.PaneHelper;
import homer.tastyworld.frontend.starterpack.entity.misc.ProductPieceType;
import homer.tastyworld.frontend.starterpack.order.Order;
import homer.tastyworld.frontend.starterpack.order.core.items.OrderItem;
import homer.tastyworld.frontend.starterpack.order.core.items.OrderItemAdditive;
import javafx.beans.binding.StringExpression;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import java.net.URL;

public class OrderInfoPaneRenderer {

    private static final URL editItemImgBtnResource = POSProcessorApplication.class.getResource("images/buttons/editOrderItemImgBtn.png");
    public static Order order = null;
    private static ScrollPane scroll;
    private static Label orderCreatedTimeTopic, orderDeliveryTopic, orderNameTopic;

    public static void render(long orderID) {
        clean();
        order = Order.get(orderID);
        orderCreatedTimeTopic.setText(order.createdAt.format(AppDateTime.DATETIME_FORMAT));
        orderDeliveryTopic.setText("Доставка: " + (order.getDeliveryInfo() == null ? "Нет" : "Да"));
        orderNameTopic.setText("Заказ " + order.name);
        scroll.setContent(computeItemsTable(order.getItems()));
    }

    public static void rerender() {
        if (order != null) {
            render(order.id);
        }
    }

    private static GridPane computeItemsTable(OrderItem[] items) {
        GridPane table = new GridPane();
        table.setVgap(5);
        table.setAlignment(Pos.CENTER);
        for (int i = 0; i < items.length; i++) {
            table.add(getItemLine(items[i]), 0, i);
        }
        return table;
    }

    private static HBox getItemLine(OrderItem item) {
        HBox row = new HBox(7);
        row.setStyle("-fx-border-color: #000000;");
        row.prefWidthProperty().bind(scroll.widthProperty());
        row.prefHeightProperty().bind(scroll.heightProperty().divide(6));
        row.setAlignment(Pos.CENTER);

        AnchorPane space1 = new AnchorPane();
        AnchorPane editImgBn = getEditImgBtn(item);
        AnchorPane name = new AnchorPane();
        AdaptiveTextHelper.setTextCentre(name, item.productName(), 13, null);
        AnchorPane qty = new AnchorPane();
        AdaptiveTextHelper.setTextCentre(qty, item.pieceQTY()+ " " + item.pieceType().shortName, 4, null);
        VBox additives = getNotDefaultAdditives(item.getNotDefaultAdditives());
        AnchorPane space2 = new AnchorPane();

        row.getChildren().addAll(space1, editImgBn, name, qty, additives, space2);
        HBox.setHgrow(space1, Priority.ALWAYS);
        HBox.setHgrow(editImgBn, Priority.ALWAYS);
        HBox.setHgrow(name, Priority.ALWAYS);
        HBox.setHgrow(qty, Priority.ALWAYS);
        HBox.setHgrow(additives, Priority.ALWAYS);
        HBox.setHgrow(space2, Priority.ALWAYS);
        space1.prefWidthProperty().bind(row.widthProperty().multiply(0.01));
        editImgBn.prefWidthProperty().bind(row.widthProperty().multiply(0.1));
        name.prefWidthProperty().bind(row.widthProperty().multiply(0.35));
        qty.prefWidthProperty().bind(row.widthProperty().multiply(0.15));
        additives.prefWidthProperty().bind(row.widthProperty().multiply(0.38));
        space2.prefWidthProperty().bind(row.widthProperty().multiply(0.01));

        return row;
    }

    private static boolean isItemEditable(OrderItem item) {
        if (item.pieceType() == ProductPieceType.PIECES) {
            return false;
        }
        return !order.isPaid();
    }

    private static AnchorPane getEditImgBtn(OrderItem item) {
        AnchorPane editImgBtn = new AnchorPane();
        PaneHelper.setImageBackgroundCentre(editImgBtn, editItemImgBtnResource);

        editImgBtn.setOpacity(isItemEditable(item) ? 1 : 0.5);
        editImgBtn.setOnMouseClicked(event -> {
            if (isItemEditable(item)) {
                EditItemQtyPane.open(item.id(), item.pieceQTY(), item.productName());
            } else {
                editImgBtn.setOpacity(0.5);
                AlertWindow.showInfo(
                        "Позицию нельзя отредактировать", "Продукт или заказ не поддерживает изменение количества", true
                );
            }
        });

        return editImgBtn;
    }

    private static VBox getNotDefaultAdditives(OrderItemAdditive[] additives) {
        VBox result = new VBox();
        result.setFillWidth(true);
        result.setAlignment(Pos.CENTER);
        for (OrderItemAdditive additive : additives) {
            AnchorPane additiveLine = new AnchorPane();
            AdaptiveTextHelper.setTextCentre(
                    additiveLine,
                    "%s %s %s".formatted(additive.productAdditiveName(), additive.pieceQTY(), additive.pieceType().shortName),
                    20,
                    null
            );
            result.getChildren().add(additiveLine);
        }
        return result;
    }

    public static void cleanIfFilled(long orderID) {
    if (order != null && order.id == orderID) {
            clean();
        }
    }

    public static void clean() {
        order = null;
        orderDeliveryTopic.setText("");
        orderCreatedTimeTopic.setText("");
        orderNameTopic.setText("");
        scroll.setVvalue(0.0);
        scroll.setContent(null);
    }

    public static void init(ScrollPane scroll, AnchorPane orderCreatedTimeTopic, AnchorPane orderDeliveryTopic, AnchorPane orderNameTopic) {
        OrderInfoPaneRenderer.scroll = scroll;
        StringExpression miscInfoTopicsFontSize = AdaptiveTextHelper.getFontSize(orderDeliveryTopic, 12);
        OrderInfoPaneRenderer.orderCreatedTimeTopic = AdaptiveTextHelper.setTextCentre(orderCreatedTimeTopic, "", miscInfoTopicsFontSize, null);
        OrderInfoPaneRenderer.orderDeliveryTopic = AdaptiveTextHelper.setTextCentre(orderDeliveryTopic, "", miscInfoTopicsFontSize, null);
        OrderInfoPaneRenderer.orderNameTopic = AdaptiveTextHelper.setTextCentre(orderNameTopic, "", 10, Color.BLACK);;
    }

}
