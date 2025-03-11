package homer.tastyworld.frontend.pos.creator.panes.dynamic;

import homer.tastyworld.frontend.pos.creator.POSCreatorApplication;
import homer.tastyworld.frontend.starterpack.api.Request;
import homer.tastyworld.frontend.starterpack.base.utils.misc.TypeChanger;
import homer.tastyworld.frontend.starterpack.base.utils.ui.helpers.PaneHelper;
import homer.tastyworld.frontend.starterpack.base.utils.ui.helpers.TextHelper;
import javafx.beans.binding.StringExpression;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.apache.hc.core5.http.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@SuperBuilder
public class LookOrderParentPane extends DynamicParentPane {

    private AnchorPane lookOrderNameTopic, lookOrderTotalPriceTopic;
    private AnchorPane lookOrderClosePaneImgBtn, lookOrderSetDoneImgBtn;
    private GridPane lookOrderItemsContainer;
    private TextField lookOrderDeliveryField;
    private Button lookOrderSetPaidBtn;
    private static StringExpression nameTopicFontSize, priceTopicFontSize;
    private static final ScrollPane scroll = new ScrollPane();
    private static final Map<Long, Map<String, Object>> productCache = new ConcurrentHashMap<>();
    private static final Map<Long, Map<String, Object>> additiveCache = new ConcurrentHashMap<>();

    @Override
    protected String getCacheProcess(int ignored1, int ignored2) { return ""; }

    @Override
    protected void cacheTask(Long ignored) {}

    @Override
    public void fill(long orderID) {
        Request request = new Request("/order/read", Method.GET);
        request.putInBody("id", orderID);
        Map<String, Object> orderInfo = request.request().getResultAsJSON();
        String deliveryAddress = (String) orderInfo.get("DELIVERY_ADDRESS");
        if (!deliveryAddress.equals("NOT FOR DELIVERY")) {
            lookOrderDeliveryField.setText(deliveryAddress);
        }
        TextHelper.setTextCentre(lookOrderNameTopic, "Заказ #" + orderInfo.get("NAME"), nameTopicFontSize, null);
        TextHelper.setTextCentre(lookOrderTotalPriceTopic, "Стоимость: " + orderInfo.get("TOTAL_PRICE"), priceTopicFontSize, null);
        lookOrderSetPaidBtn.setDisable(TypeChanger.toBool(orderInfo.get("IS_PAID")));
        scroll.setContent(computeItemsTable(TypeChanger.toSortedLongArray(orderInfo.get("ITEM_IDs"))));
    }

