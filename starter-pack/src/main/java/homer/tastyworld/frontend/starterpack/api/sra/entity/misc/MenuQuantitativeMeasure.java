package homer.tastyworld.frontend.starterpack.api.sra.entity.misc;

/**
 * It must be enum class, but if enum contains constructor - I faced with problems to use it in external modules
 * public enum MenuQuantitativeMeasure {
 *     GRAMS ("гр"),
 *     PIECES ("шт");
 *     public final String shortName;
 *     MenuQuantitativeMeasure(String shortName) {
 *         this.shortName = shortName;
 *     }
 * }
 */
public class MenuQuantitativeMeasure {

    public static final MenuQuantitativeMeasure GRAMS = new MenuQuantitativeMeasure("гр");
    public static final MenuQuantitativeMeasure PIECES = new MenuQuantitativeMeasure("шт");

    public final String shortName;

    MenuQuantitativeMeasure(String shortName) {
        this.shortName = shortName;
    }

    public static MenuQuantitativeMeasure valueOf(String name) {
        return switch (name) {
            case "GRAMS" -> GRAMS;
            case "PIECES" -> PIECES;
            default -> throw new IllegalArgumentException(
                    "No enum constant " + MenuQuantitativeMeasure.class.getName() + "." + name
            );
        };
    }

}
