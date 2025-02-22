module homer.tastyworld.frontend.poscreator {
    requires javafx.controls;
    requires javafx.fxml;
    requires homer.tastyworld.frontend.starterpack;


    opens homer.tastyworld.frontend.poscreator to javafx.fxml;
    exports homer.tastyworld.frontend.poscreator;
}