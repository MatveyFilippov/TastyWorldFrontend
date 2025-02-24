package homer.tastyworld.frontend.poscreator.core.menu;

import homer.tastyworld.frontend.starterpack.api.PhotoRequest;
import homer.tastyworld.frontend.starterpack.api.Request;
import homer.tastyworld.frontend.starterpack.base.utils.misc.TypeChanger;
import homer.tastyworld.frontend.starterpack.base.utils.ui.helpers.Helper;
import homer.tastyworld.frontend.starterpack.base.utils.ui.helpers.Text;
import javafx.beans.binding.StringExpression;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.apache.hc.core5.http.Method;
import java.util.Map;

public class ImageProducts {

    private final int column, row;
    private final GridPane place;
    private final AnchorPane topic;
    private final StringExpression topicFontSize;

    public ImageProducts(AnchorPane topic, GridPane place, int column, int row) {
        this.topic = topic;
        this.place = place;
        this.column = column;
        this.row = row;
        topicFontSize = Text.getAdaptiveFontSize(topic, 20);
    }

    public void clean() {
        place.getChildren().remove(place.getChildren().getLast());
        topic.getChildren().remove(topic.getChildren().getLast());
    }

    public void fill(long menuID) {
        GridPane gridPane = new GridPane();
        gridPane.setHgap(25);
        gridPane.setVgap(25);

        ScrollPane scrollPane = new ScrollPane(gridPane);
        scrollPane.setFitToWidth(true);

        iterateAndSetMenu(menuID, gridPane, scrollPane);

        place.add(scrollPane, column, row);
    }

    private void iterateAndSetMenu(long menuID, GridPane parent, ScrollPane scrollPane) {
        Request request = new Request("/menu/read", Method.GET);
        request.putInBody("id", menuID);
        Map<String, Object> response = request.request().getResultAsJSON();
        Text.setTextCentre(topic, (String) response.get("NAME"), topicFontSize, null);
        Long[] productIDs = TypeChanger.toLongArray(response.get("PRODUCT_IDs"));
        for (int i = 0; i < productIDs.length; i++) {
            int col = i % 3;
            int row = i / 3;
            parent.add(getProductImgBtn(scrollPane, productIDs[i]), col, row);
        }
    }

    private static VBox getProductImgBtn(ScrollPane scrollPane, long productID) {
        VBox root = new VBox();
        setImageAndName(root, productID, 0.85, 0.15);
        root.prefWidthProperty().bind(
                scrollPane.widthProperty().subtract(40)
        );
        root.prefHeightProperty().bind(
                scrollPane.heightProperty().divide(3)
        );
        setOnTouch(root, productID);
        return root;
    }

    private static void setImageAndName(VBox root, long productID, double imageRatio, double nameRatio) {
        AnchorPane topPaneWithImage = getImage(productID);
        AnchorPane bottomPaneWithName = getName(productID);
        root.getChildren().addAll(topPaneWithImage, bottomPaneWithName);

        VBox.setVgrow(topPaneWithImage, javafx.scene.layout.Priority.ALWAYS);
        VBox.setVgrow(bottomPaneWithName, javafx.scene.layout.Priority.ALWAYS);
        topPaneWithImage.prefHeightProperty().bind(root.heightProperty().multiply(imageRatio));
        bottomPaneWithName.prefHeightProperty().bind(root.heightProperty().multiply(nameRatio));
    }

    private static AnchorPane getImage(long productID) {
        AnchorPane result = new AnchorPane();
        PhotoRequest request = new PhotoRequest("/product/get_photo");
        request.putInBody("id", productID);
        Helper.setAnchorPaneImageBackground(result, request.read());
        return result;
    }

    private static AnchorPane getName(long productID) {
        AnchorPane result = new AnchorPane();
        Request request = new Request("/product/read", Method.GET);
        request.putInBody("id", productID);
        Map<String, Object> info = request.request().getResultAsJSON();
        Text.setTextCentre(result, (String) info.get("NAME"), Text.getAdaptiveFontSize(result, 15), null);
        return result;
    }

    private static void setOnTouch(VBox menuBtn, long productID) {
        menuBtn.setOnMouseClicked(event -> System.out.println(productID));
    }

}
