package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.restlet.data.Status;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import repository.CategoriaRepositoryImpl;
import service.CategoriaService;

import java.util.Map;

public class CategoriaController extends ServerResource {

    private final CategoriaService categoriaService;

    public CategoriaController() {
        this.categoriaService = new CategoriaService(new CategoriaRepositoryImpl());
    }

    @Get("json")
    public Representation listar() {
        try {
            String idStr = getAttribute("id");

            if (idStr != null) {
                var response = categoriaService.buscarPorId(Integer.parseInt(idStr));
                setStatus(Status.SUCCESS_OK);
                return new JacksonRepresentation<>(response);
            }

            var response = categoriaService.listarTodas();
            setStatus(Status.SUCCESS_OK);
            return new JacksonRepresentation<>(response);

        } catch (IllegalArgumentException e) {
            setStatus(Status.CLIENT_ERROR_NOT_FOUND);
            return new JacksonRepresentation<>(Map.of("erro", e.getMessage()));

        } catch (Exception e) {
            e.printStackTrace();
            setStatus(Status.SERVER_ERROR_INTERNAL);
            return new JacksonRepresentation<>(Map.of("erro", "Erro ao buscar categorias"));
        }
    }
}