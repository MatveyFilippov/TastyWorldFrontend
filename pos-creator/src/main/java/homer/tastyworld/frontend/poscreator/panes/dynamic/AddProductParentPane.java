package homer.tastyworld.frontend.poscreator.panes.dynamic;

import homer.tastyworld.frontend.poscreator.POSCreatorApplication;
import homer.tastyworld.frontend.starterpack.api.Request;
import homer.tastyworld.frontend.starterpack.base.utils.misc.TypeChanger;
import homer.tastyworld.frontend.starterpack.base.utils.ui.helpers.Helper;
import homer.tastyworld.frontend.starterpack.base.utils.ui.helpers.Text;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringExpression;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.apache.hc.core5.http.Method;
import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@SuperBuilder
public class AddProductParentPane extends DynamicParentPane {

    private AnchorPane addProductCloseImgBtn, addProductSubmitImgBtn;
    private AnchorPane addProductNameTopic;
    private AnchorPane addProductPriceTopic;
    private TextField addProductTotalPriceField;
    private AnchorPane addProductMinusQTYImgBtn, addProductPlusQTYImgBtn;
    private AnchorPane addProductQTYTypeTopic;
    private TextField addProductQTYFiled;
    private GridPane addProductNumbersKeyboard;
    private AnchorPane addProductAdditivesTopic;
    private GridPane addProductAdditivesContainer;
    private static GridPane additivesTable = new GridPane();
    private static ScrollPane scrollAdditives = new ScrollPane(additivesTable);
    private static final Map<Long, Map<String, Object>> additivesCache = new ConcurrentHashMap<>();
    private static StringExpression topicFontSize;
    private static Integer onePiece;
    private static BigDecimal pricePerPiece;
    public static Long productID;
    public static Integer qty;
    public static final Map<Long, Integer> notDefaultAdditives = new ConcurrentHashMap<>();


    @Override
    public void fill(long productID) {
        Request request = new Request("/product/read", Method.GET);
        request.putInBody("id", productID);
        Map<String, Object> response = request.request().getResultAsJSON();
        Text.setTextCentre(addProductNameTopic, (String) response.get("NAME"), topicFontSize, null);
        addProductQTYFiled.setText(String.valueOf(response.get("DEFAULT_PEACE_QTY")));
        String priceType;
        if (response.get("PIECE_TYPE").equals("ONE_HUNDRED_GRAMS")) {
            onePiece = 100;
            priceType = "Гр";
        } else {
            onePiece = 1;
            priceType = "Шт";
        }
        Text.setTextCentre(addProductQTYTypeTopic, priceType, Text.getAdaptiveFontSize(addProductQTYTypeTopic, 5), null);
        pricePerPiece = TypeChanger.toBigDecimal(response.get("PRICE_PEER_PEACE")).divide(BigDecimal.valueOf(onePiece));
        recalculatePrice();
        Long[] additiveIDs = TypeChanger.toSortedLongArray(response.get("ADDITIVE_IDs"));
        for (int i = 0; i < additiveIDs.length; i++) {
            additivesTable.add(getAdditivesLine(additiveIDs[i]), 0, i);
        }
        AddProductParentPane.productID = productID;
        AddProductParentPane.qty = TypeChanger.toInt(response.get("DEFAULT_PEACE_QTY"));
    }

    @Override
    public void cacheAll(Long[] productIDs) {
        Request productRequest = new Request("/product/read", Method.GET);
        Request additiveRequest = new Request("/product/read_additive", Method.GET);
        for (long productID : productIDs) {
            productRequest.putInBody("id", productID);
            Map<String, Object> response = productRequest.request().getResultAsJSON();
            for (long additiveID : TypeChanger.toSortedLongArray(response.get("ADDITIVE_IDs"))) {
                additiveRequest.putInBody("id", additiveID);
                additivesCache.put(additiveID, additiveRequest.request().getResultAsJSON());
            }
        }
    }

    private HBox getAdditivesLine(long additiveID) {
        Map<String, Object> info = additivesCache.computeIfAbsent(additiveID, id -> {
            Request request = new Request("/product/read_additive", Method.GET);
            request.putInBody("id", additiveID);
            return request.request().getResultAsJSON();
        });
        return createAdditivesLine(additiveID, info);
    }

