module homer.tastyworld.frontend.pos.processor {
    requires javafx.controls;
    requires javafx.fxml;
    requires homer.tastyworld.frontend.starterpack;


    opens homer.tastyworld.frontend.pos.processor to javafx.fxml;
    exports homer.tastyworld.frontend.pos.processor;
}