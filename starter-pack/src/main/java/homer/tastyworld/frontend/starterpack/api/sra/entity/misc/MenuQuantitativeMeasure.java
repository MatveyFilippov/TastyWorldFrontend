package homer.tastyworld.frontend.starterpack.api.sra.entity.misc;


public enum MenuQuantitativeMeasure {

    GRAMS ("гр"), 
    PIECES ("шт");

    public final String shortName;

    MenuQuantitativeMeasure(String shortName) {
        this.shortName = shortName;
    }

}
