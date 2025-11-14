package homer.tastyworld.frontend.pos.creator.panes.implementations;

import homer.tastyworld.frontend.pos.creator.POSCreatorApplication;
import homer.tastyworld.frontend.pos.creator.panes.ParentPane;
import homer.tastyworld.frontend.starterpack.api.sra.entity.menu.MenuCategory;
import homer.tastyworld.frontend.starterpack.api.sra.entity.menu.MenuUtils;
import homer.tastyworld.frontend.starterpack.api.sra.entity.menu.Product;
import homer.tastyworld.frontend.starterpack.api.sra.entity.menu.ProductTopping;
import homer.tastyworld.frontend.starterpack.api.sra.entity.misc.MenuQuantitativeMeasure;
import homer.tastyworld.frontend.starterpack.utils.ui.helpers.AdaptiveTextHelper;
import homer.tastyworld.frontend.starterpack.utils.ui.helpers.PaneHelper;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;

@SuperBuilder
public class ProductParentPane extends ParentPane<MenuCategoryProductsParentPane.CategoryProduct> {

    private final AnchorPane goBackInMenuCategoryProductsImgBtn, addProductToOrderImgBtn;
    private final AnchorPane nameTopic;
    private final AnchorPane priceTopic;
    private final AnchorPane calculatedPriceTopic;
    private final AnchorPane minusQuantityImgBtn, plusQuantityImgBtn;
    private final AnchorPane productQuantityTopic;
    private final GridPane numbersKeyboardGrid;
    private final AnchorPane productToppingsTopic;
    private final ScrollPane productToppingsScroll;

    private Label nameLabel, calculatedPriceLabel, productQuantityLabel;
    private final URL plusQuantityImgBtnResource = POSCreatorApplication.class.getResource("images/buttons/ProductPane/PlusQuantity.png");
    private final URL minusQuantityImgBtnResource = POSCreatorApplication.class.getResource("images/buttons/ProductPane/MinusQuantity.png");

    private Product product;
    private final ObservableMap<ProductTopping, Integer> productNotDefaultProductToppingsQTY = FXCollections.observableHashMap();
    private final IntegerProperty productQuantity = new SimpleIntegerProperty(-1);
    private final ObjectProperty<BigDecimal> totalPrice = new SimpleObjectProperty<>(BigDecimal.ZERO);
    private MenuCategory productCategory;

    public Product getProduct() {
        if (product == null) {
            throw new NullPointerException("The product being opened does not exist");
        }
        return product;
    }

    public int getProductQuantity() {
        int result = productQuantity.get();
        if (product == null || result < 0) {
            throw new NullPointerException("The quantity of product being opened does not exist");
        }
        return result;
    }

    public Map<ProductTopping, Integer> getProductNotDefaultProductToppingsQTY() {
        if (product == null) {
            throw new NullPointerException("Not default topping of product being opened does not exist");
        }
        return Map.copyOf(productNotDefaultProductToppingsQTY);
    }

    public MenuCategory getProductCategory() {
        if (product == null || productCategory == null) {
            throw new NullPointerException("The menu category of product being opened does not exist");
        }
        return productCategory;
    }

    private VBox computeProductToppingsRows(Product product) {  // TODO: cache it
        VBox rows = new VBox(5);
        rows.setFillWidth(true);
        rows.setAlignment(Pos.CENTER);

        rows.getChildren().addAll(
                Arrays.stream(MenuUtils.getAllProductToppings(product.getProductID(), true))
                      .map(this::getProductToppingLine)
                      .toList()
        );

        return rows;
    }

