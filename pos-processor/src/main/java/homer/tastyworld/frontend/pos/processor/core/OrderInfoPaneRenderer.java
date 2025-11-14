package homer.tastyworld.frontend.pos.processor.core;

import homer.tastyworld.frontend.pos.processor.POSProcessorApplication;
import homer.tastyworld.frontend.starterpack.api.sra.entity.misc.MenuQuantitativeMeasure;
import homer.tastyworld.frontend.starterpack.api.sra.entity.order.Order;
import homer.tastyworld.frontend.starterpack.api.sra.entity.order.OrderItem;
import homer.tastyworld.frontend.starterpack.api.sra.entity.order.OrderItemModifier;
import homer.tastyworld.frontend.starterpack.api.sra.entity.order.OrderUtils;
import homer.tastyworld.frontend.starterpack.utils.ui.AlertWindows;
import homer.tastyworld.frontend.starterpack.utils.ui.helpers.AdaptiveTextHelper;
import homer.tastyworld.frontend.starterpack.utils.ui.helpers.PaneHelper;
import javafx.beans.binding.StringExpression;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Optional;

public class OrderInfoPaneRenderer {

    private static final DateTimeFormatter orderDraftedDateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    private static final URL editItemQuantityImgBtnResource = POSProcessorApplication.class.getResource("images/buttons/EditItemQuantity.png");
    private static Order renderedOrder;
    private static ScrollPane scroll;
    private static ObservableList<Node> items;
    private static Label orderDraftedTimeLabel, orderDeliveryLabel, orderNameLabel;

    public static Optional<Order> getRenderedOrder() {
        return Optional.ofNullable(renderedOrder);
    }

    public static void render(long orderID) {
        clean();
        renderedOrder = OrderUtils.getOrCreateInstance(orderID);
        orderDraftedTimeLabel.setText(renderedOrder.getDraftAt().format(orderDraftedDateTimeFormatter));
        orderDeliveryLabel.setText("Доставка: " + (renderedOrder.getDeliveryInfo() == null ? "Нет" : "Да"));
        orderNameLabel.setText("Заказ " + renderedOrder.getName());
        items.addAll(
                Arrays.stream(renderedOrder.getItems()).map(OrderInfoPaneRenderer::getItemLine).toList()
        );
    }

    public static void rerender() {
        if (renderedOrder != null) {
            render(renderedOrder.getOrderID());
        }
    }

    private static HBox getItemLine(OrderItem item) {
        HBox row = new HBox(5);
        row.getStyleClass().add("order-position-item");
        row.prefWidthProperty().bind(scroll.widthProperty().multiply(0.98));
        row.prefHeightProperty().bind(scroll.heightProperty().multiply(0.16));
        row.setAlignment(Pos.CENTER);

        AnchorPane space1 = new AnchorPane();
        AnchorPane editImgBn = getEditItemQuantityImgBtn(item);
        AnchorPane name = new AnchorPane();
        AdaptiveTextHelper.setTextCentre(name, item.name(), 0.075, null);
        AnchorPane quantity = new AnchorPane();
        AdaptiveTextHelper.setTextCentre(quantity, item.quantity() + " " + item.qtyMeasure().shortName, 0.25, null);
        VBox modifiers = getModifiers(item.notDefaultModifiers());
        AnchorPane space2 = new AnchorPane();

        row.getChildren().addAll(space1, editImgBn, name, quantity, modifiers, space2);
        HBox.setHgrow(space1, Priority.ALWAYS);
        HBox.setHgrow(editImgBn, Priority.ALWAYS);
        HBox.setHgrow(name, Priority.ALWAYS);
        HBox.setHgrow(quantity, Priority.ALWAYS);
        HBox.setHgrow(modifiers, Priority.ALWAYS);
        HBox.setHgrow(space2, Priority.ALWAYS);
        space1.prefWidthProperty().bind(row.widthProperty().multiply(0.01));
        editImgBn.prefWidthProperty().bind(row.widthProperty().multiply(0.08));
        name.prefWidthProperty().bind(row.widthProperty().multiply(0.35));
        quantity.prefWidthProperty().bind(row.widthProperty().multiply(0.15));
        modifiers.prefWidthProperty().bind(row.widthProperty().multiply(0.4));
        space2.prefWidthProperty().bind(row.widthProperty().multiply(0.01));

        return row;
    }

    private static boolean isItemEditable(OrderItem item) {
        if (item.qtyMeasure() == MenuQuantitativeMeasure.PIECES) {
            return false;
        }
        return !renderedOrder.isPaid();
    }

    private static AnchorPane getEditItemQuantityImgBtn(OrderItem item) {
        AnchorPane editImgBtn = new AnchorPane();
        editImgBtn.getStyleClass().add("anchor-pane-as-button");
        PaneHelper.setImageBackgroundCentre(editImgBtn, editItemQuantityImgBtnResource);

        editImgBtn.setDisable(!isItemEditable(item));
        editImgBtn.setOnMouseClicked(event -> {
            if (isItemEditable(item)) {
                EditItemQuantityPane.open(item);
            } else {
                editImgBtn.setDisable(true);
                AlertWindows.showInfo(
                        "Позицию '%s' нельзя отредактировать".formatted(item.name()),
                        "Продукт или заказ больше не поддерживает изменение количества",
                        true
                );
            }
        });

        return editImgBtn;
    }

    private static VBox getModifiers(OrderItemModifier[] modifiers) {
        VBox result = new VBox();
        result.setFillWidth(true);
        result.setAlignment(Pos.CENTER);
        for (OrderItemModifier modifier : modifiers) {
            AnchorPane modifierLine = new AnchorPane();
            AdaptiveTextHelper.setTextCentre(
                    modifierLine,
                    modifier.name() + " " + modifier.quantity() + " " + modifier.qtyMeasure().shortName,
                    0.05,
                    null
            );
            result.getChildren().add(modifierLine);
        }
        return result;
    }

    public static void clean() {
        renderedOrder = null;
        orderDeliveryLabel.setText("");
        orderDraftedTimeLabel.setText("");
        orderNameLabel.setText("Заказ не выбран");
        items.clear();
        scroll.setVvalue(0.0);
    }

    public static void cleanIfFilled(long orderID) {
        if (renderedOrder != null && renderedOrder.getOrderID() == orderID) {
            clean();
        }
    }

    public static void init(ScrollPane scroll, AnchorPane orderDraftedTimeTopic, AnchorPane orderDeliveryTopic, AnchorPane orderNameTopic) {
        VBox rows = new VBox(5);
        rows.setFillWidth(true);
        rows.setAlignment(Pos.CENTER);
        scroll.setContent(rows);
        OrderInfoPaneRenderer.items = rows.getChildren();
        OrderInfoPaneRenderer.scroll = scroll;
        StringExpression miscInfoTopicsFontSize = AdaptiveTextHelper.getFontSize(orderDeliveryTopic, 0.08);
        OrderInfoPaneRenderer.orderDraftedTimeLabel = AdaptiveTextHelper.setTextCentre(orderDraftedTimeTopic, "", miscInfoTopicsFontSize, null);
        OrderInfoPaneRenderer.orderDeliveryLabel = AdaptiveTextHelper.setTextCentre(orderDeliveryTopic, "", miscInfoTopicsFontSize, null);
        OrderInfoPaneRenderer.orderNameLabel = AdaptiveTextHelper.setTextCentre(orderNameTopic, "Заказ не выбран", 0.1, Color.BLACK);;
    }

}
