package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import model.Autor;
import org.restlet.data.Status;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import service.AutorService;

import java.util.Map;

@RequiredArgsConstructor
public class AutorController extends ServerResource {

    private final AutorService autorService;
    private final ObjectMapper mapper;

    @Get("json")
    public Representation listar() {
        try {
            String idStr = getAttribute("id");

            if (idStr != null) {
                Autor autor = autorService.buscarPorId(Integer.parseInt(idStr));
                setStatus(Status.SUCCESS_OK);
                return new JacksonRepresentation<>(autor);
            }

            int page = getQueryValue("page") != null
                    ? Integer.parseInt(getQueryValue("page")) : 1;

            int limit = getQueryValue("limit") != null
                    ? Integer.parseInt(getQueryValue("limit")) : 10;

            String nome = getQueryValue("nome");

            var response = autorService.listar(page, limit, nome);
            setStatus(Status.SUCCESS_OK);
            return new JacksonRepresentation<>(response);

        } catch (IllegalArgumentException e) {
            setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
            return new JacksonRepresentation<>(Map.of("erro", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            setStatus(Status.SERVER_ERROR_INTERNAL);
            return new JacksonRepresentation<>(Map.of("erro", "Erro ao buscar autores"));
        }
    }
}
