package homer.tastyworld.frontend.starterpack.utils.config;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

class PropertiesProcessor {

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

    public String getValue(String key) {
        return properties.getProperty(key);
    }

    public void setValue(String key, String value) {
        properties.setProperty(key, value);
        save();
    }

    public void deleteValue(String key) {
        properties.remove(key);
        save();
    }

    public void save() {
        if (filePath == null) {
            return;
        }
        try (FileOutputStream output = new FileOutputStream(filePath)) {
            properties.store(output, "TastyWorldApplication");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
