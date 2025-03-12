package homer.tastyworld.frontend.pos.creator.panes.dynamic;

import homer.tastyworld.frontend.pos.creator.POSCreatorApplication;
import homer.tastyworld.frontend.starterpack.api.PhotoRequest;
import homer.tastyworld.frontend.starterpack.api.Request;
import homer.tastyworld.frontend.starterpack.base.utils.misc.TypeChanger;
import homer.tastyworld.frontend.starterpack.base.utils.ui.helpers.PaneHelper;
import homer.tastyworld.frontend.starterpack.base.utils.ui.helpers.TextHelper;
import javafx.beans.binding.StringExpression;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.apache.hc.core5.http.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@SuperBuilder
public class ProductsParentPane extends DynamicParentPane {

    private AnchorPane productsPaneBackInMenuImgBtn;
    private AnchorPane productsPaneMenuTopic;
    private GridPane productPaneImgProductsContainer;
    private DynamicParentPane addProductParentPane;
    private static StringExpression topicFontSize;
    private static final ScrollPane scroll = new ScrollPane();
    private static final Map<Long, GridPane> productsCache = new ConcurrentHashMap<>();

    @Override
    protected String getCacheProcess(int total, int actual) {
        return String.format("Getting menu (%s/%s)", actual, total);
    }

    @Override
    protected void cacheTask(Long menuID) {
        Request request = new Request("/menu/read", Method.GET);
        request.putInBody("id", menuID);
        Map<String, Object> menuInfo = request.request().getResultAsJSON();
        productsCache.put(menuID, computeTable(menuInfo));
        addProductParentPane.cacheAll(TypeChanger.toSortedLongArray(menuInfo.get("PRODUCT_IDs")));
    }

    @Override
    public void fill(long menuID) {
        Request request = new Request("/menu/read", Method.GET);
        request.putInBody("id", menuID);
        Map<String, Object> menuInfo = request.request().getResultAsJSON();
        TextHelper.setTextCentre(productsPaneMenuTopic, (String) menuInfo.get("NAME"), topicFontSize, null);
        scroll.setContent(productsCache.computeIfAbsent(menuID, ignored -> computeTable(menuInfo)));
    }

    private GridPane computeTable(Map<String, Object> menuInfo) {
        GridPane table = new GridPane();
        table.setHgap(25);
        table.setVgap(25);
        table.setAlignment(Pos.CENTER);
        Long[] productIDs = TypeChanger.toSortedLongArray(menuInfo.get("PRODUCT_IDs"));
        for (int i = 0; i < productIDs.length; i++) {
            table.add(getProductImgBtn(productIDs[i]), i % 3, i / 3);
        }
        return table;
    }

    private VBox getProductImgBtn(long productID) {
        VBox product = new VBox();
        product.setFillWidth(true);
        product.prefWidthProperty().bind(scroll.widthProperty());
        product.prefHeightProperty().bind(scroll.heightProperty().divide(2));

        PaneHelper.setOnMouseClickedWithPressingCountChecking(product, 2, event -> {
            addProductParentPane.fill(productID);
            addProductParentPane.openAndCloseFrom(parent);
            clean();
        });

        AnchorPane topPaneWithImage = getProductImage(productID);
        AnchorPane bottomPaneWithName = getProductName(productID);
        product.getChildren().addAll(topPaneWithImage, bottomPaneWithName);
        VBox.setVgrow(topPaneWithImage, javafx.scene.layout.Priority.ALWAYS);
        VBox.setVgrow(bottomPaneWithName, javafx.scene.layout.Priority.ALWAYS);
        topPaneWithImage.prefHeightProperty().bind(product.heightProperty().multiply(0.90));
        bottomPaneWithName.prefHeightProperty().bind(product.heightProperty().multiply(0.10));

        return product;
    }

    private AnchorPane getProductImage(long productID) {
        AnchorPane result = new AnchorPane();
        PhotoRequest request = new PhotoRequest("/product/get_photo");
        request.putInBody("id", productID);
        PaneHelper.setImageBackgroundBottom(result, request.read());
        return result;
    }

    private AnchorPane getProductName(long productID) {
        AnchorPane result = new AnchorPane();
        Request request = new Request("/product/read", Method.GET);
        request.putInBody("id", productID);
        Map<String, Object> info = request.request().getResultAsJSON();
        TextHelper.setTextCentre(result, (String) info.get("NAME"), TextHelper.getAdaptiveFontSize(result, 12), null);
        return result;
    }

    @Override
    protected void cleanTask() {
        productsPaneMenuTopic.getChildren().clear();
    }

    private void initTopicFontSize() {
        topicFontSize = TextHelper.getAdaptiveFontSize(productsPaneMenuTopic, 20);
    }

    private void initItemsTable() {
        scroll.setFitToWidth(true);
        productPaneImgProductsContainer.add(scroll, 1, 0);
    }

    private void initImgBtnsInProductsPane() {
        PaneHelper.setImageBackgroundCentre(
                productsPaneBackInMenuImgBtn,
                POSCreatorApplication.class.getResourceAsStream("images/buttons/ProductsPane/productsPaneBackInMenuImgBtn.png")
        );
    }

    @Override
    public void initialize() {
        initTopicFontSize();
        initImgBtnsInProductsPane();
        initItemsTable();
    }

}
