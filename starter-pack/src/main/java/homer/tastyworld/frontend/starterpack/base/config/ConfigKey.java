package homer.tastyworld.frontend.starterpack.base.config;

enum ConfigKey {

    // TastyWorld API
    API_TOKEN ("api.token"),

    // Local Application
    APP_NAME ("app.name"),
    APP_VERSION ("app.version"),
    APP_DATETIME_ZONE_ID ("app.datetime_zone_id"),

    // Local External
    PRINTER_NAME ("printer.name"),
    SCALE_COM_PORT("scale.com_port");

    public final String propertiesKey;

    ConfigKey(String propertiesKey) {
        this.propertiesKey = propertiesKey;
    }

}
