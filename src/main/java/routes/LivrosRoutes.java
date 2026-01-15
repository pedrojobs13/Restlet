package routes;

import controller.LivroController;
import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;

public class LivrosRoutes extends Application {

    @Override
    public Restlet createInboundRoot() {
    Router router = new Router(getContext());
        router.attach("/livros", LivroController.class);
        return router;
    }

}
