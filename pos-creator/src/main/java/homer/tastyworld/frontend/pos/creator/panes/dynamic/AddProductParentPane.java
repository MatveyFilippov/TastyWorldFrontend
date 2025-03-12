package homer.tastyworld.frontend.pos.creator.panes.dynamic;

import homer.tastyworld.frontend.pos.creator.POSCreatorApplication;
import homer.tastyworld.frontend.starterpack.api.Request;
import homer.tastyworld.frontend.starterpack.base.utils.misc.TypeChanger;
import homer.tastyworld.frontend.starterpack.base.utils.ui.helpers.PaneHelper;
import homer.tastyworld.frontend.starterpack.base.utils.ui.helpers.TextHelper;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Getter
@SuperBuilder
public class AddProductParentPane extends DynamicParentPane {

    public static class Product {

        public static Long menuID;
        public static Long productID;
        public static String name;
        public static Integer pieceStep;
        public static String pieceType;
        public static BigDecimal price;
        public static Integer qty;
        public static final Map<Long, Integer> notDefaultAdditives = new ConcurrentHashMap<>();

        public static void clean() {
            menuID = null;
            productID = null;
            name = null;
            pieceStep = null;
            pieceType = null;
            price = null;
            qty = null;
            notDefaultAdditives.clear();
        }

    }

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
    private static StringExpression productNameTopicFontSize;
    private static StringExpression productQTYTopicFontSize;
    private static final ScrollPane scroll = new ScrollPane();
    private static final Map<Long, Map<String, Object>> productCache = new ConcurrentHashMap<>();
    private static final Map<Long, List<Map<String, Object>>> additiveCache = new ConcurrentHashMap<>();

    @Override
    protected String getCacheProcess(int total, int actual) {
        return String.format("Getting products (%s/%s)", actual, total);
    }

    @Override
    protected void cacheTask(Long productID) {
        Request productRequest = new Request("/product/read", Method.GET);
        Request additiveRequest = new Request("/product/read_additive", Method.GET);
        productRequest.putInBody("id", productID);
        Map<String, Object> productInfo = productRequest.request().getResultAsJSON();
        productCache.put(productID, productInfo);
        List<Map<String, Object>> additivesInfo = new ArrayList<>();
        for (long additiveID : TypeChanger.toSortedLongArray(productInfo.get("ADDITIVE_IDs"))) {
            additiveRequest.putInBody("id", additiveID);
            additivesInfo.add(additiveRequest.request().getResultAsJSON());
        }
        additiveCache.put(productID, additivesInfo);
    }

    private void setProduct(Map<String, Object> productInfo) {
        Product.menuID = TypeChanger.toLong(productInfo.get("MENU_ID"));
        Product.productID = TypeChanger.toLong(productInfo.get("ID"));
        Product.name = (String) productInfo.get("NAME");
        if (productInfo.get("PIECE_TYPE").equals("ONE_HUNDRED_GRAMS")) {
            Product.pieceStep = 100;
            Product.pieceType = "Гр";
        } else {
            Product.pieceStep = 1;
            Product.pieceType = "Шт";
        }
        Product.price = TypeChanger.toBigDecimal(productInfo.get("PRICE_PEER_PEACE")).divide(BigDecimal.valueOf(Product.pieceStep));
        Product.qty = TypeChanger.toInt(productInfo.get("DEFAULT_PEACE_QTY"));
        Product.notDefaultAdditives.clear();
    }

    @Override
    public void fill(long productID) {
        Map<String, Object> productInfo = productCache.computeIfAbsent(productID, id -> {
            Request request = new Request("/product/read", Method.GET);
            request.putInBody("id", productID);
            return request.request().getResultAsJSON();
        });
        setProduct(productInfo);

        TextHelper.setTextCentre(addProductNameTopic, Product.name, productNameTopicFontSize, null);
        TextHelper.setTextCentre(addProductQTYTypeTopic, Product.pieceType, productQTYTopicFontSize, null);
        addProductQTYFiled.setText(String.valueOf(Product.qty));
        recalculatePrice();

        scroll.setContent(computeAdditivesTable(productID, TypeChanger.toSortedLongArray(productInfo.get("ADDITIVE_IDs"))));
    }

