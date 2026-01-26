package config;

import org.flywaydb.core.Flyway;

public class FlywayConfig {
    public static void migrate(String URL, String USER, String PASS) {
        Flyway flyway = Flyway.configure()
                .dataSource(
                        URL,
                        USER,
                        PASS
                )
                .load();

        flyway.migrate();
    }
}
