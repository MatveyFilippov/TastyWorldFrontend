package homer.tastyworld.frontend.starterpack.base.config;

enum ConfigKey {

    // TastyWorld API
    API_TOKEN ("api.token"),

    // Local Application
    APP_NAME ("app.name"),
    APP_VERSION ("app.version"),
    APP_DATETIME_ZONE_ID ("app.datetime_zone_id");


    public final String key;

    ConfigKey(String key) {
        this.key = key;
    }

}
