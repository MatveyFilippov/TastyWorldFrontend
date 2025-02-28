package homer.tastyworld.frontend.poscreator.panes.dynamic;

import homer.tastyworld.frontend.poscreator.POSCreatorApplication;
import homer.tastyworld.frontend.starterpack.api.Request;
import homer.tastyworld.frontend.starterpack.base.utils.misc.TypeChanger;
import homer.tastyworld.frontend.starterpack.base.utils.ui.helpers.Helper;
import homer.tastyworld.frontend.starterpack.base.utils.ui.helpers.Text;
import javafx.beans.binding.StringExpression;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.apache.hc.core5.http.Method;
import java.math.BigDecimal;
import java.util.Map;

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
    private static StringExpression topicFontSize;
    private static Integer onePiece;
    private static BigDecimal pricePerPiece;

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
        Text.setTextCentre(addProductQTYTypeTopic, priceType, Text.getAdaptiveFontSize(addProductQTYTypeTopic, 6), null);
        pricePerPiece = TypeChanger.toBigDecimal(response.get("PRICE_PEER_PEACE")).divide(BigDecimal.valueOf(onePiece));
        recalculatePrice();
    }

    @Override
    public void cacheAll(Long[] productIDs) {}

    @Override
    public void clean() {
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
            addProductQTYFiled.setText(String.valueOf(qty));
            recalculatePrice();
        });
        addProductPlusQTYImgBtn.setOnMouseClicked(event -> {
            int qty = Integer.parseInt(addProductQTYFiled.getText());
            qty += onePiece;
            addProductQTYFiled.setText(String.valueOf(qty));
            recalculatePrice();
        });
    }

    private void initPriceTopicInAddProductPane() {
        Text.setTextCentre(addProductPriceTopic, "Цена", Text.getAdaptiveFontSize(addProductPriceTopic, 5), null);
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

    @Override
    public void initialize() {
        initTopicFontSize();
        initImgBtnsInAddProductPane();
        setPlusMinusImgBtnsClickable();
        initPriceTopicInAddProductPane();
        initNumbersKeyboardInAddProductPane();
    }

}
