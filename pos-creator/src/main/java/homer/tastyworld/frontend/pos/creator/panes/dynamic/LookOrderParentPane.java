package homer.tastyworld.frontend.pos.creator.panes.dynamic;

import homer.tastyworld.frontend.pos.creator.POSCreatorApplication;
import homer.tastyworld.frontend.pos.creator.core.cache.AdditivesCache;
import homer.tastyworld.frontend.pos.creator.core.cache.ProductsCache;
import homer.tastyworld.frontend.starterpack.api.Request;
import homer.tastyworld.frontend.starterpack.base.utils.misc.TypeChanger;
import homer.tastyworld.frontend.starterpack.base.utils.ui.helpers.AdaptiveTextHelper;
import homer.tastyworld.frontend.starterpack.base.utils.ui.helpers.PaneHelper;
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
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.apache.hc.core5.http.Method;
import java.util.Map;

@Getter
@SuperBuilder
public class LookOrderParentPane extends DynamicParentPane {

    private static final ScrollPane scroll = new ScrollPane();
    private static Label nameTopicLabel, priceTopicLabel;
    private AnchorPane lookOrderNameTopic, lookOrderTotalPriceTopic;
    private AnchorPane lookOrderClosePaneImgBtn, lookOrderSetDoneImgBtn;
    private GridPane lookOrderItemsContainer;
    private TextField lookOrderDeliveryField;
    private Button lookOrderSetPaidBtn;

    @Override
    protected String getCacheProcess(int ignored1, int ignored2) {return "";}

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
        nameTopicLabel.setText("Заказ #" + orderInfo.get("NAME"));
        priceTopicLabel.setText("Стоимость: " + orderInfo.get("TOTAL_PRICE"));
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
        Map<String, Object> productInfo = ProductsCache.impl.get(TypeChanger.toLong(itemInfo.get("PRODUCT_ID")));
        AdaptiveTextHelper.setTextCentre(name, (String) productInfo.get("NAME"), 13, null);
        return name;
    }

    private AnchorPane getItemQTY(Map<String, Object> itemInfo) {
        AnchorPane qty = new AnchorPane();
        Map<String, Object> productInfo = ProductsCache.impl.get(TypeChanger.toLong(itemInfo.get("PRODUCT_ID")));
        String peaceType = productInfo.get("PIECE_TYPE").equals("ONE_HUNDRED_GRAMS") ? "Гр" : "Шт";
        AdaptiveTextHelper.setTextCentre(qty, itemInfo.get("PEACE_QTY") + " " + peaceType, 4, null);
        return qty;
    }

    private VBox getAdditives(Map<String, Object> itemInfo) {
        VBox result = new VBox();
        result.setFillWidth(true);
        result.setAlignment(Pos.CENTER);
        Map<Long, Integer> notDefaultAdditives = TypeChanger.toMap(
                itemInfo.get("NOT_DEFAULT_ADDITIVES"),
                Long.class,
                Integer.class
        );
        for (Map.Entry<Long, Integer> pair : notDefaultAdditives.entrySet()) {
            result.getChildren().add(getAdditiveLine(AdditivesCache.impl.get(pair.getKey()), pair.getValue()));
        }
        return result;
    }

    private AnchorPane getAdditiveLine(Map<String, Object> additiveInfo, int qty) {
        AnchorPane additiveLine = new AnchorPane();
        additiveLine.setStyle("-fx-border-color: #000000;");
        String line = additiveInfo.get("NAME") + " " + qty + (
                additiveInfo.get("PIECE_TYPE").equals("ONE_HUNDRED_GRAMS") ? " Гр" : " Шт"
        );
        AdaptiveTextHelper.setTextCentre(additiveLine, line, 15, null);
        return additiveLine;
    }

    private AnchorPane getItemPrice(Map<String, Object> itemInfo) {
        AnchorPane price = new AnchorPane();
        AdaptiveTextHelper.setTextCentre(
                price,
                TypeChanger.toBigDecimal(itemInfo.get(("ITEM_PRICE"))).toString(),
                4,
                null
        );
        return price;
    }

    @Override
    protected void cleanTask() {
        nameTopicLabel.setText("");
        priceTopicLabel.setText("");
        lookOrderDeliveryField.clear();
        lookOrderSetPaidBtn.setDisable(true);
    }

    private void initItemsTable() {
        scroll.setFitToWidth(true);
        lookOrderItemsContainer.add(scroll, 1, 1);
    }

    private void initTopics() {
        nameTopicLabel = AdaptiveTextHelper.setTextCentre(lookOrderNameTopic, "", 6, null);
        priceTopicLabel = AdaptiveTextHelper.setTextCentre(lookOrderTotalPriceTopic, "", 10, null);
    }

    private void initImgBtnsInLookOrderPane() {
        PaneHelper.setImageBackgroundCentre(lookOrderClosePaneImgBtn,
                                            POSCreatorApplication.class.getResourceAsStream(
                                                    "images/buttons/LookOrderPane/lookOrderClosePaneImgBtn.png")
        );
        PaneHelper.setImageBackgroundCentre(lookOrderSetDoneImgBtn,
                                            POSCreatorApplication.class.getResourceAsStream(
                                                    "images/buttons/LookOrderPane/lookOrderSetDoneImgBtn.png")
        );
    }

    @Override
    public void initialize() {
        initItemsTable();
        initImgBtnsInLookOrderPane();
        initTopics();
    }

}
