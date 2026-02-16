package utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class EnvUtils {

    private static final Properties properties = new Properties();

    static {
        try (InputStream input = EnvUtils.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                System.out.println("Desculpe, não consegui encontrar o arquivo application.properties");
            } else {
                properties.load(input);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static String get(String key) {
        String value = System.getenv(key.toUpperCase().replace(".", "_"));

        if (value == null) {
            value = properties.getProperty(key);
        }
        return value;
    }

    public static String getUser() {
        return get("db.user");
    }

    public static String getPass() {
        return get("db.pass");
    }

    public static String getHost() {
        return get("db.host");
    }

    public static String getJwt() {
        return get("jwt");
    }
    public static long getExpiration() {
        String expirationStr = get("expiration");
        if (expirationStr == null) {
            throw new RuntimeException("Key 'expiration' not found in properties or env vars");
        }
        return Long.parseLong(expirationStr);
    }
}