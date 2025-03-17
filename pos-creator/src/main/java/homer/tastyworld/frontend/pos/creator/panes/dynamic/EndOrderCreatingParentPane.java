package homer.tastyworld.frontend.pos.creator.panes.dynamic;

import homer.tastyworld.frontend.pos.creator.POSCreatorApplication;
import homer.tastyworld.frontend.pos.creator.core.cache.AdditivesCache;
import homer.tastyworld.frontend.pos.creator.core.cache.ProductsCache;
import homer.tastyworld.frontend.starterpack.api.Request;
import homer.tastyworld.frontend.starterpack.base.utils.misc.TypeChanger;
import homer.tastyworld.frontend.starterpack.base.utils.ui.DialogWindow;
import homer.tastyworld.frontend.starterpack.base.utils.ui.helpers.AdaptiveTextHelper;
import homer.tastyworld.frontend.starterpack.base.utils.ui.helpers.PaneHelper;
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
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.apache.hc.core5.http.Method;
import java.util.Map;

@Getter
@SuperBuilder
public class EndOrderCreatingParentPane extends DynamicParentPane {

    private static final ScrollPane scroll = new ScrollPane();
    private static Label nameTopicLabel, priceTopicLabel;
    private AnchorPane endOrderCreatingOpenMenuImgBtn, endOrderCreatingCommitImgBtn;
    private AnchorPane endOrderCreatingNameTopic, endOrderCreatingTotalPriceTopic;
    private GridPane endOrderCreatingItemsContainer;
    private TextField endOrderCreatingDeliveryField;
    private CheckBox endOrderCreatingIsPaidCheckBox;

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
            endOrderCreatingDeliveryField.setText(deliveryAddress);
        }
        nameTopicLabel.setText("Заказ #" + orderInfo.get("NAME"));
        priceTopicLabel.setText("Стоимость: " + orderInfo.get("TOTAL_PRICE"));
        endOrderCreatingIsPaidCheckBox.setSelected(TypeChanger.toBool(orderInfo.get("IS_PAID")));
        scroll.setContent(computeItemsTable(TypeChanger.toSortedLongArray(orderInfo.get("ITEM_IDs"))));
    }

    public GridPane computeItemsTable(Long[] itemIDs) {
        GridPane table = new GridPane();
        table.setVgap(5);
        table.setAlignment(Pos.CENTER);
        ObservableList<Node> lines = table.getChildren();
        Request request = new Request("/order/read_item", Method.GET);
        for (int i = 0; i < itemIDs.length; i++) {
            request.putInBody("id", itemIDs[i]);
            table.add(getItemLine(request.request().getResultAsJSON(), lines), 0, i);
        }
        return table;
    }

    private HBox getItemLine(Map<String, Object> itemInfo, ObservableList<Node> lines) {
        HBox row = new HBox(7);
        row.setStyle("-fx-border-color: #000000;");
        row.prefWidthProperty().bind(scroll.widthProperty());
        row.prefHeightProperty().bind(scroll.heightProperty().divide(7));
        row.setAlignment(Pos.CENTER);

        AnchorPane delete = getDeleteItem(itemInfo, lines, row);
        AnchorPane name = getItemName(itemInfo);
        AnchorPane qty = getItemQTY(itemInfo);
        VBox additives = getAdditives(itemInfo);
        AnchorPane price = getItemPrice(itemInfo);
        AnchorPane space = new AnchorPane();

        row.getChildren().addAll(delete, name, qty, additives, price, space);
        HBox.setHgrow(delete, Priority.ALWAYS);
        HBox.setHgrow(name, Priority.ALWAYS);
        HBox.setHgrow(qty, Priority.ALWAYS);
        HBox.setHgrow(additives, Priority.ALWAYS);
        HBox.setHgrow(price, Priority.ALWAYS);
        HBox.setHgrow(space, Priority.ALWAYS);
        delete.prefWidthProperty().bind(row.widthProperty().multiply(0.1));
        name.prefWidthProperty().bind(row.widthProperty().multiply(0.35));
        qty.prefWidthProperty().bind(row.widthProperty().multiply(0.12));
        additives.prefWidthProperty().bind(row.widthProperty().multiply(0.29));
        price.prefWidthProperty().bind(row.widthProperty().multiply(0.12));
        space.prefWidthProperty().bind(row.widthProperty().multiply(0.02));

        return row;
    }

    private AnchorPane getDeleteItem(Map<String, Object> itemInfo, ObservableList<Node> deleteFrom, Node toDelete) {
        AnchorPane delete = new AnchorPane();
        PaneHelper.setImageBackgroundCentre(delete,
                                            "EndOrderCreatingPane/endOrderCreatingDeleteItemImgBtn",
                                            POSCreatorApplication.class.getResourceAsStream(
                                                    "images/buttons/EndOrderCreatingPane/endOrderCreatingDeleteItemImgBtn.png")
        );
        delete.setOnMouseClicked(event -> {
            Map<String, Object> productInfo = ProductsCache.impl.get(TypeChanger.toLong(itemInfo.get("PRODUCT_ID")));
            if (!DialogWindow.askBool("Да",
                                      "Нет",
                                      "Редактирование заказа",
                                      String.format(
                                              "Вы уверены что хотите удалить '%s' из заказа?",
                                              productInfo.get("NAME")
                                      ),
                                      "Продолжить?"
            )) {
                return;
            }
            Request deleteRequest = new Request("/order/remove_item", Method.POST);
            deleteRequest.putInBody("id", TypeChanger.toLong(itemInfo.get("ID")));
            deleteRequest.request();
            deleteFrom.remove(toDelete);
            Request infoRequest = new Request("/order/read", Method.GET);
            infoRequest.putInBody("id", TypeChanger.toLong(itemInfo.get("ORDER_ID")));
            endOrderCreatingTotalPriceTopic.getChildren().clear();
            priceTopicLabel.setText("Стоимость: " + infoRequest.request().getResultAsJSON().get("TOTAL_PRICE"));
        });
        return delete;
    }

    private AnchorPane getItemName(Map<String, Object> itemInfo) {
        AnchorPane name = new AnchorPane();
        Map<String, Object> productInfo = ProductsCache.impl.get(TypeChanger.toLong(itemInfo.get("PRODUCT_ID")));
        AdaptiveTextHelper.setTextCentre(name, (String) productInfo.get("NAME"), 15, null);
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
        AdaptiveTextHelper.setTextCentre(additiveLine, line, 17, null);
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
        endOrderCreatingDeliveryField.clear();
        endOrderCreatingIsPaidCheckBox.setSelected(false);
    }

    private void initItemsTable() {
        scroll.setFitToWidth(true);
        endOrderCreatingItemsContainer.add(scroll, 1, 2);
    }

    private void initTopics() {
        nameTopicLabel = AdaptiveTextHelper.setTextCentre(endOrderCreatingNameTopic, "", 6, null);
        priceTopicLabel = AdaptiveTextHelper.setTextCentre(endOrderCreatingTotalPriceTopic, "", 10, null);
    }

    private void initImgBtnsInEndOrderCreatingPane() {
        PaneHelper.setImageBackgroundBottom(endOrderCreatingOpenMenuImgBtn,
                                            POSCreatorApplication.class.getResourceAsStream(
                                                    "images/buttons/EndOrderCreatingPane/endOrderCreatingOpenMenuImgBtn.png")
        );
        PaneHelper.setImageBackgroundBottom(endOrderCreatingCommitImgBtn,
                                            POSCreatorApplication.class.getResourceAsStream(
                                                    "images/buttons/EndOrderCreatingPane/endOrderCreatingCommitImgBtn.png")
        );
    }

    @Override
    public void initialize() {
        initItemsTable();
        initImgBtnsInEndOrderCreatingPane();
        initTopics();
    }

}
