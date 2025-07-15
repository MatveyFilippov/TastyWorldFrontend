package homer.tastyworld.frontend.pos.creator.panes.dynamic;

import homer.tastyworld.frontend.pos.creator.POSCreatorApplication;
import homer.tastyworld.frontend.starterpack.entity.Menu;
import homer.tastyworld.frontend.starterpack.entity.Product;
import homer.tastyworld.frontend.starterpack.base.utils.managers.cache.CacheManager;
import homer.tastyworld.frontend.starterpack.base.utils.managers.cache.CacheProcessor;
import homer.tastyworld.frontend.starterpack.base.utils.ui.helpers.AdaptiveTextHelper;
import homer.tastyworld.frontend.starterpack.base.utils.ui.helpers.PaneHelper;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import java.util.Arrays;

@SuperBuilder
public class ProductsParentPane extends DynamicParentPane {

    private final AnchorPane goBackInMenuImgBtn;
    private final AnchorPane paneNameTopic;
    private final GridPane productsImgBtnContainer;
    private final DynamicParentPane addProductToOrderParentPane;
    private final ScrollPane scroll = new ScrollPane();
    @Getter(lazy = true, value = AccessLevel.PRIVATE)
    private final Label menuNameTopicLabel = AdaptiveTextHelper.setTextCentre(paneNameTopic, "", 15, Color.web("#555555"));;
    private final CacheProcessor<Long, GridPane> productsInMenuCache = CacheManager.register(
            (menuID) -> computeTable(Menu.get(menuID).getProductIDs())
    );

    @Override
    protected void cacheTask(long menuID) {
        productsInMenuCache.cacheIfAbsent(menuID);
    }

    @Override
    public void fill(long menuID) {
        getMenuNameTopicLabel().setText(Menu.get(menuID).getName());
        scroll.setContent(productsInMenuCache.get(menuID));
    }

    private GridPane computeTable(long[] productIDs) {
        GridPane table = new GridPane();
        table.setHgap(25);
        table.setVgap(25);
        table.setAlignment(Pos.CENTER);
        long[] activeProductIDs = Arrays.stream(productIDs)
                                        .mapToObj(Product::get)
                                        .filter(Product::isActive)
                                        .mapToLong(Product::getId)
                                        .toArray();
        for (int i = 0; i < activeProductIDs.length; i++) {
            table.add(getProductImgBtn(activeProductIDs[i]), i % 3, i / 3);
        }
        return table;
    }

    private VBox getProductImgBtn(long productID) {
        VBox result = new VBox();
        result.setFillWidth(true);
        result.prefWidthProperty().bind(scroll.widthProperty());
        result.prefHeightProperty().bind(scroll.heightProperty().divide(2));

        PaneHelper.setOnMouseClickedWithPressingCountChecking(result, 2, event -> {
            addProductToOrderParentPane.fill(productID);
            addProductToOrderParentPane.openAndCloseFrom(current);
            clean();
        });

        Product product = Product.get(productID);

        AnchorPane topPaneWithImage = new AnchorPane();
        PaneHelper.setImageBackgroundBottom(topPaneWithImage, product.getPhoto());

        AnchorPane bottomPaneWithName = new AnchorPane();
        AdaptiveTextHelper.setTextCentre(bottomPaneWithName, product.getName(), 12, null);

        result.getChildren().addAll(topPaneWithImage, bottomPaneWithName);
        VBox.setVgrow(topPaneWithImage, javafx.scene.layout.Priority.ALWAYS);
        VBox.setVgrow(bottomPaneWithName, javafx.scene.layout.Priority.ALWAYS);
        topPaneWithImage.prefHeightProperty().bind(result.heightProperty().multiply(0.90));
        bottomPaneWithName.prefHeightProperty().bind(result.heightProperty().multiply(0.10));

        return result;
    }

    @Override
    protected void cleanTask() {
        scroll.setVvalue(0.0);
        getMenuNameTopicLabel().setText("");
    }

    private void initItemsTable() {
        scroll.setFitToWidth(true);
        productsImgBtnContainer.add(scroll, 1, 0);
    }

    private void initImgBtnsInProductsPane() {
        PaneHelper.setImageBackgroundCentre(
                goBackInMenuImgBtn,
                POSCreatorApplication.class.getResourceAsStream("images/buttons/ProductsPane/BackInMenu.png")
        );
    }

    @Override
    public void initialize() {
        initImgBtnsInProductsPane();
        initItemsTable();
    }

}
