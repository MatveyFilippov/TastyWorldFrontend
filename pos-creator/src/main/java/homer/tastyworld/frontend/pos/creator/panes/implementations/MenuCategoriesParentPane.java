package homer.tastyworld.frontend.pos.creator.panes.implementations;

import homer.tastyworld.frontend.pos.creator.POSCreatorApplication;
import homer.tastyworld.frontend.pos.creator.panes.ParentPane;
import homer.tastyworld.frontend.starterpack.api.sra.entity.menu.MenuCategory;
import homer.tastyworld.frontend.starterpack.api.sra.entity.menu.MenuUtils;
import homer.tastyworld.frontend.starterpack.utils.ui.helpers.AdaptiveTextHelper;
import homer.tastyworld.frontend.starterpack.utils.ui.helpers.PaneHelper;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import lombok.experimental.SuperBuilder;

@SuperBuilder
public class MenuCategoriesParentPane extends ParentPane<Void> {

    private final AnchorPane deleteCreatingOrderImgBtn, lookCreatingOrderImgBtn;
    private final AnchorPane nameTopic;
    private final ScrollPane iconsScroll;
    private final ParentPane<MenuCategory> menuCategoryProductsPane;

    private void initClickableIconsTable() {  // TODO: init in thread
        GridPane clickableIconsTable = new GridPane();
        clickableIconsTable.setHgap(10);
        clickableIconsTable.setVgap(10);
        clickableIconsTable.setAlignment(Pos.CENTER);

        System.out.print("Downloading product categories");
        MenuCategory[] activeCategories = MenuUtils.getAllCategories(true);
        for (int i = 0; i < activeCategories.length; i++) {
            clickableIconsTable.add(getMenuCategoryClickableIcon(activeCategories[i]), i % 3, i / 3);
            if ((i + 1) % 4 == 0) {
                System.out.print("\b\b\b");
            } else {
                System.out.print(".");
            }
        }
        System.out.print("\n");

        iconsScroll.setContent(clickableIconsTable);
    }

    private VBox getMenuCategoryClickableIcon(MenuCategory category) {
        VBox icon = new VBox();
        icon.getStyleClass().add("menu-icon");
        icon.setFillWidth(true);
        icon.prefWidthProperty().bind(iconsScroll.widthProperty());
        icon.prefHeightProperty().bind(iconsScroll.heightProperty().multiply(0.5));

        PaneHelper.setOnMouseClickedWithPressingCountChecking(
                icon, 2, e -> menuCategoryProductsPane.openAndCloseFrom(this, category)
        );

        AnchorPane topWithImage = new AnchorPane();
        PaneHelper.setImageBackgroundCentre(topWithImage, category.getImage());

        AnchorPane bottomWithName = new AnchorPane();
        AdaptiveTextHelper.setTextCentre(bottomWithName, category.getName(), 0.085, null);

        icon.getChildren().addAll(topWithImage, bottomWithName);
        VBox.setVgrow(topWithImage, Priority.ALWAYS);
        VBox.setVgrow(bottomWithName, Priority.ALWAYS);
        topWithImage.prefHeightProperty().bind(icon.heightProperty().multiply(0.9));
        bottomWithName.prefHeightProperty().bind(icon.heightProperty().multiply(0.1));

        return icon;
    }

    private void initNameTopic() {
        AdaptiveTextHelper.setTextCentre(nameTopic, "Категории меню", 0.065, Color.web("#555555"));
    }

    private void initImgBtns() {
        PaneHelper.setImageBackgroundCentre(
                deleteCreatingOrderImgBtn,
                POSCreatorApplication.class.getResourceAsStream("images/buttons/MenuCategoriesPane/DeleteOrder.png")
        );
        PaneHelper.setImageBackgroundCentre(
                lookCreatingOrderImgBtn,
                POSCreatorApplication.class.getResourceAsStream("images/buttons/MenuCategoriesPane/LookOrder.png")
        );
    }

    @Override
    public void initialize() {
        initClickableIconsTable();
        initNameTopic();
        initImgBtns();
    }

    @Override
    protected void beforeOpen(Void data) {}

    @Override
    protected void beforeClose() {
        iconsScroll.setVvalue(0.0);
    }

}
