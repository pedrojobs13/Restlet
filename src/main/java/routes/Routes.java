package routes;

import controller.*;
import filter.AuthFilter;
import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;

public class Routes extends Application {
    @Override
    public synchronized Restlet createInboundRoot() {
        Router router = new Router(getContext());
        router.attach("/auth/login", AuthController.class);
        router.attach("/auth/register", AuthController.class);

        router.attach("/livros", LivroController.class);
        router.attach("/livros/{id}", LivroController.class);

        router.attach("/autores", AutorController.class);
        router.attach("/autores/{id}", AutorController.class);
        router.attach("/autores/{id}/livros", AutorController.class);

        router.attach("/categorias", CategoriaController.class);
        router.attach("/categorias/{id}", CategoriaController.class);

        router.attach("/usuarios", UsuarioController.class);
        router.attach("/usuarios/{id}", UsuarioController.class);

        AuthFilter authFilter = new AuthFilter(getContext());
        authFilter.setNext(router);

        return authFilter;
    }
}