    private void recalculatePrice() {
        BigDecimal totalPrice = Product.price.multiply(BigDecimal.valueOf(Product.qty));
        addProductTotalPriceField.setText(totalPrice.toString());
    }

    private GridPane computeAdditivesTable(long productID, Long[] additiveIDs) {
        GridPane table = new GridPane();
        table.setVgap(5);
        table.setAlignment(Pos.CENTER);
        List<Map<String, Object>> additivesInfo = additiveCache.computeIfAbsent(productID, ignored -> {
            Request request = new Request("/product/read_additive", Method.GET);
            return Arrays.stream(additiveIDs).map(additiveID -> {
                request.putInBody("id", additiveID);
                return request.request().getResultAsJSON();
            }).collect(Collectors.toList());
        });
        for (int i = 0; i < additivesInfo.size(); i++) {
            table.add(getAdditiveLine(additivesInfo.get(i)), 0, i);
        }
        return table;
    }

    private HBox getAdditiveLine(Map<String, Object> additiveInfo) {
        HBox row = new HBox(10);
        row.setStyle("-fx-border-color: #000000;");
        row.prefWidthProperty().bind(scroll.widthProperty());
        row.prefHeightProperty().bind(scroll.heightProperty().divide(4));
        row.setAlignment(Pos.CENTER);

        AnchorPane name = getAdditiveName(additiveInfo);
        TextField qty = getAdditiveQTY(row, additiveInfo);
        AnchorPane minus = new AnchorPane();
        AnchorPane plus = new AnchorPane();
        AnchorPane space = new AnchorPane();

        setPlusMinusAdditiveImgBtnsClickable(plus, minus, qty, additiveInfo);

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

    private AnchorPane getAdditiveName(Map<String, Object> additiveInfo) {
        AnchorPane name = new AnchorPane();
        TextHelper.setTextCentre(name, (String) additiveInfo.get("NAME"), TextHelper.getAdaptiveFontSize(name, 8), null);
        return name;
    }

    private TextField getAdditiveQTY(HBox row, Map<String, Object> additiveInfo) {
        TextField qty = new TextField();
        qty.setText(String.valueOf(additiveInfo.get("DEFAULT_PEACE_QTY")));
        qty.setAlignment(Pos.CENTER);
        qty.fontProperty().bind(Bindings.createObjectBinding(
                () -> Font.font(Math.min(row.getWidth() / 10, row.getHeight() / 6)),
                row.widthProperty(), row.heightProperty()
        ));
        qty.setEditable(false);
        return qty;
    }

    private void setPlusMinusAdditiveImgBtnsClickable(AnchorPane plus, AnchorPane minus, TextField additiveQTY, Map<String, Object> additiveInfo) {
        PaneHelper.setImageBackgroundCentre(plus, POSCreatorApplication.class.getResourceAsStream("images/buttons/AddProductPane/addProductAdditivePlusQTYImgBtn.png"));
        PaneHelper.setImageBackgroundCentre(minus, POSCreatorApplication.class.getResourceAsStream("images/buttons/AddProductPane/addProductAdditiveMinusQTYImgBtn.png"));
        int step = additiveInfo.get("PIECE_TYPE").equals("ONE_HUNDRED_GRAMS") ? 100 : 1;
        long additiveID = TypeChanger.toLong(additiveInfo.get("ID"));
        plus.setOnMouseClicked(event -> {
            int qty = Integer.parseInt(additiveQTY.getText()) + step;
            Product.notDefaultAdditives.put(additiveID, qty);
            additiveQTY.setText(String.valueOf(qty));
        });
        minus.setOnMouseClicked(event -> {
            int qty = Integer.parseInt(additiveQTY.getText()) - step;
            if (qty < step) {
                qty = step;
            }
            Product.notDefaultAdditives.put(additiveID, qty);
            additiveQTY.setText(String.valueOf(qty));
        });
    }

    @Override
    protected void cleanTask() {
        Product.clean();
        addProductQTYTypeTopic.getChildren().clear();
        addProductNameTopic.getChildren().clear();
        addProductQTYFiled.setText("0");
        addProductTotalPriceField.setText("0");
    }

    private void initImgBtnsInAddProductPane() {
        PaneHelper.setImageBackgroundCentre(
                addProductCloseImgBtn,
                POSCreatorApplication.class.getResourceAsStream("images/buttons/AddProductPane/addProductCloseImgBtn.png")
        );
        PaneHelper.setImageBackgroundCentre(
                addProductSubmitImgBtn,
                POSCreatorApplication.class.getResourceAsStream("images/buttons/AddProductPane/addProductSubmitImgBtn.png")
        );
        PaneHelper.setImageBackgroundCentre(
                addProductMinusQTYImgBtn,
                POSCreatorApplication.class.getResourceAsStream("images/buttons/AddProductPane/addProductMinusQTYImgBtn.png")
        );
        PaneHelper.setImageBackgroundCentre(
                addProductPlusQTYImgBtn,
                POSCreatorApplication.class.getResourceAsStream("images/buttons/AddProductPane/addProductPlusQTYImgBtn.png")
        );
    }

    private void setPlusMinusProductImgBtnsClickable() {
        addProductMinusQTYImgBtn.setOnMouseClicked(event -> {
            Product.qty = Integer.parseInt(addProductQTYFiled.getText()) - Product.pieceStep;
            if (Product.qty < Product.pieceStep) {
                Product.qty = Product.pieceStep;
            }
            addProductQTYFiled.setText(String.valueOf(Product.qty));
            recalculatePrice();
        });
        addProductPlusQTYImgBtn.setOnMouseClicked(event -> {
            Product.qty = Integer.parseInt(addProductQTYFiled.getText()) + Product.pieceStep;
            addProductQTYFiled.setText(String.valueOf(Product.qty));
            recalculatePrice();
        });
    }

    private void initTopicsInAddProductPane() {
        TextHelper.setTextCentre(addProductPriceTopic, "Цена", TextHelper.getAdaptiveFontSize(addProductPriceTopic, 5), null);
        TextHelper.setTextCentre(addProductAdditivesTopic, "Добавки", TextHelper.getAdaptiveFontSize(addProductAdditivesTopic, 15), null);
    }

    private AnchorPane getClickableNumberKbBtn(String toAppend) {
        AnchorPane btn = new AnchorPane();
        TextHelper.setTextCentre(btn, toAppend, TextHelper.getAdaptiveFontSize(btn, 2), null);
        btn.setOnMouseClicked(event -> {
            String oldQTY = addProductQTYFiled.getText().replace(" ", "");
            String newQTY = oldQTY.equals("0") ? toAppend : oldQTY + toAppend;
            Product.qty = Integer.parseInt(newQTY);
            addProductQTYFiled.setText(newQTY);
            recalculatePrice();
        });
        return btn;
    }

    private AnchorPane getClickableShiftBackKbBtn() {
        AnchorPane btn = new AnchorPane();
        TextHelper.setTextCentre(btn, "<--", TextHelper.getAdaptiveFontSize(btn, 4), null);
        btn.setOnMouseClicked(event -> {
            String oldQTY = addProductQTYFiled.getText().replace(" ", "");
            int len = oldQTY.length();
            Product.qty = len <= 1 ? 0 : Integer.parseInt(oldQTY.substring(0, len - 1));
            addProductQTYFiled.setText(String.valueOf(Product.qty));
            recalculatePrice();
        });
        return btn;
    }

    private void initNumbersKeyboardInAddProductPane() {
        for (int i = 0; i < 9; i++) {
            addProductNumbersKeyboard.add(getClickableNumberKbBtn(String.valueOf(i + 1)), i % 3, i / 3);
        }
        addProductNumbersKeyboard.add(getClickableNumberKbBtn("0"), 3, 0);
        addProductNumbersKeyboard.add(getClickableShiftBackKbBtn(), 3, 1);
        addProductQTYFiled.setText("0");
    }

    private void initTopicsFontSize() {
        productNameTopicFontSize = TextHelper.getAdaptiveFontSize(addProductNameTopic, 20);
        productQTYTopicFontSize = TextHelper.getAdaptiveFontSize(addProductQTYTypeTopic, 5);
    }

    private void initAdditiveLines() {
        scroll.setFitToWidth(true);
        addProductAdditivesContainer.add(scroll, 0, 1);
    }

    @Override
    public void initialize() {
        initTopicsFontSize();
        initImgBtnsInAddProductPane();
        setPlusMinusProductImgBtnsClickable();
        initTopicsInAddProductPane();
        initNumbersKeyboardInAddProductPane();
        initAdditiveLines();
    }

}
