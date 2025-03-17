package homer.tastyworld.frontend.pos.creator.panes.dynamic;

import homer.tastyworld.frontend.pos.creator.POSCreatorApplication;
import homer.tastyworld.frontend.pos.creator.core.cache.MenuCache;
import homer.tastyworld.frontend.pos.creator.core.cache.ProductsCache;
import homer.tastyworld.frontend.starterpack.api.PhotoRequest;
import homer.tastyworld.frontend.starterpack.base.utils.managers.cache.CacheProcessor;
import homer.tastyworld.frontend.starterpack.base.utils.misc.TypeChanger;
import homer.tastyworld.frontend.starterpack.base.utils.ui.helpers.AdaptiveTextHelper;
import homer.tastyworld.frontend.starterpack.base.utils.ui.helpers.PaneHelper;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import java.util.Arrays;
import java.util.Map;

@Getter
@SuperBuilder
public class ProductsParentPane extends DynamicParentPane {

    private AnchorPane productsPaneBackInMenuImgBtn;
    private AnchorPane productsPaneMenuTopic;
    private GridPane productPaneImgProductsContainer;
    private DynamicParentPane addProductParentPane;
    private static Label nameTopicLabel;
    private static final ScrollPane scroll = new ScrollPane();
    private final CacheProcessor<Long, GridPane> productsInMenuCache = new CacheProcessor<>() {
        @Override protected GridPane compute(Long menuID) { return computeTable(TypeChanger.toSortedLongArray(MenuCache.impl.get(menuID).get("PRODUCT_IDs"))); }
    };

    @Override
    protected String getCacheProcess(int total, int actual) {
        return String.format("Getting menu (%s/%s)", actual, total);
    }

    @Override
    protected void cacheTask(Long menuID) {
        productsInMenuCache.cacheIfAbsent(menuID);
    }

    @Override
    public void cacheAll(Long[] menuIDs) {
        Arrays.stream(menuIDs).forEach(
                menuID -> addProductParentPane.cacheAll(TypeChanger.toSortedLongArray(
                        MenuCache.impl.get(menuID).get("PRODUCT_IDs")
                ))
        );
        super.cacheAll(menuIDs);
    }

    @Override
    public void fill(long menuID) {
        nameTopicLabel.setText((String) MenuCache.impl.get(menuID).get("NAME"));
        scroll.setContent(productsInMenuCache.get(menuID));
    }

    private GridPane computeTable(Long[] productIDs) {
        GridPane table = new GridPane();
        table.setHgap(25);
        table.setVgap(25);
        table.setAlignment(Pos.CENTER);
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
        Map<String, Object> productInfo = ProductsCache.impl.get(productID);
        AdaptiveTextHelper.setTextCentre(result, (String) productInfo.get("NAME"), 12, null);
        return result;
    }

    @Override
    protected void cleanTask() {
        productsPaneMenuTopic.getChildren().clear();
    }

    private void initNameTopic() {
        nameTopicLabel = AdaptiveTextHelper.setTextCentre(productsPaneMenuTopic, "", 20, null);
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
        initNameTopic();
        initImgBtnsInProductsPane();
        initItemsTable();
    }

}
