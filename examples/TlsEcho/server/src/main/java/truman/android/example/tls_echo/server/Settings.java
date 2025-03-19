package truman.android.example.tls_echo.server;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Settings {

    private static final String PROP_NAME = "server.settings";
    private static final Properties properties;

    static {
        properties = getProperties();
    }

    public static int getPort() {
        int port = 0;
        try {
            String prop = properties.getProperty("server.port");
            if (prop != null) {
                port = Integer.parseInt(prop);
            }
        } catch (NumberFormatException ignored) {}
        return port;
    }

    public static String getKsPassword() {
        String ksPassword = properties.getProperty("server.ks.password");

        return (ksPassword != null) ? ksPassword : "";
    }

    private static Properties getProperties() {
        Properties properties = new Properties();

        try (FileInputStream fis = new FileInputStream(PROP_NAME)) {
            properties.load(fis);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }
}
