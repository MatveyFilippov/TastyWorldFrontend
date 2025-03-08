module homer.tastyworld.frontend.starterpack {
    requires javafx.controls;
    requires org.apache.httpcomponents.core5.httpcore5;
    requires org.apache.httpcomponents.client5.httpclient5;
    requires com.fasterxml.jackson.databind;
    requires org.slf4j;
    requires com.rabbitmq.client;
    requires java.sql;
    requires java.desktop;

    exports homer.tastyworld.frontend.starterpack;
    exports homer.tastyworld.frontend.starterpack.api;
    exports homer.tastyworld.frontend.starterpack.api.notifications;
    exports homer.tastyworld.frontend.starterpack.api.requests;
    exports homer.tastyworld.frontend.starterpack.base;
    exports homer.tastyworld.frontend.starterpack.base.config;
    exports homer.tastyworld.frontend.starterpack.base.exceptions;
    exports homer.tastyworld.frontend.starterpack.base.exceptions.response;
    exports homer.tastyworld.frontend.starterpack.base.utils.managers.tablemanager;
    exports homer.tastyworld.frontend.starterpack.base.utils.managers.tablemanager.cursors;
    exports homer.tastyworld.frontend.starterpack.base.utils.misc;
    exports homer.tastyworld.frontend.starterpack.base.utils.ui;
    exports homer.tastyworld.frontend.starterpack.base.utils.ui.helpers;
}