module homer.tastyworld.frontend.statusdisplay {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires homer.tastyworld.frontend.starterpack;
    requires org.apache.httpcomponents.core5.httpcore5;


    opens homer.tastyworld.frontend.statusdisplay to javafx.fxml;
    exports homer.tastyworld.frontend.statusdisplay;
    exports homer.tastyworld.frontend.statusdisplay.base;
    opens homer.tastyworld.frontend.statusdisplay.base to javafx.fxml;
}