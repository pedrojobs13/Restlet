import config.FlywayConfig;
import org.restlet.Component;
import org.restlet.data.Protocol;
import routes.ApiApplication;

public class Main {
    public static void main(String[] args) throws Exception {

        FlywayConfig.migrate();
        Component component = new Component();
        component.getServers().add(Protocol.HTTP, 8080);
        component.getDefaultHost().attach(new ApiApplication());
        component.start();

    }
}
