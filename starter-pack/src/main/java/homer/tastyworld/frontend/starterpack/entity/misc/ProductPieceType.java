package homer.tastyworld.frontend.starterpack.entity.misc;

/**
 * It must be enum class, but if enum contains constructor - I faced with problems to use it in external modules
 * public enum ProductPieceType {
 *     GRAMS ("гр"),
 *     PIECES ("шт");
 *     public final String shortName;
 *     ProductPieceType(String shortName) {
 *         this.shortName = shortName;
 *     }
 * }
 */
public class ProductPieceType {

    public static final ProductPieceType GRAMS = new ProductPieceType("гр");
    public static final ProductPieceType PIECES = new ProductPieceType("шт");

    public final String shortName;

    ProductPieceType(String shortName) {
        this.shortName = shortName;
    }

    public static ProductPieceType valueOf(String name) {
        return switch (name) {
            case "GRAMS" -> GRAMS;
            case "PIECES" -> PIECES;
            default -> throw new IllegalArgumentException(
                    "No enum constant " + ProductPieceType.class.getName() + "." + name
            );
        };
    }

}
