package homer.tastyworld.frontend.starterpack.base.config;

enum ConfigKey {

    /* Internal (resources) */

    // Required
    APP_IDENTIFIER_NAME("app.identifier.name"),
    APP_VERSION ("app.version"),

    // Not required
    APP_TITLE ("app.tittle"),


    /* External (properties) */

    // Required
    SRA_AUTHORIZATION_TOKEN("sra.authorization.token"),

    // Not required
    APP_CACHE_AVAILABLE ("app.cache.available"),
    APP_TIMEZONE_OFFSET("app.timezone.offset"),
    SOUND_UNAVAILABLE("external.sound.unavailable"),
    PRINTER_NAME ("external.printer.name"),
    SCALE_COM_PORT("external.scale.com_port"),
    EVOTOR_ACCOUNT_PHONE("external.evotor.account_phone"),
    EVOTOR_MOBCASHIER_IDENTIFIER_SECRET("external.evotor.mobcashier.identifier_secret"),
    EVOTOR_MOBCASHIER_FISCALIZATION_EMAIL("external.evotor.mobcashier.fiscalization_email");

    final String propertiesKey;

    ConfigKey(String propertiesKey) {
        this.propertiesKey = propertiesKey;
    }

}