    private HBox createAdditivesLine(long additiveID, Map<String, Object> info) {
        HBox row = new HBox();
        row.setSpacing(10);
        row.prefWidthProperty().bind(scrollAdditives.widthProperty());
        row.prefHeightProperty().bind(scrollAdditives.heightProperty().divide(4));
        row.setAlignment(Pos.CENTER);

        AnchorPane name = getAdditiveName(info);
        TextField qty = getAdditiveQTY(row, info);
        AnchorPane minus = getAdditiveMinusImgBtn(qty, additiveID, info);
        AnchorPane plus = getAdditivePlusImgBtn(qty, additiveID, info);
        AnchorPane space = new AnchorPane();
        row.getChildren().addAll(name, minus, qty, plus, space);
        HBox.setHgrow(qty, Priority.ALWAYS);
        HBox.setHgrow(minus, Priority.ALWAYS);
        HBox.setHgrow(name, Priority.ALWAYS);
        HBox.setHgrow(plus, Priority.ALWAYS);
        HBox.setHgrow(space, Priority.ALWAYS);
        name.prefWidthProperty().bind(row.widthProperty().multiply(0.50));
        minus.prefWidthProperty().bind(row.widthProperty().multiply(0.1));
        qty.prefWidthProperty().bind(row.widthProperty().multiply(0.25));
        plus.prefWidthProperty().bind(row.widthProperty().multiply(0.1));
        space.prefWidthProperty().bind(row.widthProperty().multiply(0.05));

        return row;
    }

    private AnchorPane getAdditiveName(Map<String, Object> info) {
        AnchorPane name = new AnchorPane();
        Text.setTextCentre(name, (String) info.get("NAME"), Text.getAdaptiveFontSize(name, 8), null);
        return name;
    }

    private TextField getAdditiveQTY(HBox row, Map<String, Object> info) {
        TextField qty = new TextField();
        qty.setText(String.valueOf(info.get("DEFAULT_PEACE_QTY")));
        qty.setAlignment(Pos.CENTER);
        qty.fontProperty().bind(Bindings.createObjectBinding(
                () -> Font.font(Math.min(row.getWidth() / 10, row.getHeight() / 6)),
                row.widthProperty(), row.heightProperty()
        ));
        qty.setEditable(false);
        return qty;
    }

    private AnchorPane getAdditiveMinusImgBtn(TextField qty, long additiveID, Map<String, Object> info) {
        int step;
        if (info.get("PIECE_TYPE").equals("ONE_HUNDRED_GRAMS")) {
            step = 100;
        } else {
            step = 1;
        }
        AnchorPane minus = new AnchorPane();
        Helper.setAnchorPaneImageBackgroundCentre(minus, POSCreatorApplication.class.getResourceAsStream("images/buttons/AddProductPane/addProductAdditiveMinusQTYImgBtn.png"));
        minus.setOnMouseClicked(event -> {
            int additiveQTY = Integer.parseInt(qty.getText()) - step;
            if (additiveQTY <= 0) {
                additiveQTY = 0;
            }
            AddProductParentPane.notDefaultAdditives.put(additiveID, additiveQTY);
            qty.setText(String.valueOf(additiveQTY));
        });
        return minus;
    }

    private AnchorPane getAdditivePlusImgBtn(TextField qty, long additiveID, Map<String, Object> info) {
        int step;
        if (info.get("PIECE_TYPE").equals("ONE_HUNDRED_GRAMS")) {
            step = 100;
        } else {
            step = 1;
        }
        AnchorPane plus = new AnchorPane();
        Helper.setAnchorPaneImageBackgroundCentre(plus, POSCreatorApplication.class.getResourceAsStream("images/buttons/AddProductPane/addProductAdditivePlusQTYImgBtn.png"));
        plus.setOnMouseClicked(event -> {
            int additiveQTY = Integer.parseInt(qty.getText()) + step;
            AddProductParentPane.notDefaultAdditives.put(additiveID, additiveQTY);
            qty.setText(String.valueOf(additiveQTY));
        });
        return plus;
    }

    @Override
    public void clean() {
        productID = null;
        qty = null;
        notDefaultAdditives.clear();

        additivesTable.getChildren().clear();
        addProductQTYTypeTopic.getChildren().clear();
        addProductNameTopic.getChildren().clear();
        onePiece = null;
        pricePerPiece = null;
        addProductQTYFiled.setText("0");
        addProductTotalPriceField.setText("");
    }

    private void recalculatePrice() {
        int qty = Integer.parseInt(addProductQTYFiled.getText());
        BigDecimal totalPrice = pricePerPiece.multiply(BigDecimal.valueOf(qty));
        addProductTotalPriceField.setText(totalPrice.toString());
    }

