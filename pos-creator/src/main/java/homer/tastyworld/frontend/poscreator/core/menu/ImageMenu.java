package homer.tastyworld.frontend.poscreator.core.menu;

import homer.tastyworld.frontend.starterpack.api.PhotoRequest;
import homer.tastyworld.frontend.starterpack.api.Request;
import homer.tastyworld.frontend.starterpack.api.requests.MyParams;
import homer.tastyworld.frontend.starterpack.base.utils.ui.helpers.Helper;
import homer.tastyworld.frontend.starterpack.base.utils.ui.helpers.Text;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.apache.hc.core5.http.Method;
import java.util.Map;

public class ImageMenu {

    public static void fill(ImageProducts products, GridPane place, int column, int row, AnchorPane menuParent, AnchorPane productParent) {
        GridPane gridPane = new GridPane();
        gridPane.setHgap(25);
        gridPane.setVgap(25);

        ScrollPane scrollPane = new ScrollPane(gridPane);
        scrollPane.setFitToWidth(true);

        iterateAndSetMenu(gridPane, scrollPane, products, menuParent, productParent);

        place.add(scrollPane, column, row);
    }

    private static void iterateAndSetMenu(GridPane parent, ScrollPane place, ImageProducts products, AnchorPane menuParent, AnchorPane productParent) {
        Long[] menuIDs = MyParams.getMenu();
        for (int i = 0; i < menuIDs.length; i++) {
            int col = i % 3;
            int row = i / 3;
            parent.add(getMenuImgBtn(place, menuIDs[i], products, menuParent, productParent), col, row);
        }
    }

    private static VBox getMenuImgBtn(ScrollPane scrollPane, long menuID, ImageProducts products, AnchorPane menuParent, AnchorPane productParent) {
        VBox root = new VBox();
        setImageAndName(root, menuID, 0.85, 0.15);
        root.prefWidthProperty().bind(
                scrollPane.widthProperty().subtract(40)
        );
        root.prefHeightProperty().bind(
                scrollPane.heightProperty().divide(3)
        );
        setOnTouch(root, menuID, products, menuParent, productParent);
        return root;
    }

    private static void setImageAndName(VBox root, long menuID, double imageRatio, double nameRatio) {
        AnchorPane topPaneWithImage = getImage(menuID);
        AnchorPane bottomPaneWithName = getName(menuID);
        root.getChildren().addAll(topPaneWithImage, bottomPaneWithName);

        VBox.setVgrow(topPaneWithImage, javafx.scene.layout.Priority.ALWAYS);
        VBox.setVgrow(bottomPaneWithName, javafx.scene.layout.Priority.ALWAYS);
        topPaneWithImage.prefHeightProperty().bind(root.heightProperty().multiply(imageRatio));
        bottomPaneWithName.prefHeightProperty().bind(root.heightProperty().multiply(nameRatio));
    }

    private static AnchorPane getImage(long menuID) {
        AnchorPane result = new AnchorPane();
        PhotoRequest request = new PhotoRequest("/menu/get_photo");
        request.putInBody("id", menuID);
        Helper.setAnchorPaneImageBackground(result, request.read());
        return result;
    }

    private static AnchorPane getName(long menuID) {
        AnchorPane result = new AnchorPane();
        Request request = new Request("/menu/read", Method.GET);
        request.putInBody("id", menuID);
        Map<String, Object> info = request.request().getResultAsJSON();
        Text.setTextCentre(result, (String) info.get("NAME"), Text.getAdaptiveFontSize(result, 15), null);
        return result;
    }

    private static void setOnTouch(VBox menuBtn, long menuID, ImageProducts products, AnchorPane menuParent, AnchorPane productParent) {
        menuBtn.setOnMouseClicked(event -> {
            Helper.openParentPane(menuParent, productParent);
            products.fill(menuID);
        });
    }

}
