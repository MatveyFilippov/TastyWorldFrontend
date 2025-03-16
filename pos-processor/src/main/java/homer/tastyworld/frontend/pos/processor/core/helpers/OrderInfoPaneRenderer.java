package homer.tastyworld.frontend.pos.processor.core.helpers;

import homer.tastyworld.frontend.pos.processor.POSProcessorApplication;
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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class OrderInfoPaneRenderer {

    public static Long orderID = null;
    public static boolean isEditable = false;
    private static ScrollPane scroll;
    private static Label orderCreatedTimeTopic, orderDeliveryTopic, orderNameTopic;
    private static final Map<Long, Map<String, Object>> productCache = new ConcurrentHashMap<>();
    private static final Map<Long, Map<String, Object>> additiveCache = new ConcurrentHashMap<>();

    public static void init(ScrollPane scroll, AnchorPane orderCreatedTimeTopic, AnchorPane orderDeliveryTopic, AnchorPane orderNameTopic) {
        OrderInfoPaneRenderer.scroll = scroll;
        StringExpression miscInfoTopicsFontSize = AdaptiveTextHelper.getFontSize(orderDeliveryTopic, 1.5);
        OrderInfoPaneRenderer.orderCreatedTimeTopic = AdaptiveTextHelper.setTextCentre(orderCreatedTimeTopic, "", miscInfoTopicsFontSize, null);
        OrderInfoPaneRenderer.orderDeliveryTopic = AdaptiveTextHelper.setTextCentre(orderDeliveryTopic, "", miscInfoTopicsFontSize, null);
        OrderInfoPaneRenderer.orderNameTopic = AdaptiveTextHelper.setTextCentre(orderNameTopic, "", 1.35, Color.BLACK);;
    }

    public static void render(long orderID) {
        clean();
        Request request = new Request("/order/read", Method.GET);
        request.putInBody("id", orderID);
        Map<String, Object> orderInfo = request.request().getResultAsJSON();
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
        Request request = new Request("/order/read_item", Method.GET);
        for (int i = 0; i < itemIDs.length; i++) {
            request.putInBody("id", itemIDs[i]);
            table.add(getItemLine(request.request().getResultAsJSON()), 0, i);
        }
        return table;
    }

    private static Map<String, Object> getProductInfo(long productID) {
        return productCache.computeIfAbsent(productID, id -> {
            Request request = new Request("/product/read", Method.GET);
            request.putInBody("id", id);
            return request.request().getResultAsJSON();
        });
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
        PaneHelper.setImageBackgroundCentre(
                editImgBtn, "editOrderItemImgBtn",
                POSProcessorApplication.class.getResourceAsStream("images/buttons/editOrderItemImgBtn.png")
        );
        Map<String, Object> productInfo = getProductInfo(TypeChanger.toLong(itemInfo.get("PRODUCT_ID")));
        setEditOnClick(
                editImgBtn, TypeChanger.toLong(itemInfo.get("ID")),
                TypeChanger.toInt(itemInfo.get("PEACE_QTY")), (String) productInfo.get("NAME")
        );
        return editImgBtn;
    }

    private static void setEditOnClick(AnchorPane editImgBtn, long itemID, int qty, String name) {
        editImgBtn.setOpacity(isEditable ? 1 : 0.5);
        if (isEditable) {
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
        Map<String, Object> productInfo = getProductInfo(TypeChanger.toLong(itemInfo.get("PRODUCT_ID")));
        AdaptiveTextHelper.setTextCentre(name, (String) productInfo.get("NAME"), 5, null);
        return name;
    }

    private static AnchorPane getItemQTY(Map<String, Object> itemInfo) {
        AnchorPane qty = new AnchorPane();
        Map<String, Object> productInfo = getProductInfo(TypeChanger.toLong(itemInfo.get("PRODUCT_ID")));
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
            Map<String, Object> additiveInfo = additiveCache.computeIfAbsent(pair.getKey(), additiveID -> {
                Request request = new Request("/product/read_additive", Method.GET);
                request.putInBody("id", additiveID);
                return request.request().getResultAsJSON();
            });
            result.getChildren().add(getAdditiveLine(additiveInfo, pair.getValue()));
        }
        return result;
    }

    private static AnchorPane getAdditiveLine(Map<String, Object> additiveInfo, int qty) {
        AnchorPane additiveLine = new AnchorPane();
        String line = additiveInfo.get("NAME") + " " + qty + (
                additiveInfo.get("PIECE_TYPE").equals("ONE_HUNDRED_GRAMS") ? " Гр" : " Шт"
        );
        AdaptiveTextHelper.setTextCentre(additiveLine, line, 35, null);
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
    }

}
