package homer.tastyworld.frontend.poscreator.panes.dynamic;

import homer.tastyworld.frontend.poscreator.POSCreatorApplication;
import homer.tastyworld.frontend.starterpack.api.Request;
import homer.tastyworld.frontend.starterpack.base.utils.misc.TypeChanger;
import homer.tastyworld.frontend.starterpack.base.utils.ui.DialogWindow;
import homer.tastyworld.frontend.starterpack.base.utils.ui.helpers.Helper;
import homer.tastyworld.frontend.starterpack.base.utils.ui.helpers.Text;
import javafx.beans.binding.StringExpression;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
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
public class EndOrderCreatingParentPane extends DynamicParentPane {

    private AnchorPane endOrderCreatingOpenMenuImgBtn, endOrderCreatingCommitImgBtn;
    private AnchorPane endOrderCreatingNameTopic, endOrderCreatingTotalPriceTopic;
    private GridPane endOrderCreatingItemsContainer;
    private TextField endOrderCreatingDeliveryField;
    private CheckBox endOrderCreatingIsPaidCheckBox;
    private static StringExpression nameTopicFontSize, priceTopicFontSize;
    private static final ScrollPane scroll = new ScrollPane();
    private static final Map<Long, Map<String, Object>> productCache = new ConcurrentHashMap<>();

    @Override
    public void cacheAll(Long[] ignored) {}

    @Override
    public void fill(long orderID) {
        Request request = new Request("/order/read", Method.GET);
        request.putInBody("id", orderID);
        Map<String, Object> orderInfo = request.request().getResultAsJSON();
        String deliveryAddress = (String) orderInfo.get("DELIVERY_ADDRESS");
        if (!deliveryAddress.equals("NOT FOR DELIVERY")) {
            endOrderCreatingDeliveryField.setText(deliveryAddress);
        }
        Text.setTextCentre(endOrderCreatingNameTopic, "Заказ #" + orderInfo.get("NAME"), nameTopicFontSize, null);
        Text.setTextCentre(endOrderCreatingTotalPriceTopic, "Стоимость: " + orderInfo.get("TOTAL_PRICE"), priceTopicFontSize, null);
        endOrderCreatingIsPaidCheckBox.setSelected(TypeChanger.toBool(orderInfo.get("IS_PAID")));
        scroll.setContent(computeItemsTable(TypeChanger.toSortedLongArray(orderInfo.get("ITEM_IDs"))));
    }

    public GridPane computeItemsTable(Long[] itemIDs) {
        GridPane table = new GridPane();
        table.setVgap(5);
        table.setAlignment(Pos.CENTER);
        Request request = new Request("/order/read_item", Method.GET);
        for (int i = 0; i < itemIDs.length; i++) {
            request.putInBody("id", itemIDs[i]);
            table.add(getItemLine(request.request().getResultAsJSON(), table.getChildren()), 0, i);
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
        name.prefWidthProperty().bind(row.widthProperty().multiply(0.4));
        qty.prefWidthProperty().bind(row.widthProperty().multiply(0.09));
        additives.prefWidthProperty().bind(row.widthProperty().multiply(0.30));
        price.prefWidthProperty().bind(row.widthProperty().multiply(0.09));
        space.prefWidthProperty().bind(row.widthProperty().multiply(0.02));

        return row;
    }

    private AnchorPane getDeleteItem(Map<String, Object> itemInfo, ObservableList<Node> deleteFrom, Node toDelete) {
        AnchorPane delete = new AnchorPane();
        Helper.setAnchorPaneImageBackgroundCentre(delete, POSCreatorApplication.class.getResourceAsStream("images/buttons/EndOrderCreatingPane/endOrderCreatingDeleteItemImgBtn.png"));
        delete.setOnMouseClicked(event -> {
            Map<String, Object> productInfo = getProductInfo(TypeChanger.toLong(itemInfo.get("PRODUCT_ID")));
            if (!DialogWindow.askBool(
                    "Да", "Нет", "Редактирование заказа",
                    String.format("Вы уверены что хотите удалить '%s' из заказа?", productInfo.get("NAME")),
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
            Text.setTextCentre(
                    endOrderCreatingTotalPriceTopic, "Стоимость: " + infoRequest.request().getResultAsJSON().get("TOTAL_PRICE"), priceTopicFontSize, null
            );
        });
        return delete;
    }

    private AnchorPane getItemName(Map<String, Object> itemInfo) {
        AnchorPane name = new AnchorPane();
        Map<String, Object> productInfo = getProductInfo(TypeChanger.toLong(itemInfo.get("PRODUCT_ID")));
        Text.setTextCentre(name, (String) productInfo.get("NAME"), Text.getAdaptiveFontSize(name, 15), null);
        return name;
    }

    private AnchorPane getItemQTY(Map<String, Object> itemInfo) {
        AnchorPane qty = new AnchorPane();
        Map<String, Object> productInfo = getProductInfo(TypeChanger.toLong(itemInfo.get("PRODUCT_ID")));
        String peaceType;
        if (productInfo.get("PIECE_TYPE").equals("ONE_HUNDRED_GRAMS")) {
            peaceType = "Гр";
        } else {
            peaceType = "Шт";
        }
        Text.setTextCentre(qty, itemInfo.get("PEACE_QTY") + " " + peaceType, Text.getAdaptiveFontSize(qty, 4), null);
        return qty;
    }

    private VBox getAdditives(Map<String, Object> itemInfo) {
        return new VBox();  // TODO: (also cache)
    }

    private AnchorPane getItemPrice(Map<String, Object> itemInfo) {
        AnchorPane price = new AnchorPane();
        Text.setTextCentre(price, TypeChanger.toBigDecimal(itemInfo.get(("ITEM_PRICE"))).toString(), Text.getAdaptiveFontSize(price, 4), null);
        return price;
    }

    @Override
    public void clean() {
        endOrderCreatingNameTopic.getChildren().clear();
        endOrderCreatingTotalPriceTopic.getChildren().clear();
        endOrderCreatingDeliveryField.clear();
        endOrderCreatingIsPaidCheckBox.setSelected(false);
    }

    private void initItemsTable() {
        scroll.setFitToWidth(true);
        endOrderCreatingItemsContainer.add(scroll, 1, 1);
    }

    private void initTopicsFontSize() {
        nameTopicFontSize = Text.getAdaptiveFontSize(endOrderCreatingNameTopic, 6);
        priceTopicFontSize = Text.getAdaptiveFontSize(endOrderCreatingTotalPriceTopic, 10);
    }

    private void initImgBtnsInEndOrderCreatingPane() {
        Helper.setAnchorPaneImageBackgroundCentre(
                endOrderCreatingOpenMenuImgBtn,
                POSCreatorApplication.class.getResourceAsStream("images/buttons/EndOrderCreatingPane/endOrderCreatingOpenMenuImgBtn.png")
        );
        Helper.setAnchorPaneImageBackgroundCentre(
                endOrderCreatingCommitImgBtn,
                POSCreatorApplication.class.getResourceAsStream("images/buttons/EndOrderCreatingPane/endOrderCreatingCommitImgBtn.png")
        );
    }

    @Override
    public void initialize() {
        initItemsTable();
        initImgBtnsInEndOrderCreatingPane();
        initTopicsFontSize();
    }

}
