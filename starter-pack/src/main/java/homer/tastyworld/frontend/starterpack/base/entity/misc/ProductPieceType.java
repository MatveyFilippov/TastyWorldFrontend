package homer.tastyworld.frontend.starterpack.base.entity.misc;

import java.math.BigDecimal;

/**
 * It must be enum class, but if enum contains constructor - I faced with problems to use it in external modules
 * public enum ProductPieceType {
 *
 *     ONE_HUNDRED_GRAMS (BigDecimal.valueOf(100), "гр"),
 *     ONE_PIECE (BigDecimal.ONE, "шт");
 *
 *     public final BigDecimal step;
 *     public final String shortName;
 *
 *     ProductPieceType(BigDecimal step, String shortName) {
 *         this.step = step;
 *         this.shortName = shortName;
 *     }
 *
 * }
 */
public class ProductPieceType {

    public static final ProductPieceType ONE_HUNDRED_GRAMS = new ProductPieceType(BigDecimal.valueOf(100), "гр");
    public static final ProductPieceType ONE_PIECE = new ProductPieceType(BigDecimal.ONE, "шт");

    public final BigDecimal step;
    public final String shortName;

    ProductPieceType(BigDecimal step, String shortName) {
        this.step = step;
        this.shortName = shortName;
    }

    public static ProductPieceType valueOf(String name) {
        switch (name) {
            case "ONE_HUNDRED_GRAMS": return ONE_HUNDRED_GRAMS;
            case "ONE_PIECE": return ONE_PIECE;
        }
        throw new IllegalArgumentException("No enum constant " + ProductPieceType.class.getName() + "." + name);
    }

}
