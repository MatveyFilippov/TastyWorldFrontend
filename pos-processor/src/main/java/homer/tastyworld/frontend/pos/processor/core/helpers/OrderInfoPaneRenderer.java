package homer.tastyworld.frontend.pos.processor.core.helpers;

import homer.tastyworld.frontend.pos.processor.POSProcessorApplication;
import homer.tastyworld.frontend.pos.processor.core.cache.AdditivesCache;
import homer.tastyworld.frontend.pos.processor.core.cache.ProductsCache;
import homer.tastyworld.frontend.starterpack.api.Request;
import homer.tastyworld.frontend.starterpack.base.AppDateTime;
import homer.tastyworld.frontend.starterpack.base.utils.misc.TypeChanger;
import homer.tastyworld.frontend.starterpack.base.utils.ui.AlertWindow;
import homer.tastyworld.frontend.starterpack.base.utils.ui.helpers.AdaptiveTextHelper;
import homer.tastyworld.frontend.starterpack.base.utils.ui.helpers.PaneHelper;
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
import org.apache.hc.core5.http.Method;
import java.net.URL;
import java.util.Map;

public class OrderInfoPaneRenderer {

    private static final URL editItemImgBtnResource = POSProcessorApplication.class.getResource("images/buttons/editOrderItemImgBtn.png");
    public static Long orderID = null;
    public static boolean isEditable = false;
    private static final Request READ_ORDER_REQUEST = new Request("/order/read", Method.GET);
    private static final Request READ_ORDER_ITEM_REQUEST = new Request("/order/read_item", Method.GET);
    private static ScrollPane scroll;
    private static Label orderCreatedTimeTopic, orderDeliveryTopic, orderNameTopic;

    public static void render(long orderID) {
        clean();
        READ_ORDER_REQUEST.putInBody("id", orderID);
        Map<String, Object> orderInfo = READ_ORDER_REQUEST.request().getResultAsJSON();
        OrderInfoPaneRenderer.orderID = TypeChanger.toLong(orderInfo.get("ID"));
        OrderInfoPaneRenderer.isEditable = !TypeChanger.toBool(orderInfo.get("IS_PAID"));
        orderCreatedTimeTopic.setText(
                AppDateTime.backendToLocal(AppDateTime.parseDateTime(
                        (String) orderInfo.get("CREATED_AT")
                )).format(AppDateTime.DATETIME_FORMAT)
        );
        orderDeliveryTopic.setText(
                "Доставка: " + (orderInfo.get("DELIVERY_ADDRESS").equals("NOT FOR DELIVERY") ? "Нет" : "Да")
        );
        orderNameTopic.setText("Заказ #" + orderInfo.get("NAME"));
        scroll.setContent(computeItemsTable(TypeChanger.toSortedLongArray(orderInfo.get("ITEM_IDs"))));
    }

    public static void rerender() {
        if (orderID != null) {
            render(orderID);
        }
    }

    private static GridPane computeItemsTable(Long[] itemIDs) {
        GridPane table = new GridPane();
        table.setVgap(5);
        table.setAlignment(Pos.CENTER);
        for (int i = 0; i < itemIDs.length; i++) {
            READ_ORDER_ITEM_REQUEST.putInBody("id", itemIDs[i]);
            table.add(getItemLine(READ_ORDER_ITEM_REQUEST.request().getResultAsJSON()), 0, i);
        }
        return table;
    }

