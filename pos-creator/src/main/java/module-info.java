module homer.tastyworld.frontend.pos.creator {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires homer.tastyworld.frontend.starterpack;
    requires static lombok;

    opens homer.tastyworld.frontend.pos.creator to javafx.fxml;
    exports homer.tastyworld.frontend.pos.creator;
}