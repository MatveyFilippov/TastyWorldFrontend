package homer.tastyworld.frontend.starterpack.base.config;

enum ConfigKey {

    /* Internal (resources) */

    // Required
    APP_IDENTIFIER_NAME("app.identifier_name"),
    APP_VERSION ("app.version"),

    // Not required
    APP_TITLE ("app.tittle"),
    APP_CACHE_AVAILABLE ("app.cache.available"),


    /* External (properties) */

    // Required
    API_TOKEN ("api.token"),

    // Not required
    APP_DATETIME_ZONE_ID ("app.datetime_zone.id"),
    PRINTER_NAME ("printer.name"),
    SCALE_COM_PORT("scale.com_port");

    final String propertiesKey;

    ConfigKey(String propertiesKey) {
        this.propertiesKey = propertiesKey;
    }

}