    public GridPane computeItemsTable(Long[] itemIDs) {
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

    private Map<String, Object> getProductInfo(long productID) {
        return productCache.computeIfAbsent(productID, id -> {
            Request request = new Request("/product/read", Method.GET);
            request.putInBody("id", id);
            return request.request().getResultAsJSON();
        });
    }

    private HBox getItemLine(Map<String, Object> itemInfo) {
        HBox row = new HBox(7);
        row.setStyle("-fx-border-color: #000000;");
        row.prefWidthProperty().bind(scroll.widthProperty());
        row.prefHeightProperty().bind(scroll.heightProperty().divide(5));
        row.setAlignment(Pos.CENTER);

        AnchorPane space1 = new AnchorPane();
        AnchorPane name = getItemName(itemInfo);
        AnchorPane qty = getItemQTY(itemInfo);
        VBox additives = getAdditives(itemInfo);
        AnchorPane price = getItemPrice(itemInfo);
        AnchorPane space2 = new AnchorPane();

        row.getChildren().addAll(space1, name, qty, additives, price, space2);
        HBox.setHgrow(space1, Priority.ALWAYS);
        HBox.setHgrow(name, Priority.ALWAYS);
        HBox.setHgrow(qty, Priority.ALWAYS);
        HBox.setHgrow(additives, Priority.ALWAYS);
        HBox.setHgrow(price, Priority.ALWAYS);
        HBox.setHgrow(space2, Priority.ALWAYS);
        space1.prefWidthProperty().bind(row.widthProperty().multiply(0.02));
        name.prefWidthProperty().bind(row.widthProperty().multiply(0.35));
        qty.prefWidthProperty().bind(row.widthProperty().multiply(0.13));
        additives.prefWidthProperty().bind(row.widthProperty().multiply(0.35));
        price.prefWidthProperty().bind(row.widthProperty().multiply(0.13));
        space2.prefWidthProperty().bind(row.widthProperty().multiply(0.02));

        return row;
    }

    private AnchorPane getItemName(Map<String, Object> itemInfo) {
        AnchorPane name = new AnchorPane();
        Map<String, Object> productInfo = getProductInfo(TypeChanger.toLong(itemInfo.get("PRODUCT_ID")));
        TextHelper.setTextCentre(name, (String) productInfo.get("NAME"), TextHelper.getAdaptiveFontSize(name, 13), null);
        return name;
    }

    private AnchorPane getItemQTY(Map<String, Object> itemInfo) {
        AnchorPane qty = new AnchorPane();
        Map<String, Object> productInfo = getProductInfo(TypeChanger.toLong(itemInfo.get("PRODUCT_ID")));
        String peaceType = productInfo.get("PIECE_TYPE").equals("ONE_HUNDRED_GRAMS") ? "Гр" : "Шт";
        TextHelper.setTextCentre(qty, itemInfo.get("PEACE_QTY") + " " + peaceType, TextHelper.getAdaptiveFontSize(qty, 4), null);
        return qty;
    }

    private VBox getAdditives(Map<String, Object> itemInfo) {
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

    private AnchorPane getAdditiveLine(Map<String, Object> additiveInfo, int qty) {
        AnchorPane additiveLine = new AnchorPane();
        additiveLine.setStyle("-fx-border-color: #000000;");
        String line = additiveInfo.get("NAME") + " " + qty + (
                additiveInfo.get("PIECE_TYPE").equals("ONE_HUNDRED_GRAMS") ? " Гр" : " Шт"
        );
        TextHelper.setTextCentre(additiveLine, line, TextHelper.getAdaptiveFontSize(additiveLine, 15), null);
        return additiveLine;
    }

    private AnchorPane getItemPrice(Map<String, Object> itemInfo) {
        AnchorPane price = new AnchorPane();
        TextHelper.setTextCentre(price, TypeChanger.toBigDecimal(itemInfo.get(("ITEM_PRICE"))).toString(), TextHelper.getAdaptiveFontSize(price, 4), null);
        return price;
    }

    @Override
    protected void cleanTask() {
        lookOrderNameTopic.getChildren().clear();
        lookOrderTotalPriceTopic.getChildren().clear();
        lookOrderDeliveryField.clear();
        lookOrderSetPaidBtn.setDisable(true);
    }

    private void initItemsTable() {
        scroll.setFitToWidth(true);
        lookOrderItemsContainer.add(scroll, 1, 1);
    }

    private void initTopicsFontSize() {
        nameTopicFontSize = TextHelper.getAdaptiveFontSize(lookOrderNameTopic, 6);
        priceTopicFontSize = TextHelper.getAdaptiveFontSize(lookOrderTotalPriceTopic, 10);
    }

    private void initImgBtnsInLookOrderPane() {
        PaneHelper.setImageBackgroundCentre(
                lookOrderClosePaneImgBtn,
                POSCreatorApplication.class.getResourceAsStream("images/buttons/LookOrderPane/lookOrderClosePaneImgBtn.png")
        );
        PaneHelper.setImageBackgroundCentre(
                lookOrderSetDoneImgBtn,
                POSCreatorApplication.class.getResourceAsStream("images/buttons/LookOrderPane/lookOrderSetDoneImgBtn.png")
        );
    }

    @Override
    public void initialize() {
        initItemsTable();
        initImgBtnsInLookOrderPane();
        initTopicsFontSize();
    }

}
