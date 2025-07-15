package homer.tastyworld.frontend.pos.creator.panes.dynamic;

import homer.tastyworld.frontend.pos.creator.POSCreatorApplication;
import homer.tastyworld.frontend.starterpack.base.utils.misc.TypeChanger;
import homer.tastyworld.frontend.starterpack.base.utils.ui.helpers.AdaptiveTextHelper;
import homer.tastyworld.frontend.starterpack.base.utils.ui.helpers.PaneHelper;
import homer.tastyworld.frontend.starterpack.entity.Product;
import homer.tastyworld.frontend.starterpack.entity.ProductAdditive;
import homer.tastyworld.frontend.starterpack.entity.misc.ProductPieceType;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringExpression;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SuperBuilder
public class AddProductParentPane extends DynamicParentPane {

    public static class ProductToAdd {

        public static final Map<Long, Integer> notDefaultAdditives = new ConcurrentHashMap<>();
        public static Product product;
        public static Integer pieceQTY;

        public static void clean() {
            notDefaultAdditives.clear();
            product = null;
            pieceQTY = null;
        }

    }

    private final AnchorPane cancelImgBtn, submitImgBtn;
    private final AnchorPane productNameTopic;
    private final AnchorPane productPriceTopic;
    private final TextField totalPriceField;
    private final AnchorPane minusProductQTYImgBtn, plusProductQTYImgBtn;
    private final AnchorPane productQTYTypeTopic;
    private final TextField productQTYFiled;
    private final GridPane productQTYNumbersKeyboard;
    private final AnchorPane additivesTopic;
    private final GridPane additivesContainer;
    private final ScrollPane scroll = new ScrollPane();
    @Getter(lazy = true, value = AccessLevel.PRIVATE)
    private final Label productNameTopicLabel = AdaptiveTextHelper.setTextCentre(productNameTopic, "", 20, null);
    @Getter(lazy = true, value = AccessLevel.PRIVATE)
    private final Label productQTYTypeTopicLabel = AdaptiveTextHelper.setTextCentre(productQTYTypeTopic, "", 2.5, null);;
    private final URL plusAdditiveImgBtnResource = POSCreatorApplication.class.getResource("images/buttons/AddProductPane/PlusAdditiveQTY.png");
    private final URL minusAdditiveImgBtnResource = POSCreatorApplication.class.getResource("images/buttons/AddProductPane/MinusAdditiveQTY.png");

    @Override
    protected void cacheTask(long productID) {
        ProductAdditive.CACHE.cacheIfAbsent(TypeChanger.toLongArray(Product.get(productID).getAdditiveIDs()));
    }

    @Override
    public void fill(long productID) {
        Product product = Product.get(productID);

        ProductToAdd.product = product;
        ProductToAdd.pieceQTY = product.getDefaultPieceQTY();

        getProductNameTopicLabel().setText(product.getName());
        getProductQTYTypeTopicLabel().setText(product.getPieceType().shortName);
        productQTYFiled.setText(String.valueOf(ProductToAdd.pieceQTY));
        recalculatePrice();

        scroll.setContent(computeAdditivesTable(product));
    }

    private void recalculatePrice() {  // TODO: Possible to cache math calculating
        BigDecimal itemPieceQTY = BigDecimal.valueOf(ProductToAdd.pieceQTY);
        BigDecimal additiveMultiplier = ProductToAdd.product.getPieceType() == ProductPieceType.PIECES ? itemPieceQTY : BigDecimal.ONE;
        BigDecimal total = ProductToAdd.product.getPricePerPiece().multiply(itemPieceQTY);
        for (long additiveID : ProductToAdd.product.getAdditiveIDs()) {
            ProductAdditive additive = ProductAdditive.get(additiveID);
            int additivePieceQTY = ProductToAdd.notDefaultAdditives.getOrDefault(additiveID, additive.getDefaultPieceQTY());
            if (additivePieceQTY > additive.getDefaultPieceQTY()) {
                total = total.add(
                        additive.getPricePerPiece().multiply(BigDecimal.valueOf(additivePieceQTY)).multiply(additiveMultiplier)
                );
            }
        }
        totalPriceField.setText(total.toString());
    }

    private GridPane computeAdditivesTable(Product product) {
        GridPane table = new GridPane();
        table.setVgap(5);
        table.setAlignment(Pos.CENTER);
        long[] additiveIDs = product.getAdditiveIDs();
        for (int i = 0; i < additiveIDs.length; i++) {
            table.add(getAdditiveLine(ProductAdditive.get(additiveIDs[i])), 0, i);
        }
        return table;
    }

