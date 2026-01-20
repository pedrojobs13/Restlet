package controller;

import lombok.RequiredArgsConstructor;
import org.restlet.data.Status;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import service.AutorLivrosService;

import java.util.Map;


@RequiredArgsConstructor
public class AutorLivrosController extends ServerResource {
    private final AutorLivrosService autorLivrosService;

    @Get("json")
    public Representation listarLivrosDoAutor() {
        try {
            String idStr = getAttribute("id");
            if (idStr == null) {
                setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
                return new JacksonRepresentation<>(Map.of("erro", "ID do autor não fornecido"));
            }

            int autorId = Integer.parseInt(idStr);

            int page = getQueryValue("page") != null
                    ? Integer.parseInt(getQueryValue("page"))
                    : 1;

            int limit = getQueryValue("limit") != null
                    ? Integer.parseInt(getQueryValue("limit"))
                    : 10;

            var response = autorLivrosService.listarLivrosDoAutor(autorId, page, limit);

            setStatus(Status.SUCCESS_OK);
            return new JacksonRepresentation<>(response);

        } catch (IllegalArgumentException e) {
            setStatus(Status.CLIENT_ERROR_NOT_FOUND);
            return new JacksonRepresentation<>(Map.of("erro", e.getMessage()));

        } catch (Exception e) {
            e.printStackTrace();
            setStatus(Status.SERVER_ERROR_INTERNAL);
            return new JacksonRepresentation<>(Map.of("erro", "Erro ao buscar livros do autor"));
        }
    }
}
