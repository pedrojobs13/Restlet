package utils;

import io.github.cdimascio.dotenv.Dotenv;

public class EnvUtils {
    private static EnvUtils INSTANCE;
    private static Dotenv dotenv;

    private EnvUtils() {
    }

    public static EnvUtils getInstance() {
        if (INSTANCE == null) {
            dotenv = Dotenv.load();
            INSTANCE = new EnvUtils();
        }

        return INSTANCE;
    }

    public static String getUser() {
        return dotenv.get("DB_USER");
    }

    public static String getPass() {
        return dotenv.get("DB_PASS");
    }

    public static String getHost() {
        return dotenv.get("DB_HOST");
    }
}