    private HBox getAdditiveLine(ProductAdditive additive) {
        HBox row = new HBox(10);
        row.setStyle("-fx-border-color: #000000;");
        row.prefWidthProperty().bind(scroll.widthProperty());
        row.prefHeightProperty().bind(scroll.heightProperty().divide(4));
        row.setAlignment(Pos.CENTER);

        AnchorPane name = new AnchorPane();
        AdaptiveTextHelper.setTextCentre(name, additive.getName(), 8, null);

        TextField pieceQTY = getAdditiveQTY(row, additive);
        AnchorPane minus = new AnchorPane();
        AnchorPane plus = new AnchorPane();
        AnchorPane space = new AnchorPane();

        setPlusMinusAdditiveImgBtnsClickable(plus, minus, pieceQTY, additive);

        row.getChildren().addAll(name, minus, pieceQTY, plus, space);
        HBox.setHgrow(pieceQTY, Priority.ALWAYS);
        HBox.setHgrow(minus, Priority.ALWAYS);
        HBox.setHgrow(name, Priority.ALWAYS);
        HBox.setHgrow(plus, Priority.ALWAYS);
        HBox.setHgrow(space, Priority.ALWAYS);
        name.prefWidthProperty().bind(row.widthProperty().multiply(0.50));
        minus.prefWidthProperty().bind(row.widthProperty().multiply(0.1));
        pieceQTY.prefWidthProperty().bind(row.widthProperty().multiply(0.25));
        plus.prefWidthProperty().bind(row.widthProperty().multiply(0.1));
        space.prefWidthProperty().bind(row.widthProperty().multiply(0.05));

        return row;
    }

    private TextField getAdditiveQTY(HBox row, ProductAdditive additive) {
        TextField pieceQTY = new TextField();
        pieceQTY.setText(String.valueOf(additive.getDefaultPieceQTY()));
        pieceQTY.setAlignment(Pos.CENTER);
        pieceQTY.fontProperty().bind(Bindings.createObjectBinding(
                () -> Font.font(Math.min(row.getWidth() / 10, row.getHeight() / 6)),
                row.widthProperty(), row.heightProperty()
        ));
        pieceQTY.setEditable(false);
        return pieceQTY;
    }

    private void setPlusMinusAdditiveImgBtnsClickable(AnchorPane plus, AnchorPane minus, TextField additiveQTY, ProductAdditive additive) {
        PaneHelper.setImageBackgroundCentre(plus, plusAdditiveImgBtnResource);
        PaneHelper.setImageBackgroundCentre(minus, minusAdditiveImgBtnResource);
        plus.setOnMouseClicked(event -> {
            int pieceQTY = Integer.parseInt(additiveQTY.getText()) + 1;

            if (pieceQTY >= additive.getMaxPieceQTY()) {
                pieceQTY = additive.getMaxPieceQTY();
                plus.setOpacity(0.5);
                plus.setDisable(true);
            }
            minus.setOpacity(1);
            minus.setDisable(false);

            ProductToAdd.notDefaultAdditives.put(additive.getId(), pieceQTY);
            additiveQTY.setText(String.valueOf(pieceQTY));
            recalculatePrice();
        });
        minus.setOnMouseClicked(event -> {
            int pieceQTY = Integer.parseInt(additiveQTY.getText()) - 1;

            if (pieceQTY <= additive.getMinPieceQTY()) {
                pieceQTY = additive.getMinPieceQTY();
                minus.setOpacity(0.5);
                minus.setDisable(true);
            }
            plus.setOpacity(1);
            plus.setDisable(false);

            ProductToAdd.notDefaultAdditives.put(additive.getId(), pieceQTY);
            additiveQTY.setText(String.valueOf(pieceQTY));
            recalculatePrice();
        });
    }

    @Override
    protected void cleanTask() {
        ProductToAdd.clean();
        scroll.setVvalue(0.0);
        getProductQTYTypeTopicLabel().setText("null");
        getProductQTYTypeTopicLabel().setText("");
        productQTYFiled.setText("0");
        totalPriceField.setText("0");
    }

    private void initImgBtnsInAddProductPane() {
        PaneHelper.setImageBackgroundCentre(
                cancelImgBtn,
                POSCreatorApplication.class.getResourceAsStream("images/buttons/AddProductPane/CancelAdding.png")
        );
        PaneHelper.setImageBackgroundCentre(
                submitImgBtn,
                POSCreatorApplication.class.getResourceAsStream("images/buttons/AddProductPane/SubmitAdding.png")
        );
        PaneHelper.setImageBackgroundCentre(
                minusProductQTYImgBtn,
                POSCreatorApplication.class.getResourceAsStream("images/buttons/AddProductPane/MinusProductQTY.png")
        );
        PaneHelper.setImageBackgroundCentre(
                plusProductQTYImgBtn,
                POSCreatorApplication.class.getResourceAsStream("images/buttons/AddProductPane/PlusProductQTY.png")
        );
    }