    private void initImgBtnsInAddProductPane() {
        Helper.setAnchorPaneImageBackgroundCentre(
                addProductCloseImgBtn,
                POSCreatorApplication.class.getResourceAsStream("images/buttons/AddProductPane/addProductCloseImgBtn.png")
        );
        Helper.setAnchorPaneImageBackgroundCentre(
                addProductSubmitImgBtn,
                POSCreatorApplication.class.getResourceAsStream("images/buttons/AddProductPane/addProductSubmitImgBtn.png")
        );
        Helper.setAnchorPaneImageBackgroundCentre(
                addProductMinusQTYImgBtn,
                POSCreatorApplication.class.getResourceAsStream("images/buttons/AddProductPane/addProductMinusQTYImgBtn.png")
        );
        Helper.setAnchorPaneImageBackgroundCentre(
                addProductPlusQTYImgBtn,
                POSCreatorApplication.class.getResourceAsStream("images/buttons/AddProductPane/addProductPlusQTYImgBtn.png")
        );
    }

    private void setPlusMinusImgBtnsClickable() {
        addProductMinusQTYImgBtn.setOnMouseClicked(event -> {
            int qty = Integer.parseInt(addProductQTYFiled.getText());
            qty -= onePiece;
            if (qty < onePiece) {
                qty = onePiece;
            }
            AddProductParentPane.qty = qty;
            addProductQTYFiled.setText(String.valueOf(qty));
            recalculatePrice();
        });
        addProductPlusQTYImgBtn.setOnMouseClicked(event -> {
            int qty = Integer.parseInt(addProductQTYFiled.getText());
            qty += onePiece;
            AddProductParentPane.qty = qty;
            addProductQTYFiled.setText(String.valueOf(qty));
            recalculatePrice();
        });
    }

    private void initTopicsInAddProductPane() {
        Text.setTextCentre(addProductPriceTopic, "Цена", Text.getAdaptiveFontSize(addProductPriceTopic, 5), null);
        Text.setTextCentre(addProductAdditivesTopic, "Добавки", Text.getAdaptiveFontSize(addProductAdditivesTopic, 15), null);
    }

    private AnchorPane getClickableKbBtn(String toAppend) {
        AnchorPane btn = new AnchorPane();
        Text.setTextCentre(btn, toAppend, Text.getAdaptiveFontSize(btn, 2), null);
        btn.setOnMouseClicked(event -> {
            String old = addProductQTYFiled.getText().replace(" ", "");
            if (old.equals("0")) {
                addProductQTYFiled.setText(toAppend);
            } else {
                addProductQTYFiled.setText(old + toAppend);
            }
            recalculatePrice();
        });
        return btn;
    }

    private AnchorPane getClickableShiftBackKbBtn() {
        AnchorPane btn = new AnchorPane();
        Text.setTextCentre(btn, "<--", Text.getAdaptiveFontSize(btn, 4), null);
        btn.setOnMouseClicked(event -> {
            String old = addProductQTYFiled.getText().replace(" ", "");
            int len = old.length();
            if (len <= 1) {
                addProductQTYFiled.setText("0");
            } else {
                addProductQTYFiled.setText(old.substring(0, len - 1));
            }
            recalculatePrice();
        });
        return btn;
    }

    private void initNumbersKeyboardInAddProductPane() {
        for (int i = 0; i < 9; i++) {
            addProductNumbersKeyboard.add(getClickableKbBtn(String.valueOf(i+1)), i % 3, i / 3);
        }
        addProductNumbersKeyboard.add(getClickableKbBtn("0"), 3, 0);
        addProductNumbersKeyboard.add(getClickableShiftBackKbBtn(), 3, 1);
        addProductQTYFiled.setText("0");
    }

    private void initTopicFontSize() {
        topicFontSize = Text.getAdaptiveFontSize(addProductNameTopic, 20);
    }

    private void initAdditiveLines() {
        additivesTable.setGridLinesVisible(true);
        additivesTable.setVgap(5);
        additivesTable.setAlignment(Pos.CENTER);
        scrollAdditives.setFitToWidth(true);
        addProductAdditivesContainer.add(scrollAdditives, 0, 1);
    }

    @Override
    public void initialize() {
        initTopicFontSize();
        initImgBtnsInAddProductPane();
        setPlusMinusImgBtnsClickable();
        initTopicsInAddProductPane();
        initNumbersKeyboardInAddProductPane();
        initAdditiveLines();
    }

}
