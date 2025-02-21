module homer.tastyworld.frontend.starterpack {
    requires javafx.controls;
    requires org.apache.httpcomponents.core5.httpcore5;
    requires org.apache.httpcomponents.client5.httpclient5;
    requires com.fasterxml.jackson.databind;
    requires org.slf4j;
    requires org.apache.logging.log4j.core;
    requires org.apache.logging.log4j;
    requires com.rabbitmq.client;
    requires java.sql;

    exports homer.tastyworld.frontend.starterpack.api;
    exports homer.tastyworld.frontend.starterpack.api.notifications;
    exports homer.tastyworld.frontend.starterpack.api.photo;
    exports homer.tastyworld.frontend.starterpack.exceptions;
    exports homer.tastyworld.frontend.starterpack.ui;
    exports homer.tastyworld.frontend.starterpack.utils;
    exports homer.tastyworld.frontend.starterpack.utils.config;
    exports homer.tastyworld.frontend.starterpack.utils.misc;
}