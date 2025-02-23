package homer.tastyworld.frontend.starterpack.base.config;

import homer.tastyworld.frontend.starterpack.base.exceptions.starterpackonly.unexpected.CantInitAppConfigException;
import homer.tastyworld.frontend.starterpack.base.utils.ui.DialogWindow;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class AppConfig {

    private static final String TW_SERVER_IP = "tastyworld-pos.ru";
    public static final String API_URL = "http://" + TW_SERVER_IP + "/api";
    public static final String NOTIFICATION_HOST = TW_SERVER_IP;
    public static final int NOTIFICATION_PORT = 5672;
    public static final String NOTIFICATION_VIRTUAL_HOST = "playground";
    public static final File APP_DATA_DIR = new File(System.getProperty("user.home"), ".TastyWorld");
    private static final PropertiesProcessor properties;
    private static String token, appName, appVersion;

    static {
        APP_DATA_DIR.mkdirs();
        properties = new PropertiesProcessor(APP_DATA_DIR.getAbsolutePath() + File.separator + "twapp.properties");
    }

    public static void init(Class<?> appClass) {
        InputStream inputStream = appClass.getClassLoader().getResourceAsStream("twapp.properties");
        if (inputStream == null) {
            throw new CantInitAppConfigException("Can't read app properties from resources");
        }
        PropertiesProcessor properties = new PropertiesProcessor(inputStream);
        try {
            inputStream.close();
        } catch (IOException ignored) {}
        appName = properties.getValue("app.name");
        appVersion = properties.getValue("app.version");
        if (appName == null || appName.replace(" ", "").isEmpty()) {
            throw new CantInitAppConfigException("Can't find property 'app.name'");
        }
        if (appVersion == null || appVersion.replace(" ", "").isEmpty()) {
            throw new CantInitAppConfigException("Can't find property 'app.version'");
        }
    }

    public static String getToken() {
        if (token == null) {
            token = properties.getValue("api.token");
            if (token == null) {
                setToken(DialogWindow.askTillGood(
                        "TastyWorld API token",
                        "Прежде чем пользовать программой введите токен доступа",
                        "Токен:",
                        token -> token != null && !token.replace(" ", "").isEmpty()
                ));
            }
        }
        return token;
    }

    public static void setToken(String newToken) {
        if (newToken == null) {
            properties.deleteValue("api.token");
        } else {
            properties.setValue("api.token", newToken);
        }
        token = newToken;
    }

    public static String getAppName() {
        return appName;
    }

    public static String getAppVersion() {
        return appVersion;
    }

}