    private HBox getProductToppingLine(ProductTopping productTopping) {
        HBox row = new HBox(5);
        row.getStyleClass().add("product-topping-line");
        row.prefWidthProperty().bind(productToppingsScroll.widthProperty());
        row.prefHeightProperty().bind(productToppingsScroll.heightProperty().multiply(0.2));
        row.setAlignment(Pos.CENTER);

        AnchorPane nameWithPrice = new AnchorPane();
        AdaptiveTextHelper.setTextCentre(
                nameWithPrice, "%s (%s р)".formatted(productTopping.getName(), productTopping.getUnitPrice()), 0.08, null
        );

        AnchorPane productToppingQuantityTopic = new AnchorPane();
        productToppingQuantityTopic.getStyleClass().add("anchor-pane-as-text-field");
        Label productToppingQuantityLabel = AdaptiveTextHelper.setTextCentre(
                productToppingQuantityTopic, productTopping.getQtyDefault() + " " + productTopping.getQtyMeasure().shortName, 0.17, null
        );
        AnchorPane minusProductToppingQuantityImgBtn = new AnchorPane();
        AnchorPane plusProductToppingQuantityImgBtn = new AnchorPane();
        setPlusMinusProductToppingQuantityImgBtnsClickable(
                plusProductToppingQuantityImgBtn, minusProductToppingQuantityImgBtn, productToppingQuantityLabel, productTopping
        );

        AnchorPane space = new AnchorPane();

        row.getChildren().addAll(
                nameWithPrice, minusProductToppingQuantityImgBtn, productToppingQuantityTopic, plusProductToppingQuantityImgBtn, space
        );
        HBox.setHgrow(productToppingQuantityTopic, Priority.ALWAYS);
        HBox.setHgrow(minusProductToppingQuantityImgBtn, Priority.ALWAYS);
        HBox.setHgrow(nameWithPrice, Priority.ALWAYS);
        HBox.setHgrow(plusProductToppingQuantityImgBtn, Priority.ALWAYS);
        HBox.setHgrow(space, Priority.ALWAYS);
        nameWithPrice.prefWidthProperty().bind(row.widthProperty().multiply(0.50));
        minusProductToppingQuantityImgBtn.prefWidthProperty().bind(row.widthProperty().multiply(0.1));
        productToppingQuantityTopic.prefWidthProperty().bind(row.widthProperty().multiply(0.25));
        plusProductToppingQuantityImgBtn.prefWidthProperty().bind(row.widthProperty().multiply(0.1));
        space.prefWidthProperty().bind(row.widthProperty().multiply(0.05));

        return row;
    }

    private void setPlusMinusProductToppingQuantityImgBtnsClickable(AnchorPane plus, AnchorPane minus, Label productToppingQuantityLabel, ProductTopping productTopping) {
        plus.getStyleClass().add("anchor-pane-as-button");
        minus.getStyleClass().add("anchor-pane-as-button");
        PaneHelper.setImageBackgroundCentre(plus, plusQuantityImgBtnResource);
        PaneHelper.setImageBackgroundCentre(minus, minusQuantityImgBtnResource);
        plus.setOnMouseClicked(event -> {
            int newQTY = productNotDefaultProductToppingsQTY.getOrDefault(productTopping, productTopping.getQtyDefault()) + 1;

            plus.setDisable(newQTY >= productTopping.getQtyMax());
            minus.setDisable(newQTY <= productTopping.getQtyMin());

            productNotDefaultProductToppingsQTY.put(productTopping, newQTY);
            productToppingQuantityLabel.setText(newQTY + " " + productTopping.getQtyMeasure().shortName);
        });
        minus.setOnMouseClicked(event -> {
            int newQTY = productNotDefaultProductToppingsQTY.getOrDefault(productTopping, productTopping.getQtyDefault()) - 1;

            plus.setDisable(newQTY >= productTopping.getQtyMax());
            minus.setDisable(newQTY <= productTopping.getQtyMin());

            productNotDefaultProductToppingsQTY.put(productTopping, newQTY);
            productToppingQuantityLabel.setText(newQTY + " " + productTopping.getQtyMeasure().shortName);
        });
        plus.setDisable(productTopping.getQtyDefault() >= productTopping.getQtyMax());
        minus.setDisable(productTopping.getQtyDefault() <= productTopping.getQtyMin());
    }

    private void initImgBtns() {
        PaneHelper.setImageBackgroundCentre(
                goBackInMenuCategoryProductsImgBtn,
                POSCreatorApplication.class.getResourceAsStream("images/buttons/ProductPane/BackInMenuCategoryProducts.png")
        );
        PaneHelper.setImageBackgroundCentre(
                addProductToOrderImgBtn,
                POSCreatorApplication.class.getResourceAsStream("images/buttons/ProductPane/AddProductToOrder.png")
        );
        PaneHelper.setImageBackgroundCentre(
                plusQuantityImgBtn,
                plusQuantityImgBtnResource
        );
        PaneHelper.setImageBackgroundCentre(
                minusQuantityImgBtn,
                minusQuantityImgBtnResource
        );
    }

    private void resetTotalPrice() {
        BigDecimal productQTY = BigDecimal.valueOf(productQuantity.get());
        BigDecimal total = product.getUnitPrice().multiply(productQTY);
        BigDecimal productToppingMultiplier = product.getQtyMeasure() == MenuQuantitativeMeasure.PIECES
                                              ? productQTY : BigDecimal.ONE;
        for (Map.Entry<ProductTopping, Integer> productToppingQTY : productNotDefaultProductToppingsQTY.entrySet()) {
            ProductTopping productTopping = productToppingQTY.getKey();
            int productToppingActualQTY = productToppingQTY.getValue();
            if (productToppingActualQTY > productTopping.getQtyDefault()) {
                BigDecimal productToppingOverQTY = BigDecimal.valueOf(productToppingActualQTY - productTopping.getQtyDefault());
                BigDecimal modifierTotal = productTopping.getUnitPrice().multiply(productToppingOverQTY);
                total = total.add(modifierTotal.multiply(productToppingMultiplier));
            }
        }
        totalPrice.set(total);
    }

