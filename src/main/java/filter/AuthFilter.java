package filter;

import io.jsonwebtoken.Claims;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Status;
import org.restlet.routing.Filter;
import utils.JWTUtil;

import java.util.Arrays;
import java.util.List;

public class AuthFilter extends Filter {
    private static final List<String> PUBLIC_ROUTES = Arrays.asList(
            "/auth/login",
            "/auth/register"
    );

    private static final List<String> PUBLIC_READ_ROUTES = Arrays.asList(
            "/livros",
            "/autores",
            "/categorias"
    );

    public AuthFilter(Context context) {
        super(context);
    }

    @Override
    protected int beforeHandle(Request request, Response response) {
        String path = request.getResourceRef().getPath();
        String method = request.getMethod().getName();

        if (isPublicRoute(path)) {
            return CONTINUE;
        }

        if ("GET".equals(method) && isPublicReadRoute(path)) {
            return CONTINUE;
        }

        String authHeader = request.getHeaders().getFirstValue("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
            response.setEntity(
                    "{\"erro\": \"Token de autenticação não fornecido. " +
                    "Use o header: Authorization: Bearer <token>\"}",
                    org.restlet.data.MediaType.APPLICATION_JSON
            );
            return STOP;
        }
        String token = authHeader.substring(7);
        Claims claims = JWTUtil.validateToken(token);

        if (claims == null) {
            response.setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
            response.setEntity(
                    "{\"erro\": \"Token inválido ou expirado\"}",
                    org.restlet.data.MediaType.APPLICATION_JSON
            );
            return STOP;
        }

        request.getAttributes().put("userEmail", claims.getSubject());
        request.getAttributes().put("userRole", claims.get("funcao", String.class));

        return CONTINUE;
    }

    private boolean isPublicRoute(String path) {
        for (String route : PUBLIC_ROUTES) {
            if (path.equals(route)) {
                return true;
            }
        }
        return false;
    }

    private boolean isPublicReadRoute(String path) {
        for (String route : PUBLIC_READ_ROUTES) {
            if (path.equals(route) || path.startsWith(route + "/")) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void afterHandle(Request request, Response response) {
        response.getHeaders().add("Access-Control-Allow-Origin", "*");
        response.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.getHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");
    }
}
