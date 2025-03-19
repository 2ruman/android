package truman.android.example.tls_echo.client;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Settings {

    private static final String PROP_NAME = "client.settings";
    private static Settings instance;

    private final Properties properties;

    private Settings(Context context) {
        properties = getProperties(context);
    }

    public static synchronized Settings getInstance(Context context) {
        if (instance == null) {
            instance = new Settings(context);
        }
        return instance;
    }

    public String getServerIp() {
        return properties.getProperty("server.ip", "");
    }

    public int getServerPort() {
        int port = 0;
        try {
            String prop = properties.getProperty("server.port");
            if (prop != null) {
                port = Integer.parseInt(prop);
            }
        } catch (NumberFormatException ignored) {}
        return port;
    }

    public String getKsPassword() {
        String ksPassword = properties.getProperty("client.ks.password");
        return (ksPassword != null) ? ksPassword : "";
    }

    private static Properties getProperties(Context context) {
        Properties properties = new Properties();
        try (InputStream is = context.getAssets().open(Settings.PROP_NAME)) {
            properties.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }
}
