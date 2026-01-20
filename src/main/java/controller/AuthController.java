package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.restlet.data.Status;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;
import service.AuthService;

import java.util.Map;

@RequiredArgsConstructor
public class AuthController extends ServerResource {
    private final ObjectMapper mapper;
    private final AuthService authService;

    @Post("json")
    public Representation handleAuth(Representation entity) {
        try {
            String path = getReference().getPath();
            String json = entity.getText();
            Map<String, String> dados = mapper.readValue(json, Map.class);

            if (path.endsWith("/register")) {
                var response = authService.register(dados);
                setStatus(Status.SUCCESS_CREATED);
                return new JacksonRepresentation<>(response);
            }

            if (path.endsWith("/login")) {
                var response = authService.login(dados);
                setStatus(Status.SUCCESS_OK);
                return new JacksonRepresentation<>(response);
            }

            setStatus(Status.CLIENT_ERROR_NOT_FOUND);
            return new JacksonRepresentation<>(Map.of("erro", "Endpoint não encontrado"));

        } catch (IllegalArgumentException e) {
            setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
            return new JacksonRepresentation<>(Map.of("erro", e.getMessage()));

        } catch (SecurityException e) {
            setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
            return new JacksonRepresentation<>(Map.of("erro", e.getMessage()));

        } catch (Exception e) {
            e.printStackTrace();
            setStatus(Status.SERVER_ERROR_INTERNAL);
            return new JacksonRepresentation<>(Map.of("erro", "Erro interno"));
        }
    }
}
