package homer.tastyworld.frontend.poscreator.panes.dynamic;

import homer.tastyworld.frontend.poscreator.POSCreatorApplication;
import homer.tastyworld.frontend.starterpack.api.PhotoRequest;
import homer.tastyworld.frontend.starterpack.api.Request;
import homer.tastyworld.frontend.starterpack.base.utils.misc.TypeChanger;
import homer.tastyworld.frontend.starterpack.base.utils.ui.helpers.Helper;
import homer.tastyworld.frontend.starterpack.base.utils.ui.helpers.Text;
import javafx.beans.binding.StringExpression;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.apache.hc.core5.http.Method;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@SuperBuilder
public class ProductsParentPane extends DynamicParentPane {

    private AnchorPane productsPaneBackInMenuImgBtn;
    private AnchorPane productsPaneMenuTopic;
    private GridPane productPaneImgProductsContainer;
    private static GridPane clickableItemsTable = new GridPane();
    private static ScrollPane scroll = new ScrollPane(clickableItemsTable);
    private static StringExpression topicFontSize;
    private static final Map<Long, VBox> productsCache = new ConcurrentHashMap<>();

    @Override
    public void fill(long menuID) {
        Request request = new Request("/menu/read", Method.GET);
        request.putInBody("id", menuID);
        Map<String, Object> response = request.request().getResultAsJSON();
        Text.setTextCentre(productsPaneMenuTopic, (String) response.get("NAME"), topicFontSize, null);
        Long[] productIDs = TypeChanger.toSortedLongArray(response.get("PRODUCT_IDs"));
        for (int i = 0; i < productIDs.length; i++) {
            clickableItemsTable.add(getProductImgBtn(productIDs[i]), i % 3, i / 3);
        }
    }

    @Override
    public void cacheAll(Long[] menuIDs) {
        Request request = new Request("/menu/read", Method.GET);
        for (long menuID : menuIDs) {
            request.putInBody("id", menuID);
            Map<String, Object> response = request.request().getResultAsJSON();
            for (long productID : TypeChanger.toSortedLongArray(response.get("PRODUCT_IDs"))) {
                productsCache.put(productID, createProductImgBtn(productID));
            }
        }
    }

    private VBox getProductImgBtn(long productID) {
        return productsCache.computeIfAbsent(productID, this::createProductImgBtn);
    }

    private VBox createProductImgBtn(long productID) {
        VBox root = new VBox();
        root.setFillWidth(true);
        root.prefWidthProperty().bind(scroll.widthProperty());
        root.prefHeightProperty().bind(scroll.heightProperty().divide(2));

        root.setOnMouseClicked(event -> {  // TODO...
            System.out.println(productID);
        });

        AnchorPane topPaneWithImage = getProductImage(productID);
        AnchorPane bottomPaneWithName = getProductName(productID);
        root.getChildren().addAll(topPaneWithImage, bottomPaneWithName);
        VBox.setVgrow(topPaneWithImage, javafx.scene.layout.Priority.ALWAYS);
        VBox.setVgrow(bottomPaneWithName, javafx.scene.layout.Priority.ALWAYS);
        topPaneWithImage.prefHeightProperty().bind(root.heightProperty().multiply(0.90));
        bottomPaneWithName.prefHeightProperty().bind(root.heightProperty().multiply(0.10));

        return root;
    }

    private AnchorPane getProductImage(long productID) {
        AnchorPane result = new AnchorPane();
        PhotoRequest request = new PhotoRequest("/product/get_photo");
        request.putInBody("id", productID);
        Helper.setAnchorPaneImageBackgroundBottom(result, request.read());
        return result;
    }

    private AnchorPane getProductName(long productID) {
        AnchorPane result = new AnchorPane();
        Request request = new Request("/product/read", Method.GET);
        request.putInBody("id", productID);
        Map<String, Object> info = request.request().getResultAsJSON();
        Text.setTextCentre(result, (String) info.get("NAME"), Text.getAdaptiveFontSize(result, 12), null);
        return result;
    }

    @Override
    public void clean() {
        productsPaneMenuTopic.getChildren().clear();
        clickableItemsTable.getChildren().clear();
    }

    private void initTopicFontSize() {
        topicFontSize = Text.getAdaptiveFontSize(productsPaneMenuTopic, 20);
    }

    private void initItemsTable() {
        clickableItemsTable.setHgap(25);
        clickableItemsTable.setVgap(25);
        clickableItemsTable.setAlignment(Pos.CENTER);
        scroll.setFitToWidth(true);
        productPaneImgProductsContainer.add(scroll, 1, 0);
    }

    private void initImgBtnsInProductsPane() {
        Helper.setAnchorPaneImageBackgroundCentre(
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
