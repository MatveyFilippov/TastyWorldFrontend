module homer.tastyworld.frontend.statusdisplay {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires homer.tastyworld.frontend.starterpack;

    opens homer.tastyworld.frontend.statusdisplay to javafx.fxml;
    opens homer.tastyworld.frontend.statusdisplay.core to javafx.fxml;

    exports homer.tastyworld.frontend.statusdisplay;
    exports homer.tastyworld.frontend.statusdisplay.core;
}