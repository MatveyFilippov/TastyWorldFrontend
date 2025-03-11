package homer.tastyworld.frontend.pos.creator.panes.stable;

import homer.tastyworld.frontend.pos.creator.POSCreatorApplication;
import homer.tastyworld.frontend.pos.creator.panes.dynamic.DynamicParentPane;
import homer.tastyworld.frontend.starterpack.api.PhotoRequest;
import homer.tastyworld.frontend.starterpack.api.Request;
import homer.tastyworld.frontend.starterpack.api.requests.MyParams;
import homer.tastyworld.frontend.starterpack.base.utils.ui.helpers.PaneHelper;
import homer.tastyworld.frontend.starterpack.base.utils.ui.helpers.TextHelper;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.apache.hc.core5.http.Method;
import java.util.Map;

@Getter
@SuperBuilder
public class MenuParentPane extends StableParentPane {

    private AnchorPane menuPaneDeleteOrderImgBtn, menuPaneLookOrderImgBtn;
    private AnchorPane menuPaneTopic;
    private GridPane menuPaneImgMenuContainer;
    private DynamicParentPane productsParentPane;
    @Builder.Default
    private GridPane clickableItemsTable = new GridPane();

    private void initClickableItemsTableInMenu() {  // TODO: init in thread
        clickableItemsTable.setHgap(5);
        clickableItemsTable.setVgap(25);
        clickableItemsTable.setAlignment(Pos.CENTER);

        ScrollPane scroll = new ScrollPane(clickableItemsTable);
        scroll.setFitToWidth(true);

        Long[] menuIDs = MyParams.getMenu();
        for (int i = 0; i < menuIDs.length; i++) {
            System.out.printf("\rGetting menu category (%s/%s)", i, menuIDs.length);
            clickableItemsTable.add(getMenuImgBtn(menuIDs[i], scroll), i % 3, i / 3);
        }
        System.out.printf("\rGetting menu category (%s/%s)%n", menuIDs.length, menuIDs.length);
        productsParentPane.cacheAll(menuIDs);

        menuPaneImgMenuContainer.add(scroll, 1, 0);
    }

    private VBox getMenuImgBtn(long menuID, ScrollPane scroll) {
        VBox menu = new VBox();
        menu.setFillWidth(true);
        menu.prefWidthProperty().bind(scroll.widthProperty());
        menu.prefHeightProperty().bind(scroll.heightProperty().divide(2));

        PaneHelper.setOnMouseClickedWithLongPressing(menu, event -> {
            productsParentPane.fill(menuID);
            productsParentPane.openAndCloseFrom(parent);
        });

        AnchorPane topPaneWithImage = getMenuImage(menuID);
        AnchorPane bottomPaneWithName = getMenuName(menuID);
        menu.getChildren().addAll(topPaneWithImage, bottomPaneWithName);
        VBox.setVgrow(topPaneWithImage, Priority.ALWAYS);
        VBox.setVgrow(bottomPaneWithName, Priority.ALWAYS);
        topPaneWithImage.prefHeightProperty().bind(menu.heightProperty().multiply(0.90));
        bottomPaneWithName.prefHeightProperty().bind(menu.heightProperty().multiply(0.10));

        return menu;
    }

    private AnchorPane getMenuImage(long menuID) {
        AnchorPane result = new AnchorPane();
        PhotoRequest request = new PhotoRequest("/menu/get_photo");
        request.putInBody("id", menuID);
        PaneHelper.setImageBackgroundBottom(result, request.read());
        return result;
    }

    private AnchorPane getMenuName(long menuID) {
        AnchorPane result = new AnchorPane();
        Request request = new Request("/menu/read", Method.GET);
        request.putInBody("id", menuID);
        Map<String, Object> info = request.request().getResultAsJSON();
        TextHelper.setTextCentre(result, (String) info.get("NAME"), TextHelper.getAdaptiveFontSize(result, 12), null);
        return result;
    }

    private void initTopicInMenuPane() {
        TextHelper.setTextCentre(menuPaneTopic, "Меню", TextHelper.getAdaptiveFontSize(menuPaneTopic, 15), null);
    }

    private void initImgBtnsInMenuPane() {
        PaneHelper.setImageBackgroundCentre(
                menuPaneDeleteOrderImgBtn,
                POSCreatorApplication.class.getResourceAsStream("images/buttons/MenuPane/menuPaneDeleteOrderImgBtn.png")
        );
        PaneHelper.setImageBackgroundCentre(
                menuPaneLookOrderImgBtn,
                POSCreatorApplication.class.getResourceAsStream("images/buttons/MenuPane/menuPaneLookOrderImgBtn.png")
        );
    }

    @Override
    public void initialize() {
        initClickableItemsTableInMenu();
        initTopicInMenuPane();
        initImgBtnsInMenuPane();
    }

}
