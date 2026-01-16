package config;
import org.flywaydb.core.Flyway;
public class FlywayConfig {
    public static void migrate() {

        Flyway flyway = Flyway.configure()
                .dataSource(
                        "jdbc:mysql://localhost:3306/biblioteca?useSSL=false&serverTimezone=UTC",
                        "biblioteca_user",
                        "biblioteca_pass"
                )
                .load();

        flyway.migrate();
    }
}
