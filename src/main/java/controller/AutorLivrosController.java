package controller;

import org.restlet.data.Status;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import repository.AutorRepositoryImpl;
import repository.LivrosRepositoryImpl;
import service.AutorLivrosService;
import utils.ObjectMapperProvider;

import java.util.Map;


public class AutorLivrosController extends ServerResource {
    private final AutorLivrosService autorLivrosService;

    public AutorLivrosController() {
        this.autorLivrosService = new AutorLivrosService(new AutorRepositoryImpl(), new LivrosRepositoryImpl());
    }

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
            JacksonRepresentation<Map<String, Object>> rep =
                    new JacksonRepresentation<>(response);
            rep.setObjectMapper(ObjectMapperProvider.get());

            return rep;

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
