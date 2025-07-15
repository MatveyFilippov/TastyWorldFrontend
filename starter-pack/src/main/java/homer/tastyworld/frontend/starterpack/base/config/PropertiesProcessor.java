package homer.tastyworld.frontend.starterpack.base.config;

import homer.tastyworld.frontend.starterpack.base.AppLogger;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

class PropertiesProcessor {

    private static final AppLogger logger = AppLogger.getFor(PropertiesProcessor.class);
    private final Properties properties = new Properties();
    private final String filePath;

    public PropertiesProcessor(InputStream input) {
        try {
            properties.load(input);
        } catch (IOException ignored) {}
        filePath = null;
    }

    public PropertiesProcessor(String filePath) {
        try (FileInputStream input = new FileInputStream(filePath)) {
            properties.load(input);
        } catch (IOException ignored) {}
        this.filePath = filePath;
    }

    public String getValue(ConfigKey key, String defaultValue) {
        return properties.getProperty(key.propertiesKey, defaultValue);
    }

    public String getValue(ConfigKey key) {
        return getValue(key, null);
    }

    public void setValue(ConfigKey key, String value) {
        properties.setProperty(key.propertiesKey, value);
        save();
    }

    public void deleteValue(ConfigKey key) {
        properties.remove(key.propertiesKey);
        save();
    }

    public void save() {
        if (filePath == null) {
            return;
        }
        try (FileOutputStream output = new FileOutputStream(filePath)) {
            properties.store(output, "TastyWorldApplication");
        } catch (IOException ex) {
            logger.errorOnlyServerNotify("Can't save properties", ex);
        }
    }

}
