module homer.tastyworld.frontend.pos.creator {
    requires javafx.controls;
    requires javafx.fxml;
    requires homer.tastyworld.frontend.starterpack;
    requires org.apache.httpcomponents.core5.httpcore5;
    requires static lombok;


    opens homer.tastyworld.frontend.pos.creator to javafx.fxml;
    exports homer.tastyworld.frontend.pos.creator;
}