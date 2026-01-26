import config.FlywayConfig;
import org.restlet.Component;
import org.restlet.data.Protocol;
import routes.Routes;
import utils.EnvUtils;

public class Main {

    public static void main(String[] args) throws Exception {
        EnvUtils.getInstance();
        FlywayConfig.migrate(EnvUtils.getHost(), EnvUtils.getUser(), EnvUtils.getPass());
        Component component = new Component();
        component.getServers().add(Protocol.HTTP, 8080);
        component.getDefaultHost().attach(new Routes());
        component.start();

    }
}
