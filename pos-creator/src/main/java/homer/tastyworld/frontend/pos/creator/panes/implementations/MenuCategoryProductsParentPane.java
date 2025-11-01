package homer.tastyworld.frontend.pos.creator.panes.implementations;

import homer.tastyworld.frontend.pos.creator.POSCreatorApplication;
import homer.tastyworld.frontend.pos.creator.panes.ParentPane;
import homer.tastyworld.frontend.starterpack.api.sra.entity.menu.MenuCategory;
import homer.tastyworld.frontend.starterpack.api.sra.entity.menu.MenuUtils;
import homer.tastyworld.frontend.starterpack.api.sra.entity.menu.Product;
import homer.tastyworld.frontend.starterpack.utils.managers.cache.CacheManager;
import homer.tastyworld.frontend.starterpack.utils.managers.cache.CacheableFunction;
import homer.tastyworld.frontend.starterpack.utils.ui.helpers.AdaptiveTextHelper;
import homer.tastyworld.frontend.starterpack.utils.ui.helpers.PaneHelper;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import lombok.experimental.SuperBuilder;

@SuperBuilder
public class MenuCategoryProductsParentPane extends ParentPane<MenuCategory> {

    public record CategoryProduct(MenuCategory category, Product product) {}

    private final AnchorPane goBackInMenuCategoriesImgBtn;
    private final AnchorPane nameTopic;
    private final ScrollPane iconsScroll;
    private final ParentPane<CategoryProduct> productPane;

    private Label nameLabel;
    private CacheableFunction<MenuCategory, GridPane> cacheableGetClickableIconsTable;

    private GridPane getClickableIconsTable(MenuCategory category) {
        GridPane clickableIconsTable = new GridPane();
        clickableIconsTable.setHgap(10);
        clickableIconsTable.setVgap(10);
        clickableIconsTable.setAlignment(Pos.CENTER);

        Product[] activeProducts = MenuUtils.getAllCategoryProducts(category.getCategoryID(), true);
        for (int i = 0; i < activeProducts.length; i++) {
            clickableIconsTable.add(getProductClickableIconsTable(category, activeProducts[i]), i % 3, i / 3);
        }

        return clickableIconsTable;
    }

    private VBox getProductClickableIconsTable(MenuCategory category, Product product) {
        VBox icon = new VBox();
        icon.getStyleClass().add("menu-icon");
        icon.setFillWidth(true);
        icon.prefWidthProperty().bind(iconsScroll.widthProperty());
        icon.prefHeightProperty().bind(iconsScroll.heightProperty().multiply(0.5));

        CategoryProduct categoryProduct = new CategoryProduct(category, product);
        PaneHelper.setOnMouseClickedWithPressingCountChecking(
                icon, 2, e -> productPane.openAndCloseFrom(this, categoryProduct)
        );

        AnchorPane topWithImage = new AnchorPane();
        PaneHelper.setImageBackgroundBottom(topWithImage, product.getImage());

        AnchorPane bottomWithName = new AnchorPane();
        AdaptiveTextHelper.setTextCentre(bottomWithName, product.getName(), 0.085, null);

        icon.getChildren().addAll(topWithImage, bottomWithName);
        VBox.setVgrow(topWithImage, javafx.scene.layout.Priority.ALWAYS);
        VBox.setVgrow(bottomWithName, javafx.scene.layout.Priority.ALWAYS);
        topWithImage.prefHeightProperty().bind(icon.heightProperty().multiply(0.9));
        bottomWithName.prefHeightProperty().bind(icon.heightProperty().multiply(0.1));

        return icon;
    }

    private void initTopicsLabel() {
        nameLabel = AdaptiveTextHelper.setTextCentre(nameTopic, "", 0.065, Color.web("#555555"));
    }

    private void initCacheableGetClickableIconsTable() {
        cacheableGetClickableIconsTable = CacheManager.getForFunction(this::getClickableIconsTable);

        System.out.print("Downloading products");
        MenuCategory[] activeCategories = MenuUtils.getAllCategories(true);
        for (int i = 0; i < activeCategories.length; i++) {
            cacheableGetClickableIconsTable.cache(activeCategories[i]);
            if ((i + 1) % 4 == 0) {
                System.out.print("\b\b\b");
            } else {
                System.out.print(".");
            }
        }
        System.out.print("\n");
    }

    private void initImgBtns() {
        PaneHelper.setImageBackgroundCentre(
                goBackInMenuCategoriesImgBtn,
                POSCreatorApplication.class.getResourceAsStream("images/buttons/MenuCategoryProductsPane/BackInMenuCategories.png")
        );
    }

    @Override
    public void initialize() {
        initImgBtns();
        initTopicsLabel();
        initCacheableGetClickableIconsTable();
    }

    @Override
    protected void beforeOpen(MenuCategory category) {
        nameLabel.setText(category.getName());
        iconsScroll.setContent(cacheableGetClickableIconsTable.applyWithCache(category));
    }

    @Override
    protected void beforeClose() {
        iconsScroll.setVvalue(0.0);
        iconsScroll.setContent(null);
        nameLabel.setText("");
    }

}
