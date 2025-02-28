package homer.tastyworld.frontend.poscreator.panes.stable;

import homer.tastyworld.frontend.poscreator.POSCreatorApplication;
import homer.tastyworld.frontend.poscreator.panes.dynamic.DynamicParentPane;
import homer.tastyworld.frontend.starterpack.api.PhotoRequest;
import homer.tastyworld.frontend.starterpack.api.Request;
import homer.tastyworld.frontend.starterpack.api.requests.MyParams;
import homer.tastyworld.frontend.starterpack.base.utils.ui.helpers.Helper;
import homer.tastyworld.frontend.starterpack.base.utils.ui.helpers.Text;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
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

    private void initClickableItemsTableInMenu() {
        clickableItemsTable.setHgap(5);
        clickableItemsTable.setVgap(25);
        clickableItemsTable.setAlignment(Pos.CENTER);

        ScrollPane scroll = new ScrollPane(clickableItemsTable);
        scroll.setFitToWidth(true);

        Long[] menuIDs = MyParams.getMenu();
        for (int i = 0; i < menuIDs.length; i++) {
            clickableItemsTable.add(getMenuImgBtn(menuIDs[i], scroll), i % 3, i / 3);
        }
        productsParentPane.cacheAll(menuIDs);

        menuPaneImgMenuContainer.add(scroll, 1, 0);
    }

    private VBox getMenuImgBtn(long menuID, ScrollPane scroll) {
        VBox root = new VBox();
        root.setFillWidth(true);
        root.prefWidthProperty().bind(scroll.widthProperty());
        root.prefHeightProperty().bind(scroll.heightProperty().divide(2));

        root.setOnMouseClicked(event -> {
            productsParentPane.fill(menuID);
            productsParentPane.openAndCloseFrom(parent);
        });

        AnchorPane topPaneWithImage = getMenuImage(menuID);
        AnchorPane bottomPaneWithName = getMenuName(menuID);
        root.getChildren().addAll(topPaneWithImage, bottomPaneWithName);
        VBox.setVgrow(topPaneWithImage, Priority.ALWAYS);
        VBox.setVgrow(bottomPaneWithName, Priority.ALWAYS);
        topPaneWithImage.prefHeightProperty().bind(root.heightProperty().multiply(0.90));
        bottomPaneWithName.prefHeightProperty().bind(root.heightProperty().multiply(0.10));

        return root;
    }

    private AnchorPane getMenuImage(long menuID) {
        AnchorPane result = new AnchorPane();
        PhotoRequest request = new PhotoRequest("/menu/get_photo");
        request.putInBody("id", menuID);
        Helper.setAnchorPaneImageBackgroundBottom(result, request.read());
        return result;
    }

    private AnchorPane getMenuName(long menuID) {
        AnchorPane result = new AnchorPane();
        Request request = new Request("/menu/read", Method.GET);
        request.putInBody("id", menuID);
        Map<String, Object> info = request.request().getResultAsJSON();
        Text.setTextCentre(result, (String) info.get("NAME"), Text.getAdaptiveFontSize(result, 12), null);
        return result;
    }

    private void initTopicInMenuPane() {
        Text.setTextCentre(menuPaneTopic, "Меню", Text.getAdaptiveFontSize(menuPaneTopic, 15), null);
    }

    private void initImgBtnsInMenuPane() {
        Helper.setAnchorPaneImageBackgroundCentre(
                menuPaneDeleteOrderImgBtn,
                POSCreatorApplication.class.getResourceAsStream("images/buttons/MenuPane/menuPaneDeleteOrderImgBtn.png")
        );
        Helper.setAnchorPaneImageBackgroundCentre(
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
