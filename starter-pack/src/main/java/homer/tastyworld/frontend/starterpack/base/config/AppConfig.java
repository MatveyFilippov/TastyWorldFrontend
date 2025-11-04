package homer.tastyworld.frontend.starterpack.base.config;

import homer.tastyworld.frontend.starterpack.base.exceptions.starterpackonly.initialization.CantInitAppConfigException;
import homer.tastyworld.frontend.starterpack.utils.misc.FileDirectories;
import homer.tastyworld.frontend.starterpack.utils.ui.DialogWindows;
import javafx.application.Platform;
import org.jetbrains.annotations.Nullable;
import java.io.File;
import java.io.InputStream;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

public class AppConfig {

    public static final String TW_SRA_URL = "http://tastyworld-pos.ru:1212/api/v1";  // TastyWorld Service-REST-API
    public static final String TW_MN_HOST = "tastyworld-pos.ru";  // TastyWorld Microservice-Notifier
    public static final int TW_MN_PORT = 5672;  // TastyWorld Microservice-Notifier
    public static final String TW_MN_VHOST = "playground";  // TastyWorld Microservice-Notifier
    public static final String EVOTOR_MC_API_URL = "https://mobcashier.evotor.ru/api/v1";  // Evotor Mobile-Cashier
    public static final File APP_DATA_DIR = new File(System.getProperty("user.home"), ".TastyWorld");
    private static String appIdentifierName, appVersion;
    private static PropertiesProcessor internalProperties;
    private static final PropertiesProcessor externalProperties;

    static {
        FileDirectories.createDir(APP_DATA_DIR);
        externalProperties = new PropertiesProcessor(APP_DATA_DIR.getAbsolutePath() + File.separator + "twapp.properties");
    }

    public static void init(Class<?> appClass) {
        InputStream internalPropertiesStream = appClass.getClassLoader().getResourceAsStream("twapp.properties");
        if (internalPropertiesStream == null) {
            throw new CantInitAppConfigException("Can't read internal app properties from resources");
        }
        internalProperties = new PropertiesProcessor(internalPropertiesStream);

        appIdentifierName = internalProperties.getValue(ConfigKey.APP_IDENTIFIER_NAME).orElseThrow(
                () -> new CantInitAppConfigException("Can't find required internal app property '" + ConfigKey.APP_IDENTIFIER_NAME.propertiesKey + "'")
        );
        appVersion = internalProperties.getValue(ConfigKey.APP_VERSION).orElseThrow(
                () -> new CantInitAppConfigException("Can't find required internal app property '" + ConfigKey.APP_VERSION.propertiesKey + "'")
        );
    }

    public static void setAuthorizationToken(String newAuthorizationToken) {
        if (newAuthorizationToken == null) {
            externalProperties.deleteValue(ConfigKey.SRA_AUTHORIZATION_TOKEN);
        } else {
            externalProperties.setValue(ConfigKey.SRA_AUTHORIZATION_TOKEN, newAuthorizationToken);
        }
    }

    public static String getAuthorizationTokenSRA() {
        return externalProperties.getValue(ConfigKey.SRA_AUTHORIZATION_TOKEN).orElseGet(() -> {
            Optional<String> probablyAuthorizationToken = DialogWindows.get(
                    "TastyWorld Authorization Token",
                    "Прежде чем пользовать программой введите токен авторизации",
                    "Токен:"
            );
            if (probablyAuthorizationToken.isEmpty()) {
                Platform.exit();
                System.exit(0);
            }
            String newAuthorizationToken = probablyAuthorizationToken.get();
            setAuthorizationToken(newAuthorizationToken);
            return newAuthorizationToken;
        });
    }

    public static String getAppIdentifierName() {
        return appIdentifierName;
    }

    public static String getAppVersion() {
        return appVersion;
    }

    public static String getAppTitle() {
        return internalProperties.getValue(ConfigKey.APP_TITLE).orElse("TastyWorld");
    }

    public static boolean isAppCacheAvailable() {
        return externalProperties.getValue(ConfigKey.APP_CACHE_AVAILABLE)
                                 .map(Boolean::parseBoolean)
                                 .orElse(true);
    }

    public static ZoneOffset getAppTimeZoneOffset() {
        return externalProperties.getValue(ConfigKey.APP_TIMEZONE_OFFSET)
                                 .map(ZoneOffset::of)
                                 .orElseGet(() -> OffsetDateTime.now().getOffset());
    }

    public static boolean isSoundUnavailable() {
        return externalProperties.getValue(ConfigKey.SOUND_UNAVAILABLE)
                                 .map(Boolean::parseBoolean)
                                 .orElse(false);
    }

    @Nullable
    public static String getPrinterName() {
        return externalProperties.getValue(ConfigKey.PRINTER_NAME).orElse(null);
    }

    @Nullable
    public static String getScaleComPort() {
        return externalProperties.getValue(ConfigKey.SCALE_COM_PORT).orElse(null);
    }

    @Nullable
    public static String getEvotorAccountPhone() {
        return externalProperties.getValue(ConfigKey.EVOTOR_ACCOUNT_PHONE).orElse(null);
    }

    @Nullable
    public static String getEvotorMobcashierIdentifierSecret() {
        return externalProperties.getValue(ConfigKey.EVOTOR_MOBCASHIER_IDENTIFIER_SECRET).orElse(null);
    }

    public static String getEvotorMobcashierFiscalizationEmail() {
        return externalProperties.getValue(ConfigKey.EVOTOR_MOBCASHIER_FISCALIZATION_EMAIL).orElse("mr.tastyworld@mail.ru");
    }

}