    private void initProductQuantityUpdateListeners() {
        productQuantity.addListener((observable, oldValue, newValue) -> {
            int newQTY = newValue.intValue();
            plusQuantityImgBtn.setDisable(newQTY >= product.getQtyMax());
            minusQuantityImgBtn.setDisable(newQTY <= product.getQtyMin());

            productQuantityLabel.setText(newValue + " " + product.getQtyMeasure().shortName);

            resetTotalPrice();
        });
    }

    private void initProductNotDefaultProductToppingsQTYUpdateListeners() {
        productNotDefaultProductToppingsQTY.addListener((MapChangeListener<? super ProductTopping, ? super Integer>) (observable) -> resetTotalPrice());
    }

    private void initTotalPriceUpdateListeners() {
        totalPrice.addListener(
                (observable, oldValue, newValue) -> calculatedPriceLabel.setText(newValue + " р")
        );
    }

    private void initPlusMinusQuantityImgBtnsClickable() {
        plusQuantityImgBtn.setOnMouseClicked(e -> productQuantity.set(productQuantity.get() + 1));
        minusQuantityImgBtn.setOnMouseClicked(e -> productQuantity.set(productQuantity.get() - 1));
    }

    private void initTopicsLabel() {
        AdaptiveTextHelper.setTextCentre(priceTopic, "Цена", 0.2, null);
        AdaptiveTextHelper.setTextCentre(productToppingsTopic, "Добавки", 0.065, null);
        nameLabel = AdaptiveTextHelper.setTextCentre(nameTopic, "", 0.05, null);
        calculatedPriceLabel = AdaptiveTextHelper.setTextCentre(calculatedPriceTopic, "", 0.15, null);
        productQuantityLabel = AdaptiveTextHelper.setTextCentre(productQuantityTopic, "", 0.15, null);
    }

    private AnchorPane getClickableNumberKbBtn(int num, StringExpression fontSize) {
        AnchorPane btn = new AnchorPane();
        btn.getStyleClass().add("numpad-button");
        String toAppend = String.valueOf(num);
        AdaptiveTextHelper.setTextCentre(btn, toAppend, fontSize, Color.BLACK);
        btn.setOnMouseClicked(event -> {
            String oldQTY = String.valueOf(productQuantity.get());
            String newQTY = oldQTY.equals("0") ? toAppend : oldQTY + toAppend;
            productQuantity.set(Integer.parseInt(newQTY));
        });
        return btn;
    }

    private AnchorPane getClickableShiftBackNumberKbBtn() {
        AnchorPane btn = new AnchorPane();
        btn.getStyleClass().add("numpad-button");
        AdaptiveTextHelper.setTextCentre(btn, "<--", 0.33, Color.BLACK);
        btn.setOnMouseClicked(event -> {
            String oldQTY = String.valueOf(productQuantity.get());
            int len = oldQTY.length();
            productQuantity.set(len <= 1 ? 0 : Integer.parseInt(oldQTY.substring(0, len - 1)));
        });
        return btn;
    }

    private void initNumbersKeyboard() {
        AnchorPane shiftBackBtn = getClickableShiftBackNumberKbBtn();
        StringExpression numbersFontSize = AdaptiveTextHelper.getFontSize(shiftBackBtn, 0.33);
        for (int i = 0; i < 9; i++) {
            numbersKeyboardGrid.add(getClickableNumberKbBtn(i + 1, numbersFontSize), i % 3, i / 3);
        }
        numbersKeyboardGrid.add(getClickableNumberKbBtn(0, numbersFontSize), 3, 0);
        numbersKeyboardGrid.add(shiftBackBtn, 3, 1);
    }

    @Override
    public void initialize() {
        initImgBtns();
        initProductQuantityUpdateListeners();
        initProductNotDefaultProductToppingsQTYUpdateListeners();
        initTotalPriceUpdateListeners();
        initPlusMinusQuantityImgBtnsClickable();
        initTopicsLabel();
        initNumbersKeyboard();
    }

    @Override
    protected void beforeOpen(MenuCategoryProductsParentPane.CategoryProduct categoryProduct) {
        this.product = categoryProduct.product();
        this.productCategory = categoryProduct.category();
        this.productQuantity.set(product.getQtyDefault());

        nameLabel.setText(product.getName());

        productToppingsScroll.setContent(computeProductToppingsRows(product));
    }

    @Override
    protected void beforeClose() {
        this.productNotDefaultProductToppingsQTY.clear();
        this.productQuantity.set(-1);
        this.productCategory = null;
        this.product = null;

        nameLabel.setText("");

        productToppingsScroll.setVvalue(0.0);
        productToppingsScroll.setContent(null);
    }

}