    private static HBox getItemLine(Map<String, Object> itemInfo) {
        HBox row = new HBox(7);
        row.setStyle("-fx-border-color: #000000;");
        row.prefWidthProperty().bind(scroll.widthProperty());
        row.prefHeightProperty().bind(scroll.heightProperty().divide(6));
        row.setAlignment(Pos.CENTER);

        AnchorPane space1 = new AnchorPane();
        AnchorPane editImgBn = getEditImgBtn(itemInfo);
        AnchorPane name = getItemName(itemInfo);
        AnchorPane qty = getItemQTY(itemInfo);
        VBox additives = getAdditives(itemInfo);
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

    private static AnchorPane getEditImgBtn(Map<String, Object> itemInfo) {
        AnchorPane editImgBtn = new AnchorPane();
        PaneHelper.setImageBackgroundCentre(editImgBtn, editItemImgBtnResource);
        Map<String, Object> productInfo = ProductsCache.impl.get(TypeChanger.toLong(itemInfo.get("PRODUCT_ID")));
        setEditOnClick(
                editImgBtn, TypeChanger.toLong(itemInfo.get("ID")),
                TypeChanger.toInt(itemInfo.get("PEACE_QTY")), (String) productInfo.get("NAME"),
                productInfo.get("PIECE_TYPE").equals("ONE_HUNDRED_GRAMS")
        );
        return editImgBtn;
    }

    private static void setEditOnClick(AnchorPane editImgBtn, long itemID, int qty, String name, boolean isProductEditable) {
        editImgBtn.setOpacity(isEditable ? 1 : 0.5);
        if (isEditable && isProductEditable) {
            editImgBtn.setOnMouseClicked(event -> EditItemQtyPane.open(itemID, qty, name));
        } else {
            editImgBtn.setOnMouseClicked(event -> AlertWindow.showInfo(
                    "Позицию нельзя отредактировать", "Заказ уже оплачен, изменить позицию невозможно", true
            ));
            editImgBtn.setOpacity(0.5);
        }
    }

    private static AnchorPane getItemName(Map<String, Object> itemInfo) {
        AnchorPane name = new AnchorPane();
        Map<String, Object> productInfo = ProductsCache.impl.get(TypeChanger.toLong(itemInfo.get("PRODUCT_ID")));
        AdaptiveTextHelper.setTextCentre(name, (String) productInfo.get("NAME"), 13, null);
        return name;
    }

    private static AnchorPane getItemQTY(Map<String, Object> itemInfo) {
        AnchorPane qty = new AnchorPane();
        Map<String, Object> productInfo = ProductsCache.impl.get(TypeChanger.toLong(itemInfo.get("PRODUCT_ID")));
        String peaceType = productInfo.get("PIECE_TYPE").equals("ONE_HUNDRED_GRAMS") ? "Гр" : "Шт";
        AdaptiveTextHelper.setTextCentre(qty, itemInfo.get("PEACE_QTY") + " " + peaceType, 4, null);
        return qty;
    }

    private static VBox getAdditives(Map<String, Object> itemInfo) {
        VBox result = new VBox();
        result.setFillWidth(true);
        result.setAlignment(Pos.CENTER);
        Map<Long, Integer> notDefaultAdditives = TypeChanger.toMap(
                itemInfo.get("NOT_DEFAULT_ADDITIVES"), Long.class, Integer.class
        );
        for (Map.Entry<Long, Integer> pair : notDefaultAdditives.entrySet()) {
            Map<String, Object> additiveInfo = AdditivesCache.impl.get(pair.getKey());
            result.getChildren().add(getAdditiveLine(additiveInfo, pair.getValue()));
        }
        return result;
    }

    private static AnchorPane getAdditiveLine(Map<String, Object> additiveInfo, int qty) {
        AnchorPane additiveLine = new AnchorPane();
        String line = additiveInfo.get("NAME") + " " + qty + (
                additiveInfo.get("PIECE_TYPE").equals("ONE_HUNDRED_GRAMS") ? " Гр" : " Шт"
        );
        AdaptiveTextHelper.setTextCentre(additiveLine, line, 20, null);
        return additiveLine;
    }

    public static void cleanIfFilled(long orderID) {
        if (OrderInfoPaneRenderer.orderID != null && OrderInfoPaneRenderer.orderID.equals(orderID)) {
            clean();
        }
    }

    public static void clean() {
        orderID = null;
        isEditable = false;
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