    private void setPlusMinusProductImgBtnsClickable() {
        plusProductQTYImgBtn.setOnMouseClicked(event -> {
            ProductToAdd.pieceQTY = Integer.parseInt(productQTYFiled.getText()) + 1;

            if (ProductToAdd.pieceQTY >= ProductToAdd.product.getMaxPieceQTY()) {
                ProductToAdd.pieceQTY = ProductToAdd.product.getMaxPieceQTY();
                plusProductQTYImgBtn.setOpacity(0.5);
                plusProductQTYImgBtn.setDisable(true);
            }
            minusProductQTYImgBtn.setOpacity(1);
            minusProductQTYImgBtn.setDisable(false);

            productQTYFiled.setText(String.valueOf(ProductToAdd.pieceQTY));
            recalculatePrice();
        });
        minusProductQTYImgBtn.setOnMouseClicked(event -> {
            ProductToAdd.pieceQTY = Integer.parseInt(productQTYFiled.getText()) - 1;

            if (ProductToAdd.pieceQTY <= ProductToAdd.product.getMinPieceQTY()) {
                ProductToAdd.pieceQTY = ProductToAdd.product.getMinPieceQTY();
                minusProductQTYImgBtn.setOpacity(0.5);
                minusProductQTYImgBtn.setDisable(true);
            }
            plusProductQTYImgBtn.setOpacity(1);
            plusProductQTYImgBtn.setDisable(false);

            productQTYFiled.setText(String.valueOf(ProductToAdd.pieceQTY));
            recalculatePrice();
        });
    }

    private void initTopicsInAddProductPane() {
        AdaptiveTextHelper.setTextCentre(productPriceTopic, "Цена", 5, null);
        AdaptiveTextHelper.setTextCentre(additivesTopic, "Добавки", 15, null);
    }

    private AnchorPane getClickableNumberKbBtn(int num, StringExpression fontSize) {
        String toAppend = String.valueOf(num);
        AnchorPane btn = new AnchorPane();
        btn.setStyle("-fx-border-color: #555555; -fx-background-color: #FFFFFF; -fx-background-radius: 25; -fx-border-radius: 25");
        AdaptiveTextHelper.setTextCentre(btn, toAppend, fontSize, Color.BLACK);
        btn.setOnMouseClicked(event -> {
            String oldQTY = productQTYFiled.getText().replace(" ", "");
            String newQTY = oldQTY.equals("0") ? toAppend : oldQTY + toAppend;
            ProductToAdd.pieceQTY = Integer.parseInt(newQTY);
            productQTYFiled.setText(newQTY);
            recalculatePrice();
        });
        return btn;
    }

    private AnchorPane getClickableShiftBackKbBtn() {
        AnchorPane btn = new AnchorPane();
        btn.setStyle("-fx-border-color: #555555; -fx-background-color: #FFFFFF; -fx-background-radius: 25; -fx-border-radius: 25");
        AdaptiveTextHelper.setTextCentre(btn, "<--", 3, Color.BLACK);
        btn.setOnMouseClicked(event -> {
            String oldQTY = productQTYFiled.getText().replace(" ", "");
            int len = oldQTY.length();
            ProductToAdd.pieceQTY = len <= 1 ? 0 : Integer.parseInt(oldQTY.substring(0, len - 1));
            productQTYFiled.setText(String.valueOf(ProductToAdd.pieceQTY));
            recalculatePrice();
        });
        return btn;
    }

    private void initNumbersKeyboardInAddProductPane() {
        AnchorPane shiftBackBtn = getClickableShiftBackKbBtn();
        StringExpression numbersFontSize = AdaptiveTextHelper.getFontSize(shiftBackBtn, 3);
        for (int i = 0; i < 9; i++) {
            productQTYNumbersKeyboard.add(getClickableNumberKbBtn(i + 1, numbersFontSize), i % 3, i / 3);
        }
        productQTYNumbersKeyboard.add(getClickableNumberKbBtn(0, numbersFontSize), 3, 0);
        productQTYNumbersKeyboard.add(shiftBackBtn, 3, 1);
        productQTYFiled.setText("0");
    }

    private void initAdditiveLines() {
        scroll.setFitToWidth(true);
        additivesContainer.add(scroll, 0, 1);
    }

    @Override
    public void initialize() {
        initImgBtnsInAddProductPane();
        setPlusMinusProductImgBtnsClickable();
        initTopicsInAddProductPane();
        initNumbersKeyboardInAddProductPane();
        initAdditiveLines();
    }

}
