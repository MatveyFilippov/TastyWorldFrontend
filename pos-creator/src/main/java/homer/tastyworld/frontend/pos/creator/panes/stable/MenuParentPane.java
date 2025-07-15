package homer.tastyworld.frontend.pos.creator.panes.stable;

import homer.tastyworld.frontend.pos.creator.POSCreatorApplication;
import homer.tastyworld.frontend.pos.creator.panes.dynamic.DynamicParentPane;
import homer.tastyworld.frontend.starterpack.base.utils.ui.helpers.AdaptiveTextHelper;
import homer.tastyworld.frontend.starterpack.base.utils.ui.helpers.PaneHelper;
import homer.tastyworld.frontend.starterpack.entity.Menu;
import homer.tastyworld.frontend.starterpack.entity.current.ClientPoint;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import lombok.experimental.SuperBuilder;
import java.util.Arrays;

@SuperBuilder
public class MenuParentPane extends StableParentPane {

    private final AnchorPane deleteOrderImgBtn, lookOrderImgBtn;
    private final AnchorPane paneNameTopic;
    private final GridPane menuImgBtnContainer;
    private final DynamicParentPane productsParentPane;
    private final GridPane clickableItemsTable = new GridPane();

    private void initClickableItemsTableInMenu() {  // TODO: init in thread
        clickableItemsTable.setHgap(5);
        clickableItemsTable.setVgap(25);
        clickableItemsTable.setAlignment(Pos.CENTER);

        ScrollPane scroll = new ScrollPane(clickableItemsTable);
        scroll.setFitToWidth(true);

        long[] activeMenuIDs = Arrays.stream(ClientPoint.getMenuIDs())
                                     .mapToObj(Menu::get)
                                     .filter(Menu::isActive)
                                     .mapToLong(Menu::getId)
                                     .toArray();
        for (int i = 0; i < activeMenuIDs.length; i++) {
            clickableItemsTable.add(getMenuImgBtn(activeMenuIDs[i], scroll), i % 3, i / 3);
        }
        productsParentPane.cacheAll(activeMenuIDs);

        menuImgBtnContainer.add(scroll, 1, 0);
    }

    private VBox getMenuImgBtn(long menuID, ScrollPane scroll) {
        VBox result = new VBox();
        result.setFillWidth(true);
        result.prefWidthProperty().bind(scroll.widthProperty());
        result.prefHeightProperty().bind(scroll.heightProperty().divide(2));

        PaneHelper.setOnMouseClickedWithPressingCountChecking(result, 2, event -> {
            productsParentPane.fill(menuID);
            productsParentPane.openAndCloseFrom(current);
        });

        Menu menu = Menu.get(menuID);

        AnchorPane topPaneWithImage = new AnchorPane();
        PaneHelper.setImageBackgroundBottom(topPaneWithImage, menu.getPhoto());

        AnchorPane bottomPaneWithName = new AnchorPane();
        AdaptiveTextHelper.setTextCentre(bottomPaneWithName, menu.getName(), 15, null);

        result.getChildren().addAll(topPaneWithImage, bottomPaneWithName);
        VBox.setVgrow(topPaneWithImage, Priority.ALWAYS);
        VBox.setVgrow(bottomPaneWithName, Priority.ALWAYS);
        topPaneWithImage.prefHeightProperty().bind(result.heightProperty().multiply(0.90));
        bottomPaneWithName.prefHeightProperty().bind(result.heightProperty().multiply(0.10));

        return result;
    }

    private void initTopicInMenuPane() {
        AdaptiveTextHelper.setTextCentre(paneNameTopic, "Меню", 15, Color.web("#555555"));
    }

    private void initImgBtnsInMenuPane() {
        PaneHelper.setImageBackgroundCentre(
                deleteOrderImgBtn,
                POSCreatorApplication.class.getResourceAsStream("images/buttons/MenuPane/DeleteOrder.png")
        );
        PaneHelper.setImageBackgroundCentre(
                lookOrderImgBtn,
                POSCreatorApplication.class.getResourceAsStream("images/buttons/MenuPane/LookOrder.png")
        );
    }

    @Override
    public void initialize() {
        initClickableItemsTableInMenu();
        initTopicInMenuPane();
        initImgBtnsInMenuPane();
    }

}
